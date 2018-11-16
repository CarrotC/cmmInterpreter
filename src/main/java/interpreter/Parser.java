package interpreter;

import net.sf.json.*;
import net.sf.json.util.PropertyFilter;
import util.JsonFormatTool;
import vo.Token;
import vo.TreeNode;


import java.util.List;

public class Parser {
    private List<Token> tokenList;
    private Token curToken;
    private int tokenIndex = 0;

    private TreeNode programNode;
    private TreeNode curNode;

    private String errorInfo = "";

    public Parser(List<Token> tokenList){
        this.tokenList = tokenList;
    }

    public TreeNode getProgramNode() {
        return this.programNode;
    }

    public String grammaticalAnalysis(){
        String result = "";
        this.parse();
        TreeNode treeNode = this.programNode;
        traversalNode(treeNode);
        result += this.errorInfo;
        result += JsonFormatTool.formatJson(treeNode.getJson());

        return result;
    }

    //遍历树，将树节点转换为json对象并存储在相应属性中
    public void traversalNode(TreeNode treeNode){
        if (treeNode.getType() == TreeNode.TERMINAL_SYMBOL){
            toJson(treeNode);
        }
        else {
            for(TreeNode tn : treeNode.getChildren()){
                traversalNode(tn);
            }
            toJson(treeNode);
        }
    }

    //转换成json对象
    public void toJson(TreeNode obj){
        JsonConfig cfg = new JsonConfig();
        //这里是把关联对象剔除掉
        cfg.setJsonPropertyFilter(new PropertyFilter()
        {
            public boolean apply(Object source, String name, Object value) {
                if(name.equals("parentNode") || name.equals("json") || name.equals("type") || name.equals("value")) {//name.equals("parentNode")||
                    return true;
                } else {
                    return false;
                }
            }
        });
        JSONObject jsonObject=JSONObject.fromObject(obj, cfg);
        String json = jsonObject.toString();
        obj.setJson(json);
    }

    private void parse(){
        if(tokenList.size() > 0){
            curToken = tokenList.get(0);
            programNode = new TreeNode(TreeNode.PROGRAM, null, "program");
            curNode = programNode;
            parseProgram();
        }else {
            //报错：程序为空
            errorInfo += "error: 程序为空\n";
        }
    }

    private void parseProgram(){
        while (curToken.getType() != Token.END_SIGN){
            if(curToken.getType() >= 0 && curToken.getType() <= 7){//保留字或标识符
                parseStmtSequence();
            }else {
                //报错
                errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: block start word\n";
                break;
            }
        }
    }

    private void parseStmtSequence(){
        addNon_TerMinalNode(TreeNode.STMT_SEQUENCE, "stmt-sequence");
        if(curToken.getType() >= 0 && curToken.getType() <= 7){//保留字或标识符
            parseStatement();
        }
        else if(curToken.getType() == Token.END_SIGN || curToken.getType() == Token.RIGHT_BRACE) {//# 或 }
            //空处理
            curNode = curNode.getParentNode();
            return;
        }
        else {
            //报错
            errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: reserved word or identifier\n";
            curNode = curNode.getParentNode();
            return;
        }
        parseStmtSequence();
    }

    private void parseStatement(){
        addNon_TerMinalNode(TreeNode.STATEMENT, "statement");
        switch (curToken.getType()){
            case Token.IF:
                parseIfStmt();
                break;
            case Token.WHILE:
                parseWhileStmt();
                break;
            case Token.READ:
                parseReadStmt();
                break;
            case Token.WRITE:
                parseWriteStmt();
                break;
            case Token.INT:
                parseIntStmt();
                break;
            case Token.REAL:
                parseRealStmt();
                break;
            case Token.IDENTIFIER:
                parseAssignStmt();
                break;
            default:
                //TODO：报错
                errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: reserved word or identifier\n";
        }

        curNode = curNode.getParentNode();
    }

    private void parseIfStmt(){
        addNon_TerMinalNode(TreeNode.IF_STMT, "if-stmt");

        matchToken(Token.IF);//if
        matchToken(Token.LEFT_PARENTHESE);//(
        parseCondition();//condition
        matchToken(Token.RIGHT_PARENTHESE);//)
        matchToken(Token.LEFT_BRACE);//{
        parseStmtSequence();
        matchToken(Token.RIGHT_BRACE);
        parseElseStmt();

        curNode = curNode.getParentNode();
    }

    private void parseElseStmt(){
        addNon_TerMinalNode(TreeNode.ELSE_STMT, "else-stmt");

        if (curToken.getType() == Token.ELSE){
            matchToken(Token.ELSE);//else
            matchToken(Token.LEFT_BRACE);//{
            parseStmtSequence();
            matchToken(Token.RIGHT_BRACE);//}
        }else {
            //else语句为空
        }

        curNode = curNode.getParentNode();
    }

    private void parseWhileStmt(){
        addNon_TerMinalNode(TreeNode.WHILE_STMT, "while-stmt");

        matchToken(Token.WHILE);//while
        matchToken(Token.LEFT_PARENTHESE);//(
        parseCondition();
        matchToken(Token.RIGHT_PARENTHESE);//)
        matchToken(Token.LEFT_BRACE);//{
        parseStmtSequence();
        matchToken(Token.RIGHT_BRACE);//}

        curNode = curNode.getParentNode();
    }

    private void parseReadStmt(){
        addNon_TerMinalNode(TreeNode.READ_STMT, "read-stmt");

        matchToken(Token.READ);//read
        parseVariable();//variable
        matchToken(Token.SEMICOLON);//;

        curNode = curNode.getParentNode();
    }

    private void parseWriteStmt(){
        addNon_TerMinalNode(TreeNode.WRITE_STMT, "write-stmt");

        matchToken(Token.WRITE);//write
        parseFactor();//factor
        matchToken(Token.SEMICOLON);//;

        curNode = curNode.getParentNode();
    }

    private void parseIntStmt(){
        addNon_TerMinalNode(TreeNode.INT_STMT, "int-stmt");

        matchToken(Token.INT);//int
        parseVariable();//variable
        parseIntFollow();

        curNode = curNode.getParentNode();
    }

    private void parseIntFollow(){
        addNon_TerMinalNode(TreeNode.INT_FOLLOW, "int-follow");

        if(curToken.getType() == Token.SEMICOLON){//;
            matchToken(Token.SEMICOLON);//;
        }
        else if(curToken.getType() == Token.ASSIGNMENT){
            matchToken(Token.ASSIGNMENT);//=
            parseExpression();//expression
            matchToken(Token.SEMICOLON);
        }
        else {
            //报错
            errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: ; or =\n";
        }

        curNode = curNode.getParentNode();
    }

    private void parseRealStmt(){
        addNon_TerMinalNode(TreeNode.REAL_STMT, "real-stmt");

        matchToken(Token.REAL);//real
        parseVariable();//variable
        parseRealFollow();

        curNode = curNode.getParentNode();
    }

    private void parseRealFollow(){
        addNon_TerMinalNode(TreeNode.REAL_FOLLOW, "real-follow");

        if(curToken.getType() == Token.SEMICOLON){//;
            matchToken(Token.SEMICOLON);
        }
        else if (curToken.getType() == Token.ASSIGNMENT){
            matchToken(Token.ASSIGNMENT);//=
            parseExpression();//expression
            matchToken(Token.SEMICOLON);//;
        }else {
            errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: ; or =\n";
        }

        curNode = curNode.getParentNode();
    }

    private void parseAssignStmt(){
        addNon_TerMinalNode(TreeNode.ASSIGN_STMT, "assign-stmt");

        parseVariable();//variable
        matchToken(Token.ASSIGNMENT);//=
        parseExpression();//expression
        matchToken(Token.SEMICOLON);

        curNode = curNode.getParentNode();
    }

    private void parseCondition(){
        addNon_TerMinalNode(TreeNode.CONDITION, "condition");

        parseExpression();//expression
        parseComparisonOp();//comparison-op
        parseExpression();//expression

        curNode = curNode.getParentNode();
    }

    private void parseComparisonOp(){
        addNon_TerMinalNode(TreeNode.COMPARISON_OP, "comparison-op");

        switch (curToken.getType()){
            case Token.INEQUALITY_SIGN:
                matchToken(Token.INEQUALITY_SIGN);
                break;
            case Token.EQUALITY_SIGN:
                matchToken(Token.EQUALITY_SIGN);
                break;
            case Token.LESS_THAN:
                matchToken(Token.LESS_THAN);
                break;
            default:
                errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: comparison operation\n";
                break;
        }

        curNode = curNode.getParentNode();
    }

    private void parseExpression(){
        addNon_TerMinalNode(TreeNode.EXPRESSION, "expression");

        parseTerm();
        parseAndTerm();

        curNode = curNode.getParentNode();
    }

    private void parseAndTerm(){
        switch (curToken.getType()){
            case Token.ADD:
            case Token.MINUS:
                addNon_TerMinalNode(TreeNode.AND_TERM, "and-term");
                parseAddOp();
                parseTerm();
                parseAndTerm();
                curNode = curNode.getParentNode();
                break;
            case Token.END_SIGN:
            case Token.SEMICOLON:
            case Token.INEQUALITY_SIGN:
            case Token.EQUALITY_SIGN:
            case Token.LESS_THAN:
            case Token.LEFT_PARENTHESE:
            case Token.RIGHT_PARENTHESE:
//                getPreToken();
                break;
            default:
                //报错
//                getPreToken();
//                errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: add or minus operation\n";
                break;
        }

    }

    private void parseAddOp(){
        addNon_TerMinalNode(TreeNode.ADD_OP, "add-op");
        if (curToken.getType() == Token.ADD){
            matchToken(Token.ADD);
        }
        else if (curToken.getType() == Token.MINUS){
            matchToken(Token.MINUS);
        }
        else {
            //报错
            errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: add or minus operation\n";
        }

        curNode = curNode.getParentNode();
    }

    private void parseTerm(){
        addNon_TerMinalNode(TreeNode.TERM, "term");

        parseFactor();
        parseWithFactor();

        curNode = curNode.getParentNode();
    }

    private void parseWithFactor(){
        switch (curToken.getType()){
            case Token.MULTIPLY:
            case Token.DIVIDE:
                addNon_TerMinalNode(TreeNode.WITH_FACTOR, "with-factor");//非空时添加
                parseMulOp();
                parseFactor();
                parseWithFactor();
                curNode = curNode.getParentNode();
                break;
            case Token.END_SIGN:
            case Token.ADD:
            case Token.MINUS:
//                getPreToken();
                break;
            default:
                //报错
//                getPreToken();
//                errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: multiply or devide operation\n";
                break;
        }

    }

    private void parseMulOp(){
        addNon_TerMinalNode(TreeNode.MUL_OP, "mul-op");
        if (curToken.getType() == Token.MULTIPLY){
            matchToken(Token.MULTIPLY);
        }
        else if (curToken.getType() == Token.DIVIDE){
            matchToken(Token.DIVIDE);
        }
        else {
            //报错
            errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: multiply or devide operation\n";
        }

        curNode = curNode.getParentNode();
    }

    private void parseFactor(){
        addNon_TerMinalNode(TreeNode.FACTOR, "factor");
        switch (curToken.getType()){
            case Token.LEFT_PARENTHESE:
                matchToken(Token.LEFT_PARENTHESE);
                parseExpression();
                matchToken(Token.RIGHT_PARENTHESE);
                break;
            case Token.INT_NUM:
                matchToken(Token.INT_NUM);
                break;
            case Token.REAL_NUM:
                matchToken(Token.REAL_NUM);
                break;
            case Token.IDENTIFIER:
                parseVariable();
                break;
            default:
                //报错
                errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: number or identifier\n";
                break;
        }

        curNode = curNode.getParentNode();
    }

    private void parseVariable(){
        addNon_TerMinalNode(TreeNode.VARIABLE, "variable");

        matchToken(Token.IDENTIFIER);
        parseArrayIndex();
        curNode = curNode.getParentNode();
    }

    private void parseArrayIndex(){
        switch (curToken.getType()){
            case Token.LEFT_BRACKET:
                addNon_TerMinalNode(TreeNode.ARRAY_INDEX, "arrayIndex");
                matchToken(Token.LEFT_BRACKET);
                matchToken(Token.INT_NUM);
                matchToken(Token.RIGHT_BRACKET);
                curNode = curNode.getParentNode();
                break;
            case Token.END_SIGN:
            case Token.SEMICOLON:
            case Token.ASSIGNMENT:
//                getPreToken();
                break;//右产生式为空
            default:
                //报错
//                getPreToken();
                break;
        }
    }


    private void addNon_TerMinalNode(int type, String name){//添加非终结符结点
        TreeNode childNode = new TreeNode(type, curNode, name);
        childNode.setParentNode(curNode);
        curNode.getChildren().add(childNode);
        curNode = childNode;//前驱遍历
    }

    public void addTerminalNode(Token token){//添加终结符结点
        TreeNode childNode = new TreeNode(TreeNode.TERMINAL_SYMBOL, token, curNode, token.getStrValue());
        curNode.getChildren().add(childNode);
    }

    private void matchToken(int type){
        if (curToken.getType() == type){
            addTerminalNode(curToken);
        }else {
            //报错，终结符不符合要求
            getPreToken();
            switch (type){
                case Token.SEMICOLON:
                    errorInfo += "error: line" + curToken.getLine() + "," + " after\"" + curToken.getStrValue() + "\", expect: ;\n";
                    break;
                case Token.LEFT_PARENTHESE:
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected：(\n";
                    break;
                case Token.RIGHT_PARENTHESE:
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected：)\n";
                    break;
                case Token.LEFT_BRACKET:
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected：[\n";
                    break;
                case Token.RIGHT_BRACKET:
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: ]\n";
                    break;
                case Token.LEFT_BRACE:
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected：{\n";
                    break;
                case Token.RIGHT_BRACE:
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: }\n";
                    break;
                case Token.ASSIGNMENT:
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: =\n";
                    break;
                case Token.INT_NUM:
                    getNextToken();
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: int number\n";
                    break;
                case Token.REAL_NUM:
                    getNextToken();
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + "expected: real number\n";
                    break;
                default:
                    errorInfo += "error: line" + curToken.getLine() + "," + " column" + curToken.getColumn() + ": unexpected: " + curToken.getStrValue() +"\n";
            }
        }
        getNextToken();
    }

    private void getNextToken(){
        tokenIndex++;//读取下一个token
        if(tokenIndex < tokenList.size()){
            curToken = tokenList.get(tokenIndex);
        }else {
            //TODO：结束

        }
    }

    private void getPreToken(){
        tokenIndex--;//读取上一个token
        curToken = tokenList.get(tokenIndex);
    }


    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}

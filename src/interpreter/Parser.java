package interpreter;

import sun.reflect.generics.tree.Tree;
import vo.Token;
import vo.TreeNode;

import java.util.List;
import java.util.Map;

public class Parser {
    private List<Token> tokenList;
    private Token curToken;
    private int tokenIndex = 0;

    private TreeNode programNode;
    private TreeNode curNode;

    public void parse(){
        if(tokenList.size() > 0){
            curToken = tokenList.get(0);
            programNode = new TreeNode(TreeNode.PROGRAM);
            curNode = programNode;
            parseProgram();
        }else {
            //报错：程序为空
        }
    }

    public void parseProgram(){
        while (curToken.getType() != Token.END_SIGN){
            if(curToken.getType() >= 0 && curToken.getType() <= 7){//保留字或标识符
                parseStmtSequence();
            }else {
                //TODO:报错
            }
        }
    }

    public void parseStmtSequence(){
        addNon_TerMinalNode(TreeNode.STMT_SEQUENCE);
        if(curToken.getType() >= 0 && curToken.getType() <= 7){//保留字或标识符
            parseStatement();
        }
        else if(curToken.getType() == Token.END_SIGN || curToken.getType() == Token.RIGHT_BRACE) {//# 或 }
            //TODO:空处理
        }
        else {
            //TODO：报错
        }
    }

    public void parseStatement(){
        addNon_TerMinalNode(TreeNode.STATEMENT);
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

        }
    }

    public void parseIfStmt(){
        addNon_TerMinalNode(TreeNode.IF_STMT);

        matchToken(Token.IF);//if
        matchToken(Token.LEFT_PARENTHESE);//(
        parseCondition();//condition
        matchToken(Token.RIGHT_PARENTHESE);//)
        matchToken(Token.LEFT_BRACE);//{
        parseStmtSequence();
        matchToken(Token.RIGHT_BRACE);
        parseElseStmt();
    }

    public void parseElseStmt(){
        addNon_TerMinalNode(TreeNode.ELSE_STMT);

        if (curToken.getType() == Token.ELSE){
            matchToken(Token.ELSE);//else
            matchToken(Token.LEFT_BRACE);//{
            parseStmtSequence();
            matchToken(Token.RIGHT_BRACE);//}
        }else {
            //TODO：else语句为空
        }

    }

    public void parseWhileStmt(){
        addNon_TerMinalNode(TreeNode.WHILE_STMT);

        matchToken(Token.WHILE);//while
        matchToken(Token.LEFT_PARENTHESE);//(
        parseCondition();
        matchToken(Token.RIGHT_PARENTHESE);//)
        matchToken(Token.LEFT_BRACE);//{
        parseStmtSequence();
        matchToken(Token.RIGHT_BRACE);//}

    }

    public void parseReadStmt(){
        addNon_TerMinalNode(TreeNode.READ_STMT);

        matchToken(Token.READ);//read
        parseVariable();//variable
        matchToken(Token.SEMICOLON);//;
    }

    public void parseWriteStmt(){
        addNon_TerMinalNode(TreeNode.WRITE_STMT);

        matchToken(Token.WRITE);//write
        parseFactor();//factor
        matchToken(Token.SEMICOLON);//;
    }

    public void parseIntStmt(){
        addNon_TerMinalNode(TreeNode.INT_STMT);

        matchToken(Token.INT);//int
        parseVariable();//variable
        parseIntFollow();
    }

    public void parseIntFollow(){
        addNon_TerMinalNode(TreeNode.INT_FOLLOW);

        if(curToken.getType() == Token.SEMICOLON){//;
            matchToken(Token.SEMICOLON);//;
        }
        else if(curToken.getType() == Token.ASSIGNMENT){
            matchToken(Token.ASSIGNMENT);//=
            parseExpression();//expression
            matchToken(Token.SEMICOLON);
        }
        else {
            //TODO：报错
        }
    }

    public void parseRealStmt(){
        addNon_TerMinalNode(TreeNode.REAL_STMT);

        matchToken(Token.REAL);//real
        parseVariable();//variable
        parseRealFollow();
    }

    public void parseRealFollow(){
        addNon_TerMinalNode(TreeNode.REAL_FOLLOW);

        if(curToken.getType() == Token.SEMICOLON){//;
            matchToken(Token.SEMICOLON);
        }
        else if (curToken.getType() == Token.ASSIGNMENT){
            matchToken(Token.ASSIGNMENT);//=
            parseExpression();//expression
            matchToken(Token.SEMICOLON);//;
        }
    }

    public void parseAssignStmt(){
        addNon_TerMinalNode(TreeNode.ASSIGN_STMT);

        parseVariable();//variable
        matchToken(Token.ASSIGNMENT);//=
        parseExpression();//expression
        matchToken(Token.SEMICOLON);

    }

    public void parseCondition(){
        addNon_TerMinalNode(TreeNode.CONDITION);

        parseExpression();//expression
        parseComparisonOp();//comparison-op
        parseExpression();//expression
    }

    public void parseComparisonOp(){
        addNon_TerMinalNode(TreeNode.COMPARISON_OP);

        switch (curToken.getType()){
            case Token.INEQUALITY_SIGN:
                matchToken(Token.INEQUALITY_SIGN);
            case Token.EQUALITY_SIGN:
                matchToken(Token.EQUALITY_SIGN);
            case Token.LESS_THAN:
                matchToken(Token.LESS_THAN);

        }
    }

    public void parseExpression(){
        addNon_TerMinalNode(TreeNode.EXPRESSION);

        parseTerm();
        parseAndTerm();
    }

    public void parseAndTerm(){
        switch (curToken.getType()){
            case Token.ADD:
            case Token.MINUS:
                addNon_TerMinalNode(TreeNode.AND_TERM);
                parseAddOp();
                parseTerm();
                parseAndTerm();
                break;
            case Token.END_SIGN:
            case Token.SEMICOLON:
            case Token.INEQUALITY_SIGN:
            case Token.EQUALITY_SIGN:
            case Token.LESS_THAN:
            case Token.LEFT_PARENTHESE:
            case Token.RIGHT_PARENTHESE:
                break;
            default:
                //TODO：报错
                break;
        }
    }

    public void parseAddOp(){
        addNon_TerMinalNode(TreeNode.ADD_OP);
        if (curToken.getType() == Token.ADD){
            matchToken(Token.ADD);
        }
        else if (curToken.getType() == Token.MINUS){
            matchToken(Token.MINUS);
        }
        else {
            //TODO：报错
        }
    }

    public void parseTerm(){
        addNon_TerMinalNode(TreeNode.TERM);

        parseFactor();
        parseWithFactor();
    }

    public void parseWithFactor(){
        switch (curToken.getType()){
            case Token.MULTIPLY:
            case Token.DIVIDE:
                addNon_TerMinalNode(TreeNode.WITH_FACTOR);//非空时添加
                parseMulOp();
                parseFactor();
                parseWithFactor();
                break;
            case Token.END_SIGN:
            case Token.ADD:
            case Token.MINUS:
                break;
            default:
                //TODO：报错
                break;
        }

    }

    public void parseMulOp(){
        addNon_TerMinalNode(TreeNode.MUL_OP);
        if (curToken.getType() == Token.MULTIPLY){
            matchToken(Token.MULTIPLY);
        }
        else if (curToken.getType() == Token.DIVIDE){
            matchToken(Token.DIVIDE);
        }
        else {
            //TODO：报错
        }

    }

    public void parseFactor(){
        addNon_TerMinalNode(TreeNode.FACTOR);
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
                //TODO：报错
                break;
        }

    }

    public void parseVariable(){
        addNon_TerMinalNode(TreeNode.VARIABLE);

        matchToken(Token.IDENTIFIER);
        parseArrayIndex();
    }

    public void parseArrayIndex(){
        switch (curToken.getType()){
            case Token.LEFT_BRACKET:
                addNon_TerMinalNode(TreeNode.ARRAY_INDEX);
                matchToken(Token.LEFT_BRACKET);
                matchToken(Token.INT_NUM);
                matchToken(Token.RIGHT_BRACKET);
                break;
            case Token.END_SIGN:
            case Token.SEMICOLON:
            case Token.ASSIGNMENT:
                break;//右产生式为空
            default:
                //TODO：报错
                break;
        }
    }


    public void addNon_TerMinalNode(int type){//添加非终结符结点
        TreeNode childNode = new TreeNode(type);
        curNode.getChildren().add(childNode);
        curNode = childNode;//前驱遍历
    }

    public void addTerminalNode(Token token){//添加终结符结点
        TreeNode childNode = new TreeNode(TreeNode.TERMINAL_SYMBOL, token);
        curNode.getChildren().add(childNode);
    }

    public void matchToken(int type){
        if (curToken.getType() == type){
            addTerminalNode(curToken);
            tokenIndex++;//读取下一个token
            if(tokenIndex < tokenList.size()){
                curToken = tokenList.get(tokenIndex);
            }else {
                //TODO：结束
            }
        }else {
            //TODO：报错，终结符不符合要求
        }
    }



}

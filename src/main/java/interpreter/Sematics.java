package interpreter;

import com.sun.org.apache.regexp.internal.RE;
import vo.Record;
import vo.SimbolTabble;
import vo.Token;
import vo.TreeNode;

import java.util.Scanner;

public class Sematics {

    private TreeNode programNode;
    private SimbolTabble simbolTabble;
    private TreeNode currNode;
    private Token currToken;
    private int level = 0;
    private String result = "\n";
    private String errorInfo = "";

    public Sematics(TreeNode programNode){
        this.programNode = programNode;
        this.simbolTabble = new SimbolTabble();
    }

    public String sematicAnalyse(){
        currNode = programNode;
        analyseProgram();
        if (errorInfo != ""){
            result = "Error: \n" + errorInfo + result;
        }
        return result;
    }

    public void analyseProgram(){
        currNode = currNode.getChildren().get(0);//stmt-sequence
        analyseStmtSequence();
    }

    public void analyseStmtSequence(){//statement stmt-sequence
        currNode = currNode.getChildren().get(0);//statement
        analyseStatement();
        currNode = currNode.getParentNode();//stmt-sequence
        if(currNode.getChildren().size() > 1){//如果statement后的smt-sequence不为空
            currNode = currNode.getChildren().get(1);//stmt-sequence(2）
            analyseStmtSequence();
            currNode = currNode.getParentNode();//stmtment(1)
        }
    }

    public void analyseStatement(){
        currNode = currNode.getChildren().get(0);
        switch (currNode.getType()){
            case TreeNode.IF_STMT:
                analyseIfStmt();
                break;
            case TreeNode.ELSE_STMT:
                analyseElseStmt();
                break;
            case TreeNode.WHILE_STMT:
                analyseWhileStmt();
                break;
            case TreeNode.READ_STMT:
                analyseReadStmt();
                break;
            case TreeNode.WRITE_STMT:
                analyseWriteStmt();
                break;
            case TreeNode.INT_STMT:
                analyseIntStmt();
                break;
            case TreeNode.REAL_STMT:
                analyseRealStmt();
                break;
            case TreeNode.ASSIGN_STMT:
                analyseAssignStmt();
                break;
        }
        currNode = currNode.getParentNode();
    }

    public void analyseIfStmt(){//if (condition) {stmt-sequence} else-stmt
        level++;
        currNode = currNode.getChildren().get(2);//condition
        if(analyseCondition()){
            currNode = currNode.getParentNode();//if-stmt
            currNode = currNode.getChildren().get(5);//stmt-sequence
            analyseStmtSequence();
            currNode = currNode.getParentNode();//if-stmt
        }
        else {
            currNode = currNode.getParentNode();//if-stmt
            if(currNode.getChildren().size() > 7){//如果存在else语句
                currNode = currNode.getChildren().get(7);//else-stmt
                analyseElseStmt();
                currNode = currNode.getParentNode();//if-stmt
            }
        }
        simbolTabble.deleteInsideRecord(level);
        level--;
    }

    public void analyseElseStmt(){//else{stmt-sequence}
        level++;
        currNode = currNode.getChildren().get(2);//stmt-sequence
        analyseStmtSequence();
        currNode = currNode.getParentNode();//else-stmt
        simbolTabble.deleteInsideRecord(level);
        level--;
    }

    public void analyseWhileStmt(){//while(condition){stmt-sequence}
        level++;
        currNode = currNode.getChildren().get(2);//condition
        while (analyseCondition()){
            currNode = currNode.getParentNode();//while-stmt
            currNode = currNode.getChildren().get(5);//stmt-sequence
            analyseStmtSequence();
            currNode = currNode.getParentNode();//while-stmt
            currNode = currNode.getChildren().get(2);//condition
        }
        currNode = currNode.getParentNode();//while-stmt
        simbolTabble.deleteInsideRecord(level);
        level--;
    }

    public void analyseReadStmt(){//read variable;
        currNode = currNode.getChildren().get(1);//variable
        Record var = analyseVariable();
        if(var == null){//如果该变量没有被声明过//, currNode.getChildren().get(0): variable->idetifier arrayIndex, identifier是终结点，有token
            String name =  currNode.getChildren().get(0).getValue().getStrValue();
            Record record = new Record(level, null, Record.tReal,name, 0.0);
            simbolTabble.getTable().add(record);
        }
        var = analyseVariable();//获取声明的变量
        Scanner sc = new Scanner(System.in);
        String readStr = sc.next();
        double readRealVar = Double.parseDouble(readStr);
        var.setRealValue(readRealVar);
        result+= "read: " + readStr +"\n";
        currNode = currNode.getParentNode();//read-stmt
    }

    public void analyseWriteStmt(){//write factor;
        currNode = currNode.getChildren().get(1);//factor
        double factor2Write = analyseFactor();
        System.out.println(factor2Write);
        result+= "write: " + factor2Write + "\n";
        currNode = currNode.getParentNode();
    }

    public void analyseIntStmt(){//int variable int-follow
        currNode = currNode.getChildren().get(1);//vaiable
        Record var = analyseVariable();
        if(var == null){//如果该变量未声明过
            String name =  currNode.getChildren().get(0).getValue().getStrValue();
            if(currNode.getChildren().size() == 1){//variable: identifier
                var = new Record(level, currNode.getValue(), Record.tInt, name, 0);
            }else {//数组
                int arrayNum = Integer.parseInt(currNode.getChildren().get(1).getChildren().get(1).getValue().getStrValue());
                var = new Record(level, currNode.getValue(), Record.tIntArray, name, new int[arrayNum]);
            }
            simbolTabble.getTable().add(var);
            currNode = currNode.getParentNode();//int-stmt
            currNode = currNode.getChildren().get(2);//intFollow
            analyseIntFollowStmt(name);
            currNode = currNode.getParentNode();//int-stmt
        }else {
            errorInfo += "重复声明变量：line " + currNode.getChildren().get(0).getValue().getLine()
                    + ", column " + currNode.getChildren().get(0).getValue().getColumn()
                    + " " + currNode.getChildren().get(0).getValue().getStrValue() + "\n";
            currNode = currNode.getParentNode();//int-stmt
        }
    }

    public void analyseIntFollowStmt(String name){//;或=expression;
        if(currNode.getChildren().size() <= 1){//;
            return;
        }else {
            Record record = simbolTabble.getRecordByName(name);
            if (record.getType() == Record.tIntArray){//数组
                errorInfo += "错误的数组声明方式： line " + currNode.getChildren().get(0).getValue().getLine()
                        + ", column " + (currNode.getChildren().get(0).getValue().getColumn() - 1)
                        + " int" + record.getName() + "[" + record.getArrayNum() + "] = " + "\n";
                return;
            }
            currNode = currNode.getChildren().get(1);//expression
            int intVar = (int) analyseExpression();
            record.setIntValue(intVar);
            currNode = currNode.getParentNode();//intFollow
        }
    }

    public void analyseRealStmt(){//real variable real-follow
        currNode = currNode.getChildren().get(1);//variable
        Record var = analyseVariable();
        if(var == null){//变量未被声明过
            String name =  currNode.getChildren().get(0).getValue().getStrValue();
            if(currNode.getChildren().size() == 1){//普通int变量
                var = new Record(level, null, Record.tReal, name, 0.0);
            }else{//数组变量
                int arrayNum = Integer.parseInt(currNode.getChildren().get(1).getChildren().get(1).getValue().getStrValue());
                var = new Record(level, null, Record.tRealArray, name, new int[arrayNum]);
            }
            simbolTabble.getTable().add(var);
            currNode = currNode.getParentNode();//real-stmt
            currNode = currNode.getChildren().get(2);//real-follow
            analyseRealFollowStmt(name);
            currNode = currNode.getParentNode();
        }else {//变量已经被声明
            errorInfo+= "重复声明变量：line " + currNode.getChildren().get(0).getValue().getLine()
                    + ", column " + currNode.getChildren().get(0).getValue().getColumn()
                    + " " + currNode.getChildren().get(0).getValue().getStrValue() + "\n";
            currNode = currNode.getParentNode();//real-stmt
        }
    }

    public void analyseRealFollowStmt(String name){//;或=expression;
        if(currNode.getChildren().size() > 1){//=expression;
            Record record = simbolTabble.getRecordByName(name);
            if (record.getType() == Record.tRealArray){//数组不能直接定义值
                errorInfo+= "错误的数组声明方式：line" + currNode.getChildren().get(0).getValue().getLine()
                        + ", column " + (currNode.getChildren().get(0).getValue().getColumn() - 1)
                        + " real" + record.getName() + "[" + record.getArrayNum() + "] = \n";

            }
            currNode = currNode.getChildren().get(1);//expression
            double realVar = analyseExpression();
            record.setRealValue(realVar);
            currNode = currNode.getParentNode();//realFollow
        }
    }

    public void analyseAssignStmt(){//variable = expression;
        currNode = currNode.getChildren().get(2);//expression
        double exp = analyseExpression();
        currNode = currNode.getParentNode();
        currNode = currNode.getChildren().get(0);//variable
        Record var = analyseVariable();
        if (var == null){//变量未声明
            errorInfo += "未声明的变量： line " + currNode.getChildren().get(0).getValue().getLine()
                    + ", column " + currNode.getChildren().get(0).getValue().getColumn()
                    + " " + currNode.getChildren().get(0).getValue().getStrValue() + "\n";
        }else {
            if(currNode.getChildren().size() > 1){//variable: identifier arrayIndex
                int arrayType = var.getType();
                if(arrayType == Record.tIntArray || arrayType == Record.tRealArray){
                    if (arrayType == Record.tIntArray){
                        var.setIntValue((int) exp);
                    }else {
                        var.setRealValue(exp);
                    }
                }
            }else {//variable: identifier
                if (var.getType() == Record.tInt){
                    var.setIntValue((int) exp);
                }else {
                    var.setRealValue(exp);
                }
            }
        }
        currNode = currNode.getParentNode();//assign-stmt
    }

    public boolean analyseCondition(){//expression comparison-op expresion
        level++;
        currNode = currNode.getChildren().get(0);//expression(1)
        double leftExp = analyseExpression();
        currNode = currNode.getParentNode();//condition

        currNode = currNode.getChildren().get(1);//comparison-op
        int operation = analyseComparisonOp();
        currNode = currNode.getParentNode();//condition

        currNode = currNode.getChildren().get(2);//expression(2)
        double rightExp = analyseExpression();
        currNode = currNode.getParentNode();
        level--;
        switch (operation){
            case Token.LESS_THAN:
                return leftExp < rightExp;
            case Token.EQUALITY_SIGN:
                return leftExp == rightExp;
            case Token.INEQUALITY_SIGN:
                return leftExp != rightExp;
            default:
                return false;
        }
    }
    public int analyseComparisonOp(){// < 或 <> 或 ==
        return currNode.getChildren().get(0).getValue().getType();
    }
    public double analyseExpression(){//term and-term
        currNode = currNode.getChildren().get(0);//term
        double exp;
        double term = analyseTerm();
        exp = term;
        currNode = currNode.getParentNode();//expression
        if(currNode.getChildren().size() > 1){//and-term 不为空
            currNode = currNode.getChildren().get(1);//and-term
            exp = analyseAddTerm(term);
            currNode = currNode.getParentNode();
        }
        return exp;
    }
    public double analyseAddTerm(double term){//addop term and-term （注：and-term可能为空）
        double changedTerm;
        currNode = currNode.getChildren().get(0);//addop
        int addOpType = analyseAddOp();
        currNode = currNode.getParentNode();//addTerm
        currNode = currNode.getChildren().get(1);//term
        double term2 = analyseTerm();
        if (addOpType == Token.ADD){
            changedTerm = term + term2;
        }else {
            changedTerm = term - term2;
        }
        currNode = currNode.getParentNode();
        if (currNode.getChildren().size() > 2){
            changedTerm = analyseAddTerm(changedTerm);
        }
        return changedTerm;
    }
    public int analyseAddOp(){
        return currNode.getChildren().get(0).getValue().getType();
    }

    public double analyseTerm(){//factor with-factor （注：with-factor可能为空）
        double term;
        currNode = currNode.getChildren().get(0);//factor
        double factor = analyseFactor();
        term = factor;
        currNode = currNode.getParentNode();//term
        if(currNode.getChildren().size() > 1){//with-factor不为空
            currNode = currNode.getChildren().get(1);//with-factor
            term = analyseWithFactor(factor);
            currNode = currNode.getParentNode();
        }
        return term;
    }
    public double analyseWithFactor(double factor){//mulop factor with-factor
        double changedFactor = 0;
        currNode = currNode.getChildren().get(0);
        int mulOpType = analyseMulOp();
        currNode = currNode.getParentNode();//withFactor
        currNode = currNode.getChildren().get(1);//factor
        double factor2 = analyseFactor();
        currNode = currNode.getParentNode();//withFactor()
        if(mulOpType == Token.MULTIPLY){
            changedFactor = factor * factor2;
        }else {
            if(factor2 == 0){
                errorInfo += "计算错误： line" + currNode.getChildren().get(0).getChildren().get(0).getValue().getLine()
                        + "除数不能为0！\n";
            }else {
                changedFactor = factor / factor2;
            }
        }

        if (currNode.getChildren().size() > 2){
            changedFactor = analyseWithFactor(changedFactor);
        }
        return changedFactor;

    }

    public int analyseMulOp(){
        return currNode.getChildren().get(0).getValue().getType();
    }
    public double analyseFactor(){// (expression) 或 intNum 或 realNum 或 variable
        double factor = 0.0;
        currNode = currNode.getChildren().get(0);//第一个子节点
        if(currNode.getType() == TreeNode.TERMINAL_SYMBOL){//终结符
            currNode = currNode.getParentNode();//factor
            if(currNode.getChildren().get(0).getValue().getType() == Token.LEFT_PARENTHESE){
                currNode = currNode.getChildren().get(1);//exp
                factor = analyseExpression();
                currNode = currNode.getParentNode();  //factor
            }else {
                factor = Double.parseDouble(currNode.getChildren().get(0).getValue().getStrValue());
            }
        }else {//var
            Record var = analyseVariable();
            if(var == null){
                errorInfo += "未声明的变量： line " + currNode.getChildren().get(0).getValue().getLine()
                        + " " + currNode.getChildren().get(0).getValue().getStrValue() + "\n";
            }else {
                factor = var.getValue();
            }
            currNode = currNode.getParentNode();
        }
        return factor;
    }
    public Record analyseVariable(){//identifier arrayIndex （注： arrayIndex可能为空）
        currNode = currNode.getChildren().get(0);//identifier
        Record var =  simbolTabble.getRecordByName(currNode.getValue().getStrValue());
        if (var == null){
            currNode = currNode.getParentNode();//variable
            return null;
        }
        currNode = currNode.getParentNode();//variable
        if(currNode.getChildren().size() > 1){//arrayIndex不为空
            int arrayIndex = Integer.parseInt(currNode.getChildren().get(1).getChildren().get(1).getValue().getStrValue());
            if(var.getType() == Record.tIntArray || var.getType() == Record.tRealArray){
                var.setArrayIndex(arrayIndex);
                if(arrayIndex >= var.getArrayNum()){
                    errorInfo += "数组越界: line" + currNode.getChildren().get(0).getValue().getLine()
                            + " " + var.getName() + "[" + var.getArrayIndex() + "] in array " + var.getName() +"[" + var.getArrayNum() +"]\n";
                    return null;
                }
            }else {//普通变量但是有下标
                errorInfo += "错误的变量使用方式：变量" + var.getArrayNum() + "不是数组\n";
                return null;
            }
        }
        if(var.getType() == Record.tIntArray || var.getType() == Record.tRealArray){//如果为数组变量但没有数组下标
            if(var.getArrayIndex() == null){
                errorInfo += "错误的数组变量使用方式： line " + currNode.getChildren().get(0).getValue().getLine()
                        + ", column " + currNode.getChildren().get(0).getValue().getColumn()
                        + " " + currNode.getChildren().get(0).getValue().getStrValue() + "\n";
            }
            return null;
        }
        return var;
    }

}

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
    private String result;
    private String errorInfo;

    public Sematics(TreeNode programNode){
        this.programNode = programNode;
    }

    public void sematicAnalyse(){
        currNode = programNode;
        analyseProgram();
    }

    public void analyseProgram(){
        currNode = currNode.getChildren().get(0);//stmt-sequence
        analyseStmtSequence();
    }

    public void analyseStmtSequence(){//statement stmt-sequence
        level++;
        currNode = currNode.getChildren().get(0);//statement
        analyseStatement();
        currNode = currNode.getParentNode();//stmt-sequence
        if(currNode.getChildren().size() > 1){//如果statement后的smt-sequence不为空
            currNode = currNode.getChildren().get(1);//stmt-sequence(2）
            analyseStmtSequence();
            currNode = currNode.getParentNode();//stmtment(1)
        }
        level--;
    }
    public void analyseStatement(){
        level++;
        currNode = currNode.getChildren().get(0);
        switch (currNode.getType()){
            case TreeNode.IF_STMT:
                analyseIfStmt();
            case TreeNode.ELSE_STMT:
                analyseElseStmt();
            case TreeNode.WHILE_STMT:
                analyseWhileStmt();
            case TreeNode.READ_STMT:
                analyseReadStmt();
            case TreeNode.WRITE_STMT:
                analyseWriteStmt();
            case TreeNode.INT_STMT:
                analyseIntStmt();
            case TreeNode.REAL_STMT:
                analyseRealStmt();
            case TreeNode.ASSIGN_STMT:
                analyseAssignStmt();
        }
        level--;
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
        level--;
    }
    public void analyseElseStmt(){//else{stmt-sequence}
        level++;
        currNode = currNode.getChildren().get(2);//stmt-sequence
        analyseStmtSequence();
        currNode = currNode.getParentNode();//else-stmt
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
            analyseIntFollowStmt(name);

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
            analyseRealFollowStmt(name);
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
        currNode = currNode.getChildren().get(0);//variable
        Record var = analyseVariable();
        if (var == null){//变量未声明
            errorInfo += "未声明的变量： line " + currNode.getChildren().get(0).getValue().getLine()
                    + ", column " + currNode.getChildren().get(0).getValue().getColumn()
                    + " " + currNode.getChildren().get(0).getValue().getStrValue() + "\n";
            currNode = currNode.getParentNode();//assign-stmt
        }else {
            double exp = analyseExpression();
            if(currNode.getChildren().size() > 1){//variable: identifier arrayIndex
                int arrayType = var.getType();
                if(arrayType == Record.tIntArray || arrayType == Record.tRealArray){
                    if (var.getArrayIndex() >= var.getArrayNum()){//数组越界
                        errorInfo += "数组越界： line "+ currNode.getChildren().get(0).getValue().getLine()
                                + var.getName() + "[" + var.getArrayIndex() + "] in array " + var.getName() +"[" + var.getArrayNum() +"]\n";
                        currNode = currNode.getParentNode();//assign-stmt
                        return;
                    }
                    if (arrayType == Record.tIntArray){
                        var.setIntValue((int) exp);
                    }else {
                        var.setRealValue(exp);
                    }
                }else {//普通变量但是有数组下标
                    errorInfo += "错误的变量使用方式：变量" + var.getArrayNum() + "不是数组\n";
                    currNode = currNode.getParentNode();//assign-stmt
                    return;
                }
            }else {//variable: identifier
                if(var.getType() == Record.tIntArray || var.getType() == Record.tRealArray){//如果为数组变量且没有数组下标
                    errorInfo += "错误的数组赋值方式： line" + currNode.getChildren().get(0).getValue().getLine() + "\n";
                    currNode = currNode.getParentNode();//assign-stmt
                    return;
                }
                if (var.getType() == Record.tInt){
                    var.setIntValue((int) exp);
                }else {
                    var.setRealValue(exp);
                }
            }
        }
        currNode = currNode.getParentNode();//assign-stmt
    }

    public boolean analyseCondition(){
        level++;
        currNode = currNode.getChildren().get(0);
        double leftExp = analyseExpression();
        currNode = currNode.getChildren().get(1);
        int operation = analyseComparisonOp();
        currNode = currNode.getChildren().get(2);
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
    public int analyseComparisonOp(){
        return 0;
    }
    public double analyseExpression(){
        return 0;
    }
    public void analyseAddTerm(){

    }
    public void analyseAddOp(){

    }
    public void analyseTerm(){

    }
    public void analyseWithFactor(){

    }
    public void analyseMulOp(){

    }
    public double analyseFactor(){
        return 0.0;
    }
    public Record analyseVariable(){//identifier arrayIndex
        currNode = currNode.getChildren().get(0);//identifier??
        return simbolTabble.getRecordByName(currNode.getValue().getStrValue());
    }

}

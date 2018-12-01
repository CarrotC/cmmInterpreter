package interpreter;

import vo.Record;
import vo.SimbolTabble;
import vo.Token;
import vo.TreeNode;

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
        level++;
        currNode = currNode.getChildren().get(1);//variable
        Record var = analyseVariable(currNode.getValue());

    }
    public void analyseWriteStmt(){

    }
    public void analyseIntStmt(){

    }
    public void analyseRealStmt(){

    }
    public void analyseAssignStmt(){

    }
    public void analyseIntFollowStmt(){

    }
    public void analyseRealFollowStmt(){

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
    public void analyseFactor(){

    }
    public Record analyseVariable(Token token){
        return simbolTabble.getDefinedRecord(level, token);
    }

}

package interpreter;

import vo.Token;
import vo.TreeNode;

import java.util.List;

public class Parser {
    private List<Token> tokenList;
    private TreeNode programNode;
    private Token curToken;

    public void parse(){
        if(tokenList.size() > 0){
            curToken = tokenList.get(0);

        }else {
            //报错：程序为空
        }
    }

    public void parseProgram(){
        while (curToken.getType() != Token.END_SIGN){
            if(curToken.getType() >= 0 && curToken.getType() <= 7){//保留字或标识符
                parseStmtSequence();
            }else {
                //报错
            }
        }
    }

    public void parseStmtSequence(){
        if(curToken.getType() >= 0 && curToken.getType() <= 7){//保留字或标识符
            parseStatement();
        }
    }

    public void parseStatement(){

    }

    public void parseIfStmt(){

    }

    public void parseElseStmt(){

    }

    public void parseWhileStmt(){

    }

    public void parseReadStmt(){

    }

    public void parseWriteStmt(){

    }

    public void parseIntStmt(){

    }

    public void parseIntFollow(){

    }

    public void parseRealStmt(){

    }

    public void parseRealFollow(){

    }

    public void parseAssignStmt(){

    }

    public void parseCondition(){

    }

    public void parseComparisonOp(){

    }

    public void parseExpression(){

    }

    public void parseAndTerm(){

    }

    public void parseAddOp(){

    }

    public void parseTerm(){

    }

    public void parseWithFactor(){

    }

    public void parseMulOp(){

    }

    public void parseFactor(){

    }

    public void parseVariable(){

    }

    public void parseArrayIndex(){

    }









    public static final int PROGRAM = 0;//开始符号

    public static final int STMT_SEQUENCE = 1;//代码块

    public static final int STATEMENT = 2;//语句

    public static final int IF_STMT = 3;//if语句

    public static final int ELSE_STMT = 4;//else语句

    public static final int WHILE_STMT = 5;//while

    public static final int READ_STMT = 6;//read

    public static final int WRITE_STMT = 7;//write

    public static final int INT_STMT = 8;//int

    public static final int INT_FOLLOW = 9;//为区分int声明语句和赋值语句，构成LL(1)文法而构造。代表跟在int语句中保留字int后的部分

    public static final int REAL_STMT = 10;//real

    public static final int REAL_FOLLOW = 11;//为区分real声明语句和赋值语句，构成LL(1)文法而构造。代表跟在real语句中保留字real后的部分

    public static final int ASSIGN_STMT = 12;//赋值语句

    public static final int CONDITION = 13;//条件语句

    public static final int COMPARISON_OP = 14;//比较符号

    public static final int EXPRESSION = 15;//表达式

    public static final int AND_TERM = 16;//加减符号和项，表达式为若干个项相加减，因此即为term和(n-1)个add-term

    public static final int ADD_OP = 17;//加减符号

    public static final int TERM = 18;//项

    public static final int WITH_FACTOR = 19;//乘除符号和因子，项为若干个因子相乘除，因此即为factor和(n-1)个with-factor

    public static final int MUL_OP = 20;//乘除符号

    public static final int FACTOR = 21;//因子

    public static final int VARIABLE = 22;//变量

    public static final int ARRAY_INDEX = 23;//数组下标


}

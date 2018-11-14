package vo;

public class Token {
//    private String name;
    private int type;
    private String strValue;
    private int line;
    private int column;

    public Token(int type, String strValue, int line, int column){
        super();
        this.type = type;
        this.strValue = strValue;
        this.line  = line;
        this.column = column;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public static final int IF = 0;//保留字if

    public static final int WHILE = 1;//保留字while

    public static final int READ = 2;//保留字read

    public static final int WRITE = 3;//保留字write

    public static final int INT = 4;//保留字int

    public static final int REAL = 5;//保留字real

    public static final int ELSE = 6;//保留字else

    public static final int IDENTIFIER = 7;//标识符

    public static final int INT_NUM = 8;//int类型数字（整数）

    public static final int REAL_NUM = 9;//real类型数字（带小数的实数）

    public static final int LEFT_PARENTHESE = 10; //左括号

    public static final int RIGHT_PARENTHESE = 11;//右括号

    public static final int INEQUALITY_SIGN = 12;//不等号<>

    public static final int EQUALITY_SIGN = 13;//等号==

    public static final int LESS_THAN = 14;//小于<

    public static final int SEMICOLON = 15;//分号;

    public static final int ASSIGNMENT = 16;//赋值号=

    public static final int ADD = 17;//加号+

    public static final int MINUS = 18;//减号-

    public static final int MULTIPLY = 19;//乘号*

    public static final int DIVIDE = 20;//除号/

    public static final int LEFT_BRACKET = 21;//左中括号[

    public static final int RIGHT_BRACKET = 22;//右中括号]

    public static final int LEFT_BRACE = 23;//左大括号{

    public static final int RIGHT_BRACE = 24;//右大括号}

    public static final int END_SIGN = 25;//结束符号#


//    public void setNameByType(){
//        switch (this.getType()){
//            case IF:
//                this.setName("if");
//            case WHILE:
//                this.setName("while");
//            case READ:
//                this.setName("if");
//            case WRITE:
//                this.setName("if");
//            case INT:
//                this.setName("if");
//            case REAL:
//                this.setName("if");
//            case ELSE:
//                this.setName("if");
//            case IDENTIFIER:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//            case IF:
//                this.setName("if");
//
//        }
//    }



}

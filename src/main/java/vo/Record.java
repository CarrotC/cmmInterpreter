package vo;

//声明的变量
public class Record {
    private int level;//变量的层次

    private Token token;//对应的标识符（不一定有）
    private String name;
    private int type;//数据类型
    private int intValue;//int类型数据的值
    private double realValue;//real类型数据的值
    private int arrayNum;//数组类型数据的大小
    private Integer arrayIndex;//数组下标,为赋值时为空
    private int[] intArray;//int类型数组
    private double[] realArray;//real类型数组

    public Record(int level, Token token, int type, String name, int intValue){
        this.level = level;
        this.token = token;
        this.type = type;
        this.intValue = intValue;
    }

    public Record(int level, Token token, int type, String name, double realValue){
        this.level = level;
        this.token = token;
        this.type = type;
        this.realValue = realValue;
    }

    public Record(int level, Token token, int type, String name, int[] intArray){
        this.level = level;
        this.token = token;
        this.type = type;
        this.intArray = intArray;
        this.arrayNum = intArray.length;
    }

    public Record(int level, Token token, int type, String name, double[] realArray){
        this.level = level;
        this.token = token;
        this.type = type;
        this.realArray = realArray;
        this.arrayNum = realArray.length;
    }

    public double getValue(){
        if(this.getType() == Record.tInt){
            return this.intValue;
        }
        if (this.getType() == Record.tReal){
            return this.realValue;
        }
        if (this.getType() == Record.tIntArray){
            return intArray[this.getArrayIndex()];
        }
        if (this.getType() == Record.tRealArray){
            return this.realArray[this.arrayIndex];
        }
        return 0;
    }


    public static int tInt = 0;
    public static int tReal = 1;
    public static int tIntArray = 2;
    public static int tRealArray = 3;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public double getRealValue() {
        return realValue;
    }

    public void setRealValue(double realValue) {
        this.realValue = realValue;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public double[] getRealArray() {
        return realArray;
    }

    public void setRealArray(double[] realArray) {
        this.realArray = realArray;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArrayNum() {
        return arrayNum;
    }

    public void setArrayNum(int arrayNum) {
        this.arrayNum = arrayNum;
    }

    public Integer getArrayIndex() {
        return arrayIndex;
    }

    public void setArrayIndex(Integer arrayIndex) {
        this.arrayIndex = arrayIndex;
    }
}

package vo;

//声明的变量
public class Record {
    private int level;//变量的层次

    private Token token;
    private int type;//数据类型
    private int intValue;//int类型数据的值
    private double realValue;//real类型数据的值
    //private int arrayNum;//数组类型数据的大小
    private int[] intArray;//int类型数组
    private double[] realArray;//real类型数组

    public Record(int level, Token token, int type, int intValue){
        this.level = level;
        this.token = token;
        this.type = type;
        this.intValue = intValue;
    }

    public Record(int level, Token token, int type, double realValue){
        this.level = level;
        this.token = token;
        this.type = type;
        this.realValue = realValue;
    }

    public Record(int level, Token token, int type, int[] intArray){
        this.level = level;
        this.token = token;
        this.type = type;
        this.intArray = intArray;
    }

    public Record(int level, Token token, int type, double[] realArray){
        this.level = level;
        this.token = token;
        this.type = type;
        this.realArray = realArray;
    }


    public int tInt = 0;
    public int tReal = 1;
    public int tIntArray = 2;
    public int tRealArray = 3;

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
}

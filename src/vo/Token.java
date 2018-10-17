package vo;

public class Token {
    private String type;
    private String strValue;
    private int line;
    private int column;

    public Token(String type, String strValue, int line, int column){
        super();
        this.type = type;
        this.strValue = strValue;
        this.line  = line;
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
}

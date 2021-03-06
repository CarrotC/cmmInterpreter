package interpreter;

import vo.Comment;
import vo.Token;

import java.util.ArrayList;
import java.util.List;

public class Lexer {


    //源程序码
    private String source = "";
    //词法分析结果
    private String result = "";
    //错误
    private String errorInfo = "";
    //是否有词法错误
    private boolean hasError = false;
    //token集合
    private List<Token> tokenList = new ArrayList<Token>();
    //是否是注释（用于处理多行注释）
    private boolean isComment = false;
    //注释内容
    private Comment comment;
    private String commentValue = "";
    //注释集合
    private List<Comment> commentList = new ArrayList<Comment>();

    //保留字
    private String[] reservedWords = new String[]{
            "if", "else", "while", "read", "write", "int", "real"
    };
    //运算符号
    private Character[] operators = new Character[]{
            '+', '-', '*', '/', '=', '<', '>'
    };
    //分隔符号
    private Character[] seperators = new Character[]{
             '(', ')', '[', ']', '{', '}', ';'
    };

    //构造函数初始化源程序码
    public Lexer(String source){
        this.source = source;
    }


    public void analyzeByLine(String str, int line){
        int begin = 0;//符号起始位置
        int end = 0;//符号结束位置
        Character before = null;//前一字符
        Character after = null;//后一字符
        int column = 0;//当前读取的字符位置
        int state = 0;//初始状态
//        boolean isNavigative = false;

        int length = str.length();//此行字符串的长度
        String numStr = "";
        String wordStr = "";
        Token token;

        if (isComment){
            state = 3;
            commentValue += "\n";
        }
        for(; column < length; column++){
            Character c = str.charAt(column);
            if(column != 0){
                before = str.charAt(column - 1);
            }
            if(column != length - 1){
                after = str.charAt(column + 1);
            }

            switch (state){
                case 0:
                    if(isCharInArray(c, operators)){//运算符号
                        if(c == '/' && after == '/'){//单行注释的处理
                            comment = new Comment(line, column + 1, line, length, str.substring(column));
                            commentList.add(comment);
                            return;//"//"后的视为注释，此行的解析结束
                        }
                        else if(c == '/' && after == '*'){//多行注释的处理
                            commentValue += "/*";
                            comment = new Comment(line, column + 1, 9999, 2, "");
                            column++;
                            state = 3;
                            isComment = true;
                        }
                        else if(c == '-' && Character.isDigit(after) && !Character.isDigit(before) && !Character.isLetter(before)){//‘-’作为负号的情况
                            begin = column;
                            state = 1;
                        }
                        else if(c == '=' && after == '='){//"=="等于符号
                            token = new Token(Token.EQUALITY_SIGN, "==", line, column + 1);
                            tokenList.add(token);
                            column++;
                        }
                        else if(c == '<' && after == '>'){//"<>"不等符号
                            token = new Token(Token.INEQUALITY_SIGN, "<>", line, column + 1);
                            tokenList.add(token);
                            column++;
                        }
                        else {
                            switch (c){
                                case '+':
                                    token = new Token(Token.ADD, c.toString(), line, column + 1);
                                    tokenList.add(token);
                                    break;
                                case '-':
                                    token = new Token(Token.MINUS, c.toString(), line, column + 1);
                                    tokenList.add(token);
                                    break;
                                case '*':
                                    token = new Token(Token.MULTIPLY, c.toString(), line, column + 1);
                                    tokenList.add(token);
                                    break;
                                case '/':
                                    token = new Token(Token.DIVIDE, c.toString(), line, column + 1);
                                    tokenList.add(token);
                                    break;
                                case '=':
                                    token = new Token(Token.ASSIGNMENT, c.toString(), line, column + 1);
                                    tokenList.add(token);
                                    break;
                                case '<':
                                    token = new Token(Token.LESS_THAN, c.toString(), line, column + 1);
                                    tokenList.add(token);
                                    break;
                                default:
                                    errorInfo += "error: line" + line + "," + " column" + (column + 1) + ": "+ c.toString() +"使用错误\n";
                                    state = 0;
                            }
                        }
                    }
                    else if(isCharInArray(c, seperators)){//分隔符
                        switch (c) {
                            case '(':
                                token = new Token(Token.LEFT_PARENTHESE, c.toString(), line, column + 1);
                                tokenList.add(token);
                                break;
                            case ')':
                                token = new Token(Token.RIGHT_PARENTHESE, c.toString(), line, column + 1);
                                tokenList.add(token);
                                break;
                            case '[':
                                token = new Token(Token.LEFT_BRACKET, c.toString(), line, column + 1);
                                tokenList.add(token);
                                break;
                            case ']':
                                token = new Token(Token.RIGHT_BRACKET, c.toString(), line, column + 1);
                                tokenList.add(token);
                                break;
                            case '{':
                                token = new Token(Token.LEFT_BRACE, c.toString(), line, column + 1);
                                tokenList.add(token);
                                break;
                            case '}':
                                token = new Token(Token.RIGHT_BRACE, c.toString(), line, column + 1);
                                tokenList.add(token);
                                break;
                            case ';':
                                token = new Token(Token.SEMICOLON, c.toString(), line, column + 1);
                                tokenList.add(token);
                                break;
                            default:
                                errorInfo += "error: line" + line + "," + " column" + (column + 1) + ": " + c.toString() + "使用错误\n";
                                state = 0;
                        }
                    }
                    else if (c == '.'){
                        errorInfo += "error: line" + line + "," + " column" + (column + 1) + ": '.'使用错误\n";
                        state = 0;
                    }
                    else if(c == '_'){
                        if(Character.isLetter(after) || Character.isDigit(after)){
                            errorInfo += "error: line" + line + "," + " column" + (column + 1) + ": 标识符命名错误\n";
                            while ((Character.isLetter(after) || Character.isDigit(after)) && column < length -1){
                                column++;
                                after = str.charAt(column);
                            }
                        }else {
                            errorInfo += "error: line" + line + "," + " column" + (column + 1) + ": '_'使用错误\n";
                        }
                        state = 0;
                    }
                    else if(Character.isWhitespace(c)){//空格或tab
                        state = 0;
                    }
                    else if (Character.isDigit(c)){
                        begin = column;
                        column--;
                        state = 1;
                    }
                    else if(Character.isLetter(c)){
                        begin = column;
                        column--;
                        state = 2;
                    }
                    else {
                        errorInfo += "error: line" + line + "," + " column" + (column + 1) + ": 未知符号'"+ c.toString() +"'\n";
                        state = 0;
                    }
                    break;
                case 1: //数字
                    if(Character.isDigit(c)){
                        if(column == length -1){
                            numStr = str.substring(begin);
                            if(numStr.contains(".")){
                                token = new Token(Token.REAL_NUM, numStr, line, begin + 1);
                            }else {
                                token = new Token(Token.INT_NUM, numStr, line, begin + 1);
                            }
                            tokenList.add(token);
                        }else {
                            state = 1;
                        }
                    }else if(c == '.'){
                        if(column == length - 1 || str.substring(begin, column).contains(".")){
                            errorInfo += "error: line" + line + "," + " column" + (begin + 1) + ":数字格式错误\n";
                            state = 0;
                        }else {
                            state = 1;
                        }
                    }else if(Character.isLetter(c)){
                            errorInfo += "error: line" + line + "," + " column" + (begin + 1) + ": 数字格式错误或标识符错误\n";
                            while ((Character.isDigit(c) || Character.isLetter(c)) && column < length - 1){
                                column++;
                                c = str.charAt(column);
                            }
                        state = 0;
                    }
                    else {
                        if(before == '.'){
                            errorInfo += "error: line" + line + "," + " column" + (begin + 1) + ": 数字格式错误\n";
                            state = 0;
                        }else {
                            numStr = str.substring(begin, column);
                            if(numStr.contains(".")){
                                token = new Token(Token.REAL_NUM, numStr, line, begin + 1);
                                tokenList.add(token);
                            }else {
                                token = new Token(Token.INT_NUM, numStr, line, begin + 1);
                                tokenList.add(token);
                            }
                            column--;
                            state = 0;
                        }
                    }
                    break;
                case 2://word
                    if(Character.isLetter(c) || Character.isDigit(c)){
                        if(column == length - 1){
                            wordStr = str.substring(begin, column + 1);
                            if(isStrInArray(wordStr, reservedWords)){
                                addReservedWord2TokenList(wordStr, line, begin);
                                state = 0;
                            }else {
                                token = new Token(Token.IDENTIFIER, wordStr, line, begin + 1);
                                tokenList.add(token);
                                state = 0;
                            }
                        }else {
                            state = 2;
                        }
                    }else if(c == '_'){
                        if(column == length - 1){
                            errorInfo += "error: line" + line + "," + " column" + (begin + 1) + ":标识符格式错误\n";
                            state = 0;
                        }else {
                            state = 2;
                        }
                    }
                    else {
                        wordStr = str.substring(begin, column);
                        if(isStrInArray(wordStr, reservedWords)){
                            addReservedWord2TokenList(wordStr, line, begin);
                            state = 0;
                        }else {
                            token = new Token(Token.IDENTIFIER, wordStr, line, begin + 1);
                            tokenList.add(token);
                            state = 0;
                        }
                        column--;
                    }
                    break;
                case 3://注释
                    if(c == '*' && after == '/'){
                        comment.setContent(commentValue);
                        commentList.add(comment);
                        commentValue = "";
                        state = 0;
                        isComment = false;
                    }else {
                        commentValue += c;
                        state = 3;
                        isComment = true;
                    }
            }
        }
    }

    public String lexicalAnalysis(){
        String[] strListByLine = this.source.split("\n");
        for(int i = 0; i < strListByLine.length; i++){
            analyzeByLine(strListByLine[i], i + 1);
        }

        if(isComment){
            this.errorInfo += "warning: line" + comment.getBeginLine() + "," + " column" + comment.getBeginColumn() + ":有未闭合的多行注释\n" + commentValue + "\n";
        }

        if(this.errorInfo != ""){
            this.result += "程序中存在的词法问题：\n";
            this.result += errorInfo;
            this.result += "\n";
        }
        this.result += "词法分析如下：\n";
        for(Token t : tokenList){
            this.result += "line" + t.getLine() + ", column" + t.getColumn() + ": " + t.getType() + " \"" + t.getStrValue() + "\"";
            this.result += "\n";
        }
        return this.result;
    }

    //判断字符是否在数组中
    public boolean isCharInArray(Character c, Character[] cArray){
        for(int i = 0; i < cArray.length; i++){
            if(cArray[i].equals(c)){
                return true;
            }
        }
        return false;
    }

    //判断字符串是否在数组中
    public boolean isStrInArray(String s, String[] strArray){
        for(int i = 0; i < strArray.length; i++){
            if(strArray[i].equals(s)){
                return true;
            }
        }
        return false;
    }

    //向TokenList中添加保留字
    public void addReservedWord2TokenList(String wordStr, int line, int begin){
        Token token;
        if(wordStr.equals("if")){
            token = new Token(Token.IF, wordStr, line, begin + 1);
            tokenList.add(token);
        }
        else if(wordStr.equals("else")){
            token = new Token(Token.ELSE, wordStr, line, begin + 1);
            tokenList.add(token);
        }
        else if(wordStr.equals("while")){
            token = new Token(Token.WHILE, wordStr, line, begin + 1);
            tokenList.add(token);
        }
        else if(wordStr.equals("read")){
            token = new Token(Token.READ, wordStr, line, begin + 1);
            tokenList.add(token);
        }
        else if(wordStr.equals("write")){
            token = new Token(Token.WRITE, wordStr, line, begin + 1);
            tokenList.add(token);
        }
        else if(wordStr.equals("int")){
            token = new Token(Token.INT, wordStr, line, begin + 1);
            tokenList.add(token);
        }
        else if(wordStr.equals("real")){
            token = new Token(Token.REAL, wordStr, line, begin + 1);
            tokenList.add(token);
        }
    }

    public List<Token> getTokenList(){
        Token endToken = new Token(Token.END_SIGN, "#", 999, 999);
        this.tokenList.add(endToken);
        return this.tokenList;
    }

}


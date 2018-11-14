package vo;

import java.util.ArrayList;

public class TreeNode {
    private ArrayList<TreeNode> children;//孩子节点
    private int type;//节点类型
    //private int priority;//操作符优先级
    private Token value;//树的值

    TreeNode(int type, Token value){
        this.type = type;
        this.value = value;
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<TreeNode> children) {
        this.children = children;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }
}

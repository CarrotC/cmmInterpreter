package vo;

import java.util.ArrayList;

public class SimbolTabble {
    private ArrayList<Record> table;//普通变量表
    private ArrayList<Record> arrayTable;//数组变量表

    //获取当前层和更外层的全部变量
//    public ArrayList<Record> getAllValidRecord(int level){
//        ArrayList<Record> validRecords = new ArrayList<>();
//        for(Record r: table){
//            if(r.getLevel() <= level){
//                validRecords.add(r);
//            }
//        }
//        return validRecords;
//    }

    //判断某变量是否已声明/定义过
    public boolean haveDefined(String name){
        Record record = null;
        for(Record r: table){
            if(r.getToken().getStrValue() == name){
                return true;
            }
        }
        return false;
    }

    //清除内层的变量（在程序执行跳出一个块的时候使用）
    public void deleteInsideRecord(int level){
        for (Record r: table){
            if(r.getLevel() <= level){
                table.remove(r);
            }
        }
    }

    //通过名称获取已声明的、在更外层的变量（已声明则返回相应的record，没有则返回null）
    public Record getRecordByName(String name){
        Record record = null;
        for (Record r: table){
            if(r.getName() == name){//名称相同
                record = r;
            }
        }
        return record;//如果符号表中没有相应的标识符则返回空
    }

    public ArrayList<Record> getArrayTable() {
        return arrayTable;
    }

    public void setArrayTable(ArrayList<Record> arrayTable) {
        this.arrayTable = arrayTable;
    }


    public void setTable(ArrayList<Record> table) {
        this.table = table;
    }

    public ArrayList<Record> getTable() {
        return this.table;
    }
}

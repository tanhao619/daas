package com.youedata.daas.rest.common.enums;

public enum DbType {
    Mysql(1), Oracle(2);

    private int type;
    DbType(int type){
        this.type = type;
    }

    public int getDsType(){
        return type;
    }

    public String getTableTypeName(){
        if(type == 1){
            return "mysql";
        }else if(type == 2){
            return "oracle";
        }
        return null;
    }
}

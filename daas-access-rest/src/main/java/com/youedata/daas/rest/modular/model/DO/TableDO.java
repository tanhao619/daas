package com.youedata.daas.rest.modular.model.DO;

/**
 * Created by cdyoue on 2017/12/26.
 */
public class TableDO {
    /*
    字段
     */
    private String field;

    /*
    字段描述
     */
    private String desc;

    /*
    字段类型
     */
    private String type;

    /*
    字段长度
     */
    private Integer length;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "TableDO{" +
                "field='" + field + '\'' +
                ", desc='" + desc + '\'' +
                ", type='" + type + '\'' +
                ", length=" + length +
                '}';
    }
}

package com.youedata.daas.rest.common.enums;

/**
 * Created by cdyoue on 2018/1/27.
 */
public enum UnitType {
//    1:兆,2:条,3:个数,4:日,5:小时,6:周
    MB(1),
    ARTICLE(2),
    NUM(3),
    DAY(4),
    HOUR(5),
    WEEK(6)
    ;
    private Integer type;

    UnitType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}

package com.youedata.daas.rest.schedule.helper;

/**
 * Created by Administrator on 2018/3/9.
 */
public class JobLogUtil {

    public static  String getAliasType (int type) {
        String mediumTypeStr = "";
        switch (type) {
            case 1:
                mediumTypeStr = "MYSQL";
                break;
            case 2:
                mediumTypeStr = "HIVE";
                break;
            case 3:
                mediumTypeStr = "HBASE";
                break;
            case 4:
                mediumTypeStr = "HDFS";
                break;
        }
        return mediumTypeStr;
    }
}

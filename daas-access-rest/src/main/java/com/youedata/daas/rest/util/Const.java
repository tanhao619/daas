package com.youedata.daas.rest.util;

/**
 * 常用常量值
 *
 * @author lucky
 * @create 2017-09-07 13:52
 **/
public class Const {

    //==================文件名Filter=====================//
    public static String REGEXP_YYYY_STR="YYYY";
    public static String REGEXP_YYYYMM_STR="YYYYMM";
    public static String REGEXP_YYYYMMDD_STR="YYYYMMDD";
    public static String REGEXP_YYYYMMDDHH_STR="YYYYMMDDHH";

    public static String REGEXP_YYYY_HDFSSTR="yyyy";
    public static String REGEXP_YYYYMM_HDFSSTR="yyyyMM";
    public static String REGEXP_YYYYMMDD_HDFSSTR="yyyyMMdd";
    public static String REGEXP_YYYYMMDDHH_HDFSSTR="yyyyMMddHH";

    public static String REGEXP_YYYYMMDDHH_REP="\\[0-9\\]\\{10\\}";
    public static String REGEXP_YYYYMMDD_REP="\\[0-9\\]\\{8\\}";
    public static String REGEXP_YYYYMM_REP="\\[0-9\\]\\{6\\}";
    public static String REGEXP_YYYY_REP="\\[0-9\\]\\{4\\}";
    //================================================//
}

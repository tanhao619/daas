package com.youedata.daas.rest.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.youedata.daas.rest.common.enums.DataSourceType;
import com.youedata.daas.rest.common.enums.EngineType;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sijianmeng
 * 字符串工具
 */
public class StringUtil {

    /**
     * @param keys 作为map的key
     * @param values 作为map的value
     */
    public static Map<String, String> StrsToMap(String keys, String values, Class c) throws BussinessException {
        Map<String, String> tempMap = new HashMap<String, String>();
        //存放实体类所有属性
        List<String> colStrs = new ArrayList<String>();
        Field[] fields = c.getDeclaredFields();
        for (Field field:fields) {
            colStrs.add(field.getName());
        }
        //如果传入的查询条件为空, 返回null
        if(StringUtils.isBlank(keys) || StringUtils.isBlank(values)){
            return null;
        }
        String[] keyArr = keys.split(Constant.SEPARATOR);
        String[] valueArr = values.split(Constant.SEPARATOR);
        if (keyArr.length == 1 || valueArr.length == 1){
            Boolean flag = colStrs.contains(keys);
            if (flag){
                tempMap.put(keys,values);
            }else{
                throw new BussinessException(BizExceptionEnum.PATAM_ERROR);
            }
            return tempMap;
        }
        if (keyArr.length != valueArr.length){
            return null;
        }else{
            for(int i = 0; i < keyArr.length; i++){
                Boolean flag = colStrs.contains(keyArr[i]);
                if (flag){
                    tempMap.put(keyArr[i], valueArr[i]);
                }else{
                    throw new BussinessException(BizExceptionEnum.PATAM_ERROR);
                }
            }
            return tempMap;
        }
    }

    /**
     * 标识生成规则
     * @param pre 前缀(p,f,s等)
     * @param code 上一个标识
     * @return
     */
    public static String getCode(String pre,String code){
        //第一次生成数据时
        if (StringUtils.isBlank(code) && !pre.contains("_TASK")){
            return pre + "00001";
        }else if (StringUtils.isBlank(code) && pre.contains("_TASK")){
            return pre + "1";
        }
        //已有数据
        //获取标识中的数字
        String newCodeStr = code.substring(pre.length());
        Integer newCodeIn = Integer.valueOf(newCodeStr);
        if (pre.contains("_TASK")){
            newCodeIn = newCodeIn + 1;
            return pre + String.valueOf(newCodeIn);
        }
        String codeStr = String.valueOf(newCodeIn);
        Integer codeLen = codeStr.length();
        switch (codeLen){
            case 1:
                if(newCodeIn < 9){
                    return pre + "0000" + (newCodeIn + 1);
                }else{
                    return pre + "00010";
                }
            case 2:
                if (newCodeIn < 99){
                    return pre + "000" + (newCodeIn + 1);
                }else{
                    return pre + "00100";
                }
            case 3:
                if(newCodeIn < 999){
                    return pre + "00" + (newCodeIn + 1);
                }else {
                    return pre + "01000";
                }
            case 4:
                if (newCodeIn < 9999){
                    return pre + "0" + (newCodeIn + 1);
                }else {
                    return pre + "10000";
                }
            case 5:
                if (newCodeIn < 99999){
                    return pre + (newCodeIn + 1);
                }else {
                    throw new BussinessException(BizExceptionEnum.TOO_LONG);
                }
            default:
                throw new BussinessException(BizExceptionEnum.TOO_LONG);
        }

    }

    /**
     *  重塑数据源链接信息
     * @param jsonStr
     * @param  type 1, mysql 2, oracle
     * @return
     */
    public static String Remodeling(String jsonStr,Integer type, Integer engine){
        JSONObject json = JSON.parseObject(jsonStr);
        if (type.equals(DataSourceType.TABLE.getType()) && engine.equals(EngineType.MYSQL.getType())){
            String url = json.getString("url");
            url = "jdbc:mysql://" + url +"?useSSL=false&useUnicode=true&characterEncoding=UTF8";
            json.put("url",url);
            json.put("driverClassName", "com.mysql.jdbc.Driver");
        }
        if (type.equals(DataSourceType.TABLE.getType()) && engine.equals(EngineType.ORACLE.getType())){
            String url = json.getString("url");
            url = "jdbc:oracle:thin:@"+url;
            json.put("url",url);
            json.put("driverClassName", "oracle.jdbc.driver.OracleDriver");
        }
        return json.toJSONString();
    }

    /**
     * 将文件大小转换为字节
     * @param size
     * @return
     */
    public static Long corver2Byte(String size){
        String left;
        if (size.endsWith("KB")){
            left = StringUtils.left(size, size.length()-2);
            return (long)(Double.valueOf(left)*1024);
        }else if (size.endsWith("MB")){
            left = StringUtils.left(size, size.length()-2);
            return (long)(Double.valueOf(left)*1024 * 1024);
        }else if (size.endsWith("GB")){
            left = StringUtils.left(size, size.length()-2);
            return (long)(Double.valueOf(left)*1024 * 1024 * 1024);
        }else {
            return Long.valueOf(size);
        }
    }
    public static String getSql(JSONObject rule, String username){
        JSONObject from = rule.getJSONObject("from");
        String filename = from.getString("filename");
        String column = from.getJSONObject("structure").getString("column");
        if (from.getJSONObject("dsource").getString("engine").toUpperCase().equals("MYSQL")){
            return "SELECT " + column + " FROM " + filename;
        }else {
            String[] split = column.split(",");
            StringBuffer buffer = new StringBuffer();
            for (int i=0;i<split.length;i++){
                if (i <split.length-1){
                    buffer.append("\""+split[i]+"\",");
                }else {
                    buffer.append("\""+split[i]+"\"");
                }
            }
            return "SELECT " + buffer.toString() + " FROM " + username + ".\"" + filename+"\"";
        }
    }
}

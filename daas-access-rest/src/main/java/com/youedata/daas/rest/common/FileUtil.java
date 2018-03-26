package com.youedata.daas.rest.common;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author sijianmeng
 * 文件工具
 */
public class FileUtil {
    /**
     * csv通过浏览器下载并处理乱码
     *
     * @throws
     */
    public static void responseSetProperties(String fileName, HttpServletResponse response) throws UnsupportedEncodingException{
        //文件名
        String fn = fileName + ".csv";
        //读取字节码
        String utf = "UTF-8";
        //设置浏览器保存相应并发起下载
        //response.setContentType("application/ms-txt.numberformat:@");
        response.setCharacterEncoding(utf);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control","max-age=30");
        response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(fn,utf));
    }

    /**
     * 导出csv文件
     */
    public static void doExport(List<Map<String, Object>> dataList, String colNames, String mapKey, OutputStream os) throws IOException, ParseException {
        CsvWriter csvWriter = new CsvWriter(os,',', Charset.forName("UTF-8"));
        String[] colNameArr = null;
        String[] mapKeyArr = null;
        colNameArr = colNames.split(",");
        mapKeyArr = mapKey.split(",");
        //csv输出列头
        csvWriter.writeRecord(colNameArr,true);
        //csv输出数据值
        if(null != dataList){
            for(int i = 0; i < dataList.size(); i++){
                String[] content = new String[colNameArr.length];
                for (int j = 0; j < colNameArr.length; j++){
                    Object tempObj = dataList.get(i).get(mapKeyArr[j]);
                    if (null != tempObj && !"".equals(tempObj.toString())){
                        String temp = dataList.get(i).get(mapKeyArr[j]).toString();
                        if(isDateType(dataList.get(i).get(mapKeyArr[j]))){
                            content[j] = "" + temp;
                        }else {
                            if (mapKeyArr[j] instanceof String){
                                if("proCTime".equals(mapKeyArr[j])) {
                                    temp = DateUtil.formatDate(dataList.get(i).get(mapKeyArr[j]).toString(),"yyyy-MM-dd");
                                } else if("createTime".equals(mapKeyArr[j])) {
                                    temp = DateUtil.formatDate(dataList.get(i).get(mapKeyArr[j]).toString(),"yyyy-MM-dd HH:mm:ss");
                                } else if("warnningType".equals(mapKeyArr[j])) {
                                    temp = convertWarnType((Integer)dataList.get(i).get(mapKeyArr[j]));
                                } else if("acoType".equals(mapKeyArr[j])) {
                                    temp = convertAcoType((Integer)dataList.get(i).get(mapKeyArr[j]));
                                }
                            }
                            content[j] = temp;
                        }
                    }else{
                        content[j] = "";
                    }
                }
                csvWriter.writeRecord(content, true);
            }
        }
        os.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
        csvWriter.flush();
        csvWriter.close();
    }

    /**
     * 告警类型转换
     * @param key
     * @return
     */
    private static String convertWarnType(Integer key) {
        switch (key) {
            case 1:
                return "未达到传输标准";
            case 2:
                return "周期内未执行";
            case 3:
                return "重复文件";
            case 4:
                return "报错停止";
            case 5:
                return "未按指定时间开始";
            default:
                return String.valueOf(key);
        }
    }

    /**
     * 接入类型转换
     * @param key
     * @return
     */
    private static String convertAcoType(Integer key) {
        switch (key) {
            case 1:
                return "FTP";
            case 2:
                return "API";
            default:
                return String.valueOf(key);
        }
    }

    /**
     * 接入对象导出csv文件
     */
    public static void doObjectsExport(List<Map<String, Object>> dataList, String colNames, String mapKey, OutputStream os)throws IOException{
        CsvWriter csvWriter = new CsvWriter(os,',', Charset.forName("UTF-8"));
        String[] colNameArr = null;
        String[] mapKeyArr = null;
        colNameArr = colNames.split(",");
        mapKeyArr = mapKey.split(",");
        //csv输出列头
        csvWriter.writeRecord(colNameArr,true);
        //csv输出数据值
        if(null != dataList){
            for(int i = 0; i < dataList.size(); i++){
                String[] content = new String[colNameArr.length];
                for (int j = 0; j < colNameArr.length; j++){
                    if (dataList.get(i).containsKey(mapKeyArr[j])){
                        String temp = dataList.get(i).get(mapKeyArr[j]).toString();
                        if(isDateType(dataList.get(i).get(mapKeyArr[j]))){
                            content[j] = "" + temp;
                        }else{
                            content[j] = temp;
                        }
                    }else{
                        JSONObject obj = JSONObject.parseObject((String) dataList.get(i).get("acoContent"));
                        if ((Integer)dataList.get(i).get("acoType") == 1){
                            JSONObject desc = obj.getJSONObject("ftpDesc");
                            content[j] = desc.getString("ip");
                            content[j+1] = desc.getString("port");
                            content[j+2] = desc.getString("user");
                            content[j+3] = desc.getString("rootPath");
                            break;
                        }
                        if ((Integer)dataList.get(i).get("acoType") == 2){
                            JSONObject desc = obj.getJSONObject("apiDesc");
                            content[j] = desc.getString("url");
                            content[j+1] = desc.getString("kafkaTopic");
                            break;
                        }
                    }
                }
                csvWriter.writeRecord(content, true);
            }
        }
        os.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
        csvWriter.flush();
        csvWriter.close();
    }

    /**
     * 转换时间为字符串, 不然会出问题
     */
    private static boolean isDateType(Object o){
        if(o instanceof Date){
            return true;
        } else {
            return false;
        }
    }
    /**
     * 判断json字符串
     */
    private static boolean isJsonType(String str){
        try{
            JSONObject.parseObject(str);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}

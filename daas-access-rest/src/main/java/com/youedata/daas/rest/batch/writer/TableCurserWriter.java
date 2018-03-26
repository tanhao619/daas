package com.youedata.daas.rest.batch.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.youedata.daas.rest.common.DateUtil;
import com.youedata.daas.rest.common.enums.AccessType;
import com.youedata.daas.rest.config.DaasCoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by cdyoue on 2017/12/27.
 */
public class TableCurserWriter implements ItemWriter<JSONObject> {
    private static Logger logger = LoggerFactory.getLogger(TableCurserWriter.class);
    @Autowired
    private DaasCoreConfig daasCoreConfig;
    @Autowired
    private RestTemplate template;

    private String targetType;
    private String tableName;
    private long unit;
    private String to;
    private String accessType;
    private String taskId;

    public TableCurserWriter() {
    }

    public TableCurserWriter(String targetType, String tableName, long unit, String to, String accessType, String taskId) {
        this.targetType = targetType;
        this.tableName = tableName;
        this.unit = unit;
        this.to = to;
        this.accessType = accessType;
        this.taskId = taskId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void write(List<? extends JSONObject> items) {
        logger.info("taskId=" + taskId + ",向数据库写入" + items.size() + "条数据" + "，入库时间：" + DateUtil.getTime());
        logger.info("目标介质信息：" + to);
        for (JSONObject item : items) {
            JSONArray columnJsonArray = JSONObject.parseObject(to).getJSONObject("structure").getJSONArray("column");
//            if (AccessType.TABLE.getAccessType().equals(accessType)) {
            for (int i = 0; i < columnJsonArray.size(); i++) {
                JSONObject columnJsonObject = columnJsonArray.getJSONObject(i);
                String targetField = columnJsonObject.getString("field");
                String sourceField = columnJsonObject.getString("fromField");
                if (item.containsKey(sourceField)) {
                    String columnType = columnJsonObject.getString("type");
                    Object data = null;
                    //如果value是json格式, 需要转义成json字符串
                    if ("json".equalsIgnoreCase(columnType)) {
                        Object rawData = item.get(sourceField);
                        data = JSONObject.toJSONString(rawData);
                    } else if ("datetime".equalsIgnoreCase(columnType)) {
                        Timestamp rawData = (Timestamp) item.get(sourceField);
                        if (rawData != null && rawData.getTime() != 0L) {
                            data = DateUtil.formatDate(new Date(rawData.getTime()));
                        }
                    }
                    else {
                        data = item.get(sourceField);
                    }

                    item.remove(sourceField);
                    item.put(targetField, data);
                }
            }
//            }
            if (AccessType.FILE.getAccessType().equals(accessType)) {
                Iterator<Map.Entry<String, Object>> iterator = item.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> next = iterator.next();
                    if (next.getValue() instanceof HashMap) {
                        next.setValue(JSONObject.toJSONString((HashMap) next.getValue()));
                        logger.info("ftp json值处理");
                    }
                }
            }
            item.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));

        }
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("record", items);
        json.put("data", data);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity entity = new HttpEntity(json, headers);
          ResponseEntity<JSONObject> exchange = template.exchange(daasCoreConfig.getDataPut() + daasCoreConfig.getMysqlNode() + "/" + daasCoreConfig.getDbName() + "/" + tableName, HttpMethod.POST, entity, JSONObject.class);
        if (exchange.getBody().getInteger("code") != 6001) {
            logger.error("数据 " + json.toJSONString() + "写入失败 ");
//            throw new BussinessException(BizExceptionEnum.TASK_WRITE_ERROR);
        }

    }
}

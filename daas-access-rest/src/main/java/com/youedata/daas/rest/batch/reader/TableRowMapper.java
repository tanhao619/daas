package com.youedata.daas.rest.batch.reader;

import com.alibaba.fastjson.JSONObject;
import com.youedata.daas.rest.modular.service.ICurserService;
import com.youedata.daas.rest.util.SpringUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by cdyoue on 2018/1/3.
 */
public class TableRowMapper implements RowMapper<JSONObject> {
    private String url;
    private String tableName;
    private ICurserService curserService = (ICurserService) SpringUtil.getBean("curserServiceImpl");
    public TableRowMapper(String tableName, String url) {
        this.url = url;
        this.tableName = tableName;
    }
    @Override
    public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int colcount = metaData.getColumnCount();//取得全部列数
        JSONObject map = new JSONObject(colcount);
        for(int i=1;i<=colcount;i++){
            String colName = metaData.getColumnName(i);//列名
            String colType = metaData.getColumnTypeName(i);//类型
            map.put(colName, rs.getObject(i));
        }
        return map;
    }
}

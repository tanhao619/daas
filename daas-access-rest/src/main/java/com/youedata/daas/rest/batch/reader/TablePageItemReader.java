package com.youedata.daas.rest.batch.reader;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sql.DataSource;

import com.youedata.daas.rest.common.enums.DbType;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * created by cdyoue 2018/3/20 16:40
 * @param <T>
 */

public class TablePageItemReader<T> extends AbstractPagingItemReader<T> implements InitializingBean {
    private static final String START_AFTER_VALUE = "start.after";
    public static final int VALUE_NOT_SET = -1;
    private DataSource dataSource;
    private PagingQueryProvider queryProvider;
    private Map<String, Object> parameterValues;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private RowMapper<T> rowMapper;
    private String firstPageSql;
    private String remainingPagesSql;
    private Map<String, Object> startAfterValues;
    private Map<String, Object> previousStartAfterValues;
    private String tabelName;
    private String tableTypeName;
    private int fetchSize = -1;

    public TablePageItemReader() {
        this.setName(ClassUtils.getShortName(TablePageItemReader.class));
    }

    public String getTableTypeName() {
        return tableTypeName;
    }

    public void setTableTypeName(String tableTypeName) {
        this.tableTypeName = tableTypeName;
    }

    public void setTableName(String tableName){
        this.tabelName = tableName;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setQueryProvider(PagingQueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }

    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public void setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(this.dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        if (this.fetchSize != -1) {
            jdbcTemplate.setFetchSize(this.fetchSize);
        }

        jdbcTemplate.setMaxRows(this.getPageSize());
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Assert.notNull(this.queryProvider);
        this.queryProvider.init(this.dataSource);
        this.firstPageSql = this.queryProvider.generateFirstPageQuery(this.getPageSize());
        this.remainingPagesSql = this.queryProvider.generateRemainingPagesQuery(this.getPageSize());
    }


    protected String getRemainingPagesSql(String tableTypeName){
        String sql = null;
        if(DbType.Mysql.getTableTypeName().equals(tableTypeName)){
            sql = "SELECT * FROM " + this.tabelName + " LIMIT ?,?";
        }else if(DbType.Oracle.getTableTypeName().equals(tableTypeName)){
            sql = "SELECT * FROM " + this.tabelName + " WHERE ROWNUM > ? AND ROWNUM <= ?";
        }
        return sql;
    }


    /**
     * 自定义部分方法
     */
    protected void doReadPage() {

        if (this.results == null) {
            this.results = new CopyOnWriteArrayList();
        } else {
            this.results.clear();
        }

        TablePageItemReader<T>.PagingRowMapper rowCallback = new TablePageItemReader.PagingRowMapper();
        List query;

        int currentPage = this.getPage();

        if (currentPage == 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("SQL used for reading first page: [" + this.firstPageSql + "]");
            }

            if (this.parameterValues != null && this.parameterValues.size() > 0) {
                if (this.queryProvider.isUsingNamedParameters()) {
                    query = this.namedParameterJdbcTemplate.query(this.firstPageSql, this.getParameterMap(this.parameterValues, (Map)null), rowCallback);
                } else {
                    query = this.getJdbcTemplate().query(this.firstPageSql, this.getParameterList(this.parameterValues, (Map)null).toArray(), rowCallback);
                }
            } else {
                query = this.getJdbcTemplate().query(this.firstPageSql, rowCallback);
            }
        } else {
            this.previousStartAfterValues = this.startAfterValues;
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("SQL used for reading remaining pages: [" + this.remainingPagesSql + "]");
            }

            if (this.queryProvider.isUsingNamedParameters()) {
                query = this.namedParameterJdbcTemplate.query(this.remainingPagesSql, this.getParameterMap(this.parameterValues, this.startAfterValues), rowCallback);
            } else {
//               query = this.getJdbcTemplate().query(this.remainingPagesSql, this.getParameterList(this.parameterValues, this.startAfterValues).toArray(), rowCallback);
                //拿到当前的条数
                long currentCount = currentPage * this.getPageSize();
                Object[] objects = new Object[]{currentCount,currentCount + this.getPageSize()};
                query = this.getJdbcTemplate().query(getRemainingPagesSql(this.tableTypeName), objects, rowCallback);
            }
        }

        this.results.addAll(query);
    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);
        if (this.isSaveState()) {
            if (this.isAtEndOfPage() && this.startAfterValues != null) {
                executionContext.put(this.getExecutionContextKey("start.after"), this.startAfterValues);
            } else if (this.previousStartAfterValues != null) {
                executionContext.put(this.getExecutionContextKey("start.after"), this.previousStartAfterValues);
            }
        }

    }

    private boolean isAtEndOfPage() {
        return this.getCurrentItemCount() % this.getPageSize() == 0;
    }

    public void open(ExecutionContext executionContext) {
        if (this.isSaveState()) {
            this.startAfterValues = (Map)executionContext.get(this.getExecutionContextKey("start.after"));
            if (this.startAfterValues == null) {
                this.startAfterValues = new LinkedHashMap();
            }
        }

        super.open(executionContext);
    }

    protected void doJumpToPage(int itemIndex) {
        if (this.startAfterValues == null && this.getPage() > 0) {
            String jumpToItemSql = this.queryProvider.generateJumpToItemQuery(itemIndex, this.getPageSize());
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("SQL used for jumping: [" + jumpToItemSql + "]");
            }

            if (this.queryProvider.isUsingNamedParameters()) {
                this.startAfterValues = this.namedParameterJdbcTemplate.queryForMap(jumpToItemSql, this.getParameterMap(this.parameterValues, (Map)null));
            } else {
                this.startAfterValues = this.getJdbcTemplate().queryForMap(jumpToItemSql, this.getParameterList(this.parameterValues, (Map)null).toArray());
            }
        }

    }

    private Map<String, Object> getParameterMap(Map<String, Object> values, Map<String, Object> sortKeyValues) {
        Map<String, Object> parameterMap = new LinkedHashMap();
        if (values != null) {
            parameterMap.putAll(values);
        }

        if (sortKeyValues != null && !sortKeyValues.isEmpty()) {
            Iterator var4 = sortKeyValues.entrySet().iterator();

            while(var4.hasNext()) {
                Entry<String, Object> sortKey = (Entry)var4.next();
                parameterMap.put("_" + (String)sortKey.getKey(), sortKey.getValue());
            }
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Using parameterMap:" + parameterMap);
        }

        return parameterMap;
    }

    private List<Object> getParameterList(Map<String, Object> values, Map<String, Object> sortKeyValue) {
        SortedMap<String, Object> sm = new TreeMap();
        if (values != null) {
            sm.putAll(values);
        }

        List<Object> parameterList = new ArrayList();
        parameterList.addAll(sm.values());
        if (sortKeyValue != null && sortKeyValue.size() > 0) {
            List<Entry<String, Object>> keys = new ArrayList(sortKeyValue.entrySet());

            for(int i = 0; i < keys.size(); ++i) {
                for(int j = 0; j < i; ++j) {
                    parameterList.add(((Entry)keys.get(j)).getValue());
                }

                parameterList.add(((Entry)keys.get(i)).getValue());
            }
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Using parameterList:" + parameterList);
        }

        return parameterList;
    }

    private JdbcTemplate getJdbcTemplate() {
        return (JdbcTemplate)this.namedParameterJdbcTemplate.getJdbcOperations();
    }

    private class PagingRowMapper implements RowMapper<T> {
        private PagingRowMapper() {
        }

        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            TablePageItemReader.this.startAfterValues = new LinkedHashMap();
            Iterator var3 = TablePageItemReader.this.queryProvider.getSortKeys().entrySet().iterator();

            while(var3.hasNext()) {
                Entry<String, Order> sortKey = (Entry)var3.next();
                TablePageItemReader.this.startAfterValues.put(sortKey.getKey(), rs.getObject((String)sortKey.getKey()));
            }

            return TablePageItemReader.this.rowMapper.mapRow(rs, rowNum);
        }
    }
}

package com.youedata.daas.core.mutidatasource;

import com.youedata.daas.core.mutidatasource.annotion.DataSourceType;

/**
 * datasource的上下文
 *
 * @author fengshuonan
 * @date 2017年3月5日 上午9:10:58
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<DataSourceType>();

    /**
     * 设置数据源类型
     *
     * @param dataSourceType 数据库类型
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        contextHolder.set(dataSourceType);
    }

    /**
     * 获取数据源类型
     */
    public static DataSourceType getDataSourceType() {
        return contextHolder.get();
    }

    /**
     * 清除数据源类型
     */
    public static void clearDataSourceType() {
        contextHolder.remove();
    }
}

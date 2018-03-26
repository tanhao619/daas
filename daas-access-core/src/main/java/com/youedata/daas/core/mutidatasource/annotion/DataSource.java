package com.youedata.daas.core.mutidatasource.annotion;

import java.lang.annotation.*;

/**
 * 
 * 多数据源标识
 *
 * @author fengshuonan
 * @date 2017年3月5日 上午9:44:24
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DataSource {

	DataSourceType value() default DataSourceType.def;
}

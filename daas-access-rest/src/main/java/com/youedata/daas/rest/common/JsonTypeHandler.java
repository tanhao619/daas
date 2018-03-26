package com.youedata.daas.rest.common;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mapper里json型字段到类的映射。
 * 用法一:
 * 入库：#{jsonDataField, typeHandler=com.adu.spring_test.mybatis.typehandler.JsonTypeHandler}
 * 出库：
 * <resultMap>
 * <result property="jsonDataField" column="json_data_field" javaType="com.xxx.MyClass" typeHandler="com.adu.spring_test.mybatis.typehandler.JsonTypeHandler"/>
 * </resultMap>
 *
 * 用法二：
 * 1）在mybatis-config.xml中指定handler:
 *      <typeHandlers>
 *              <typeHandler handler="com.adu.spring_test.mybatis.typehandler.JsonTypeHandler" javaType="com.xxx.MyClass"/>
 *      </typeHandlers>
 * 2)在MyClassMapper.xml里直接select/update/insert。
 *
 *
 * @author yunjie.du
 * @date 2016/5/31 19:33
 */
public class JsonTypeHandler extends BaseTypeHandler<Object> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter,
                                    JdbcType jdbcType) throws SQLException {

        ps.setString(i, JsonUtil.stringify(parameter));
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName)
            throws SQLException {

        return JsonUtil.parse(rs.getString(columnName), Object.class);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return JsonUtil.parse(rs.getString(columnIndex), Object.class);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {

        return JsonUtil.parse(cs.getString(columnIndex), Object.class);
    }
}

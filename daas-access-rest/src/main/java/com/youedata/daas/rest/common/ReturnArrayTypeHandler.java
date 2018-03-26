package com.youedata.daas.rest.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

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
public class ReturnArrayTypeHandler extends BaseTypeHandler<JSONArray> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter,
                                    JdbcType jdbcType) throws SQLException {

        ps.setString(i, JsonUtil.stringify(parameter));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName)
            throws SQLException {

        return JsonUtil.parse(rs.getString(columnName), JSONArray.class);
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return JsonUtil.parse(rs.getString(columnIndex), JSONArray.class);
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {

        return JsonUtil.parse(cs.getString(columnIndex), JSONArray.class);
    }
}

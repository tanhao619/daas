package com.youedata.daas.rest.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;

import java.sql.*;

/**
 * Created by cdyoue on 2018/2/1.
 */
public class ArrayTypeHandler extends BaseTypeHandler<JSONObject[]> {
    private static final String TYPE_NAME_VARCHAR = "varchar";
    private static final String TYPE_NAME_INTEGER = "integer";
    private static final String TYPE_NAME_BOOLEAN = "boolean";
    private static final String TYPE_NAME_NUMERIC = "numeric";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject[] parameter,
                                    JdbcType jdbcType) throws SQLException {

        /* 这是ibatis时的做法
        StringBuilder arrayString = new StringBuilder("{");

        for (int j = 0, l = parameter.length; j < l; j++) {
            arrayString.append(parameter[j]);
            if (j < l - 1) {
                arrayString.append(",");
            }
        }

        arrayString.append("}");

        ps.setString(i, arrayString.toString());
        */

        String typeName = "json";
//        if (parameter instanceof Integer[]) {
//            typeName = TYPE_NAME_INTEGER;
//        } else if (parameter instanceof String[]) {
//            typeName = TYPE_NAME_VARCHAR;
//        } else if (parameter instanceof Boolean[]) {
//            typeName = TYPE_NAME_BOOLEAN;
//        } else if (parameter instanceof Double[]) {
//            typeName = TYPE_NAME_NUMERIC;
//        }

        if (typeName == null) {
            throw new TypeException("ArrayTypeHandler parameter typeName error, your type is " + parameter.getClass().getName());
        }

        // 这3行是关键的代码，创建Array，然后ps.setArray(i, array)就可以了
        Connection conn = ps.getConnection();
        Array array = conn.createArrayOf(typeName, parameter);
        ps.setArray(i, array);
    }

    @Override
    public JSONObject[] getNullableResult(ResultSet rs, String columnName)
            throws SQLException {

        return getArray(rs.getArray(columnName));
    }

    @Override
    public JSONObject[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return getArray(rs.getArray(columnIndex));
    }

    @Override
    public JSONObject[] getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {

        return getArray(cs.getArray(columnIndex));
    }

    private JSONObject[] getArray(Array array) {

        if (array == null) {
            return null;
        }

        try {
            return (JSONObject[]) array.getArray();
        } catch (Exception e) {
        }

        return null;
    }
}

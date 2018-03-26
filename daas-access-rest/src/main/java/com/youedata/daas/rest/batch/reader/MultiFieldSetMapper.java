package com.youedata.daas.rest.batch.reader;

import com.alibaba.fastjson.JSONObject;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.util.UUID;

public class MultiFieldSetMapper implements FieldSetMapper<JSONObject> {

    private String fromColumn;

    public MultiFieldSetMapper() {
        super();
    }

    public MultiFieldSetMapper (String _fromColumn) {
        this.fromColumn = _fromColumn;
    }

    @Override
    public JSONObject mapFieldSet(FieldSet fieldSet) {
        String[] split = fromColumn.split(",");
        String[] values = fieldSet.getValues();
        JSONObject map = new JSONObject();
        for (int i = 0;i<values.length;i++){
            map.put(split[i],fieldSet.readString(i));
        }
        map.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
        return map;
    }
}
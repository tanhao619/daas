package com.youedata.daas.rest.batch.processor;

import com.alibaba.fastjson.JSONObject;
import org.springframework.batch.item.ItemProcessor;

public class DaasItemProcessor implements ItemProcessor<JSONObject,JSONObject> {
    private String conversion;

    public DaasItemProcessor(String conversion) {
        this.conversion = conversion;
    }

    public String getConversion() {
        return conversion;
    }

    public void setConversion(String conversion) {
        this.conversion = conversion;
    }

    @Override
    public JSONObject process(JSONObject item) throws Exception {
        return item;
    }
}
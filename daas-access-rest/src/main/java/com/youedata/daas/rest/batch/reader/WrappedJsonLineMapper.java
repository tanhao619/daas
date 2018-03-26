package com.youedata.daas.rest.batch.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.apache.velocity.runtime.directive.ForeachScope;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.JsonLineMapper;
import org.springframework.context.annotation.Bean;

import java.util.*;


public class WrappedJsonLineMapper implements LineMapper<JSONObject> {

    private String fromCloum;

    public WrappedJsonLineMapper(String fromCloum) {
        this.fromCloum = fromCloum;
    }

    @Override
    public JSONObject mapLine(String line, int lineNumber) throws Exception {
//        String[] split = fromCloum.split(",");
//        //内容里面{"id":1,"name":"test"}
//        //f1,f2
//        JSONObject jsonObject = JSON.parseObject(line, Feature.OrderedField);
//        LinkedHashMap<String, String> linkedHashMap = JSON.parseObject(jsonObject.toJSONString(), LinkedHashMap.class);
//        Map objectMap = new HashMap();
//        int index = 0;
//        List<String> stringB = Arrays.asList(split);
//        for (Map.Entry<String, String> entry : linkedHashMap.entrySet()) {
//            objectMap.put(stringB.get(index), entry.getValue());
//            index++;
//        }
//
//        JSONObject json = new JSONObject();
//        json.putAll(objectMap);
//
//        json.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
//        return json;
        //防止json格式的值会继续读取, 返回空会跳出次方法
        if ("".equals(line)){
            return null;
        }
        Map<String, Object> objectMap = delegate().mapLine(line, lineNumber);
        JSONObject json = new JSONObject();
        json.putAll(objectMap);
        json.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
        return json;
    }
    //这里要交给spring去初始化
    @Bean
    public JsonLineMapper delegate(){
        return new JsonLineMapper();
    }

//    public static void main(String args[]) throws Exception {
//        WrappedJsonLineMapper a = new WrappedJsonLineMapper("f1,f2,f3");
//        System.out.println(a.mapLine("{\"id\":1,\"name\":\"test\",\"age\":\"12\"}", 1));
//    }
}
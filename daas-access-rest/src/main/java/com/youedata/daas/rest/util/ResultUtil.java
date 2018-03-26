package com.youedata.daas.rest.util;

import com.baomidou.mybatisplus.plugins.Page;
import com.youedata.daas.core.base.tips.Tip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sijianmeng
 * Created by cdyoue on 2017/11/27.
 */
public class ResultUtil {

    public static Tip result(int code, String msg, Object... objs){
        Tip tip = new Tip();
        tip.setCode(code);
        tip.setMessage(msg);
        if (objs.length == 0){
            tip.setResult("");
        }else{
            Map<String, Object> resultMap = new HashMap<String,Object>();
            Object obj = objs[0];
            if (obj instanceof List){
                List list = (List) obj;
                resultMap.put("datas", list);
                tip.setResult(resultMap);
            }else if (obj instanceof Page){
                Page page = (Page)obj;
                resultMap.put("datas", page.getRecords());
                resultMap.put("totalCounts", page.getTotal());
                tip.setResult(resultMap);
            }else{
                resultMap.put("data", obj);
                tip.setResult(resultMap);
            }
        }
        return tip;
    }
}

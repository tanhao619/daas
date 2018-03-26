package com.youedata.daas.rest.common;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

/**
 * @author sijianmeng
 * map,对象工具
 */
public class MapObjectUtils {
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null)
            return null;

        Object obj = beanClass.newInstance();

        BeanUtils.populate(obj, map);

        return obj;
    }

    public static Map<?, ?> objectToMap(Object obj) {
        if(obj == null)
            return null;

        return new BeanMap(obj);
    }
}

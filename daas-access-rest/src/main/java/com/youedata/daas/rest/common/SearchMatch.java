package com.youedata.daas.rest.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tanhao on 2018/3/9.
 */
public class SearchMatch {
    public static final String REGEX_SEARCHVALUE = "[`~!@#$^&*()=|{}':;',\\\\[\\\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？%+_]";




    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    /**
     * 判断是否含有特殊字符
     * @param regex
     * @param input
     * @return
     */
    public static boolean isMatch(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.find();
    }
}

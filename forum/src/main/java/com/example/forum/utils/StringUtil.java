package com.example.forum.utils;

public class StringUtil {

    /**
     * 判断字符串是够为空
     * @param value 要判断的字符串
     * @return true 空  <br/> false 非空
     */
    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}

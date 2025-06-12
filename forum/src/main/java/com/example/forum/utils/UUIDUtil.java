package com.example.forum.utils;

import java.util.UUID;

public class UUIDUtil {
    /**
     * 生成36位的UUID
     * @return
     */
    public static String UUID_36() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成32位的UUID
     *
     */
    public static String UUID_32() {
        return UUID.randomUUID().toString().replace("-","");
    }
}

package com.example.forum.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 用于MD5加密的工具类
 */
public class MD5Util {
    /**
     * 对字符串进行MD5加密
     * @param str 明文
     * @return 密文
     */
    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    /**
     * 对用户密码进行家里的算法(原密码md5加密后拼接随机字符串,并再次进行MD5加密)
     * @param str 密码明文
     * @param salt 随机盐值
     * @return 密文
     */
    public static String md5Salt(String str, String salt) {
        return md5(md5(str) + salt);

    }
}

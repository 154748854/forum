package com.example.forum.services.impl;

import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceImplTest {
    @Resource
    private IUserService userService;
    @Test
    void createNormalUser() {
        User user = new User();
        user.setUsername("boy");
        user.setNickname("男孩");
        // 定义原始密码
        String password = "123456";
        // 生成盐
        String salt = UUIDUtil.UUID_32();
        // 生成密文
        String ciphertext = MD5Util.md5Salt(password, salt);
        // 设置加密后的密码
        user.setPassword(ciphertext);
        user.setSalt(salt);
        // 调用service方法
        userService.createNormalUser(user);
        // 打印结果
        System.out.println(user);
    }
}
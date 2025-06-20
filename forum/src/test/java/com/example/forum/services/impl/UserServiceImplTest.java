package com.example.forum.services.impl;

import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceImplTest {
    @Resource
    private IUserService userService;
    @Test
    @Transactional
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

    @Test
    void selectByUserName() {
        User user = userService.selectByUserName("bitboy1");
        System.out.println(user);
    }

    @Test
    void login() {
        User u = userService.login("testUser111", "111111");
        System.out.println(u);
    }

    @Test
    void selectById() {
        User user = userService.selectById(1l);
        System.out.println(user);
    }

    @Test
    @Transactional
    void addOneArticleCountById() {
        userService.addOneArticleCountById(1L);
        System.out.println("更新成功");
    }

    @Test
    @Transactional
    void subOneArticleCountById() {
        userService.subOneArticleCountById(6l);
    }

    @Test
    @Transactional
    void modifyInfo() {
        User user = new User();
        user.setId(1L);
        user.setUsername("boy111");
        user.setNickname("boy111");
        user.setGender((byte) 0);
        user.setEmail("qq.@qq.com");
        user.setPhoneNum("12345678907");
        user.setRemark("测试");
        userService.modifyInfo(user);
    }

    @Test
    @Transactional
    void modifyPassword() {
        userService.modifyPassword(7L,"123456","111111");
        System.out.println("修改成功");
    }
}
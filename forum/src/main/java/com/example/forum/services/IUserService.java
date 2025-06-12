package com.example.forum.services;

import com.example.forum.model.User;

/**
 * 用户接口
 */
public interface IUserService {
    /**
     * 创建一个普通用户
     * @param user 用户信息
     */
    void createNormalUser(User user);
}

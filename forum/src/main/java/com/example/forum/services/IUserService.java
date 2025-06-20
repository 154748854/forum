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

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUserName(String username);

    /**
     * 处理用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 根据Id查询用户信息
     * @param id 用户id
     * @return User对象
     */
    User selectById(Long id);


    /**
     * 为当前用户的发帖数+1
     * @param id 用户id
     */
    void addOneArticleCountById(Long id);


    /**
     * 当前用户的发帖数量 -1
     * @param id 用户id
     */
    void subOneArticleCountById(Long id);


    /**
     * 修改个人信息
     * @param user 要更新的对象
     */
    void modifyInfo(User user);


    /**
     * 修改密码
     * @param id 用户id
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     */
    void modifyPassword(Long id, String newPassword, String oldPassword);
}

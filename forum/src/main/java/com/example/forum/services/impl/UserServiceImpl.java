package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.UserMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private UserMapper userMapper;


    @Transactional
    @Override
    public void createNormalUser(User user) {
        // 1. 非空校验
        if (user == null || StringUtil.isEmpty(user.getUsername())
                || StringUtil.isEmpty(user.getNickname())
                || StringUtil.isEmpty(user.getPassword())
                || StringUtil.isEmpty(user.getSalt())) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常, 统一抛Application异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 2. 按用户名查询用户信息
        User existUser = userMapper.selectByUserName(user.getUsername());
        // 2.1 判断用户是否存在
        if (existUser != null) {
            // 打印日志
            log.info(ResultCode.FAILED_USER_EXISTS.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
        }

        // 3. 新增用户
        // 3.1 设置默认值
        user.setGender((byte) 2);
        user.setArticleCount(0);
        user.setIsAdmin((byte) 0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        // 写入数据库
        int row = userMapper.insertSelective(user);
        if (row != 1) {
            // 打印日志
            log.info(ResultCode.FAILED_CREATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        // 打印日志
        log.info("新增用户成功, username:{}",user.getUsername());

    }

    @Override
    public User selectByUserName(String username) {
        // 非空校验
        if (StringUtil.isEmpty(username)) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 返回查询结果
        return userMapper.selectByUserName(username);

    }

    @Override
    public User login(String username, String password) {
        // 1. 非空校验
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        // 2. 用户名查询用户信息
        User user = selectByUserName(username);
        // 3.查询结果做非空校验
        if (user == null) {
            // 打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString() + ", username = " + username);
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        // 4. 对密码做校验
        String encryptPassword = MD5Util.md5Salt(password, user.getSalt());
        // 5. 用密文和数据库中存的用户密码进行比较
        if (!encryptPassword.equalsIgnoreCase(user.getPassword())) {
            // 打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString() + ", 密码错误, username = " + username);
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        // 答应登录成功的日志
        log.info("登录成功, username:{}", username);
        // 登录成功,返回用户信息
        return user;
    }

    @Override
    public User selectById(Long id) {
        // 1. 非空校验
        if (id == null) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        // 调用DAO查询数据库并获取对象
        User user = userMapper.selectByPrimaryKey(id);
        // 返回结果
        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if (id == null || id <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        // 查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            // 打印日志
            log.warn(ResultCode.ERROR_IS_NULL.toString()+", user id = "+id);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        // 更新用户发帖数量
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount()+1);
        // 调用DAO执行更新
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        //判断受影响行数
        if (row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}

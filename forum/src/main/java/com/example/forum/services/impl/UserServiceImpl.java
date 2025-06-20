package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.UserMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.StringUtil;
import com.example.forum.utils.UUIDUtil;
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

    @Override
    public void subOneArticleCountById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
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
        updateUser.setArticleCount(user.getArticleCount()-1);
        // 判断减一之后是否小于零
        if (updateUser.getArticleCount() < 0) {
            // 若小于零,设为零
            updateUser.setArticleCount(0);
        }
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

    @Override
    public void modifyInfo(User user) {
        // 非空校验
        if (user == null || user.getId() == null || user.getId() <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 校验用户是否存在
        User existsUser = userMapper.selectByPrimaryKey(user.getId());
        if (existsUser == null) {
            // 打印日志
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        // 3. 定义一个标志位
        boolean checkAttr = false; // false表示没有校验通过

        // 定义一个用来更新的对象,防止用户传出的User对象设置其他属
        User updateUser = new User();
        updateUser.setId(user.getId());
        // 对每一个参数进行校验并赋值
        // 校验用户名
        if (!StringUtil.isEmpty(user.getUsername())
                && !existsUser.getUsername().equals(user.getUsername())) {
            // 对想更新的用户名进行唯一性校验(用户名不能重复
            User checkUser = userMapper.selectByUserName(user.getUsername());
            if (checkUser != null) {
                // 用户已存在
                log.warn(ResultCode.FAILED_USER_EXISTS.toString());
                // 抛出异常
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
            }
            // 数据库中没有找到相应的用户,表示可以修改用户名
            updateUser.setUsername(user.getUsername());
            // 更新标志位
            checkAttr = true;
        }

        // 校验昵称
        if (!StringUtil.isEmpty(user.getNickname())
                && !user.getNickname().equals(existsUser.getNickname())) {
            // 设置昵称
            updateUser.setNickname(user.getNickname());
            // 更新标志位
            checkAttr = true;
        }

        // 校验性别
        if (user.getGender() != null && user.getGender() != existsUser.getGender()) {
            // 设置性别
            updateUser.setGender(user.getGender());
            // 合法性校验
            if (updateUser.getGender() > 2 || updateUser.getGender() < 0) {
                updateUser.setGender((byte) 2);
            }
            // 更新标志位
            checkAttr = true;
        }
        // 校验邮箱
        if (!StringUtil.isEmpty(user.getEmail())
                && !user.getEmail().equals(existsUser.getEmail())) {
            // 设置邮箱
            updateUser.setEmail(user.getEmail());
            // 更新标志位
            checkAttr = true;
        }
        // 校验电话号码
        if (!StringUtil.isEmpty(user.getPhoneNum())
                && !user.getPhoneNum().equals(existsUser.getPhoneNum())) {
            // 设置电话号码
            updateUser.setPhoneNum(user.getPhoneNum());
            // 更新标志位
            checkAttr = true;
        }
        // 校验个人简介
        if (!StringUtil.isEmpty(user.getRemark())
                && !user.getRemark().equals(existsUser.getRemark())) {
            // 设置个人简介
            updateUser.setRemark(user.getRemark());
            // 更新标志位
            checkAttr = true;
        }
        if (checkAttr == false) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 执行sql
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        //判断受影响行数
        if (row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

    }

    @Override
    public void modifyPassword(Long id, String newPassword, String oldPassword) {
        // 非空校验
        if (id == null || id <= 0 || StringUtil.isEmpty(newPassword) || StringUtil.isEmpty(oldPassword)) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        // 校验用户是否存在
        if (user == null || user.getDeleteState() == 1) {
            // 打印日志
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        // 校验老密码是否正确
        // 先对输入老密码(明文)进行加密
        String oldEncryptPassword = MD5Util.md5Salt(oldPassword, user.getSalt());
        // 与用户当前密码进行比较
        if (!oldEncryptPassword.equalsIgnoreCase(user.getPassword())) {
            // 打印日志
            log.warn(ResultCode.FAILED.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

        // 生成一个新的盐值
        String salt = UUIDUtil.UUID_32();
        // 生成新密码的密文
        String encryptPassword = MD5Util.md5Salt(newPassword,salt);

        // 生成更新对象
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(encryptPassword);
        updateUser.setSalt(salt);
        updateUser.setUpdateTime(new Date());
        // 设置数据库
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

    }


}

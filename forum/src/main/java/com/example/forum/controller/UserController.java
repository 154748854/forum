package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.StringUtil;
import com.example.forum.utils.UUIDUtil;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// 对controller进行api说明
@Api(tags = "用户接口")
@Slf4j
// 这是一份返回数据的Controller
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;
    /**
     * 用户注册
     * @param username 用户名
     * @param nickname 昵称
     * @param password 密码
     * @param passwordRepeat 重复密码
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping("register")
    public AppResult register(@ApiParam("用户名") @RequestParam("username") @NotNull String username,
                              @ApiParam("昵称") @RequestParam("nickname") @NotNull String nickname,
                              @ApiParam("密码") @RequestParam("password") @NotNull String password,
                              @ApiParam("重复密码") @RequestParam("passwordRepeat") @NotNull String passwordRepeat) {
//        if (StringUtil.isEmpty(nickname)
//                || StringUtil.isEmpty(username)
//                || StringUtil.isEmpty(password)
//                || StringUtil.isEmpty(passwordRepeat)) {
//            // 返回错误信息, controller层不抛异常,这是要给用户看的
//            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
//        }

        // 校验密码与重复密码是否相同
        if (!password.equals(passwordRepeat)) {
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }
        // 准备数据
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        // 处理密码
        String salt = UUIDUtil.UUID_32();
        String encryptPassword = MD5Util.md5Salt(password, salt);
        // 设置密码和盐
        user.setSalt(salt);
        user.setPassword(encryptPassword);

        // 调用Service方法
        userService.createNormalUser(user);
        // 返回成功
        return AppResult.success("用户注册成功");
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AppResult login(HttpServletRequest request,
                           @ApiParam("用户名") @RequestParam("username") @NotNull String username,
                           @ApiParam("密码") @RequestParam("password") @NotNull String password) {
        // 1. 调用service中的login方法,返回user对象
        User user = userService.login(username, password);
        if (user == null) {
            // 打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString());
            // 返回结果
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        // 2. 登录成功,把User对象舍之道session作用域中
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.USER_SESSION, user);
        // 3,返回结果
        return AppResult.success();
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/ info")
    public AppResult<User> getUserInfo (HttpServletRequest request,
                                        @ApiParam("用户Id") @RequestParam(value = "id",required = false) Long id) {
        User user = null;
        // 根据入参来判断User获取方式
        if (id == null) {
            //1. 如果id为空(没传, 从session中获取当前登录用户的信息
            // 不主动获取创建session
            HttpSession session = request.getSession(false);
            // 登录了,从session中获取登录用户
            user = (User) session.getAttribute(AppConfig.USER_SESSION);
        }else {
            // 2. id不为空(传过来了,根据id从数据库中查询用户
            user = userService.selectById(id);
        }

        // 对用户的判断,用户是否为空
        if (user == null) {
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        // 返回正常结果(不是直接返回User,而是同一结果返回,User作为data
        return AppResult.success(user);
    }

    @ApiOperation("退出登录")
    @GetMapping("/logout")
    public AppResult logout(HttpServletRequest request) {
        // 获取session对象
        HttpSession session = request.getSession(false);
        // 判断session是否有效
        if (session != null) {
            // 打印日志
            log.info("退出成功");
            // 用户已登录,直接销毁session
            session.invalidate();
        }
        return AppResult.success("退出成功");
    }

    /**
     * 修改个人信息
     * @param username 用户名
     * @param nickname 昵称
     * @param gender 性别
     * @param email 邮箱
     * @param phoneNum 电话号
     * @param remark 简介
     * @return
     */
    @ApiOperation("修改个人信息")
    @PostMapping("/modifyInfo")
    public AppResult modifyInfo (HttpServletRequest request,
                                 @ApiParam("用户名") @RequestParam(value = "username",required = false) String username,
                                 @ApiParam("昵称") @RequestParam(value = "nickname",required = false) String nickname,
                                 @ApiParam("性别") @RequestParam(value = "gender",required = false) Byte gender,
                                 @ApiParam("邮箱") @RequestParam(value = "email",required = false) String email,
                                 @ApiParam("电话号") @RequestParam(value = "phoneNum",required = false) String phoneNum,
                                 @ApiParam("个人简介") @RequestParam(value = "remark",required = false) String remark) {

        if (StringUtil.isEmpty(username) && StringUtil.isEmpty(nickname)
                && StringUtil.isEmpty(email) && StringUtil.isEmpty(phoneNum)
                && StringUtil.isEmpty(remark) && gender == null) {
            // 返回错误信息
            return AppResult.failed("请输入要修改的内容:");
        }

        // 从session中获取用户id
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 封装对象
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(username);
        updateUser.setNickname(nickname);
        updateUser.setGender(gender);
        updateUser.setEmail(email);
        updateUser.setPhoneNum(phoneNum);
        updateUser.setRemark(remark);

        // 调用service
        userService.modifyInfo(updateUser);

        // 查询最新的用户信息
        user = userService.selectById(user.getId());
        // 把最新的用户信息放到session中
        session.setAttribute(AppConfig.USER_SESSION,user);
        return AppResult.success(user);
    }

    @ApiOperation(("修改密码"))
    @PostMapping("/modifyPwd")
    public AppResult modifyPassword(HttpServletRequest request,
                                    @ApiParam("原密码") @RequestParam("oldPassword") @NotNull String oldPassword,
                                    @ApiParam("新密码") @RequestParam("newPassword") @NotNull String newPassword,
                                    @ApiParam("确认密码") @RequestParam("passwordRepeat") @NotNull String passwordRepeat) {
        // 1. 校验新密码与确认密码是否相同
        if (!newPassword.equals(passwordRepeat)) {
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }
        // 2. 获取登录的用户信息
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 调用service
        userService.modifyPassword(user.getId(),newPassword,oldPassword);
        // 销毁session
        if (session != null) {
            session.invalidate();
        }
        // 返回结果
        return AppResult.success();
    }

}

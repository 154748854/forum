package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.UUIDUtil;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}

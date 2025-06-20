package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.Message;
import com.example.forum.model.User;
import com.example.forum.services.IMessageService;
import com.example.forum.services.IUserService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/message")
@Api(tags = "站内信接口")
@Slf4j
public class MessageController {

    @Resource
    private IMessageService messageService;

    @Resource
    private IUserService userService;

    @PostMapping("/send")
    @ApiOperation("发送站内信")
    public AppResult send(HttpServletRequest request,
                          @ApiParam("接受者id") @RequestParam("receiveUserId") @NotNull Long receiveUserId,
                          @ApiParam("内容") @RequestParam("content") @NotNull String content) {
        // 当前登录的用户状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 不能给自己发
        if (receiveUserId == user.getId()) {
            return AppResult.failed("不能给自己发送私信");
        }
        // 校验接受者是否存在
        User receiveUser = userService.selectById(receiveUserId);
        if (receiveUser == null || receiveUser.getDeleteState() == 1) {
            // 返回接搜着状态异常
            return AppResult.failed("接受者状态异常");
        }
        // 封装对象
        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(receiveUserId);
        message.setContent(content);
        // 调用Service
        messageService.create(message);
        // 返回结果
        return AppResult.success("发送成功");
    }

    @ApiOperation("获取未读数")
    @GetMapping("/getUnreadCount")
    public AppResult<Integer> getUnreadCount(HttpServletRequest request) {
        // 获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 调用Service
        Integer count = messageService.selectUnreadCount(user.getId());// 当前登录用户的id就是接受者id
        // 返回结果
        return AppResult.success(count);
    }

    @ApiOperation("查询用户的所有站内信")
    @GetMapping("/getAll")
    public AppResult<List<Message>> getAll(HttpServletRequest request) {
        // 获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 调用service
        List<Message> messages = messageService.selectByReceiveUserId(user.getId());
        // 返回结果
        return AppResult.success(messages);
    }
}

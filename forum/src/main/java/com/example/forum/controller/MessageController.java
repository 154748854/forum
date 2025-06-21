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


    @ApiOperation("更新为已读")
    @PostMapping("/markRead")
    public AppResult markRead(HttpServletRequest request,
                              @ApiParam("站内信id") @RequestParam("id") @NotNull Long id) {
        // 根据id查询站内信
        Message message = messageService.selectById(id);
        // 站内信是否存在
        if (message == null || message.getDeleteState() == 1) {
            // 站内信不存在
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }
        // 站内信是不是自己的
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (message.getReceiveUserId() != user.getId()) {
            // 给自己发了消息
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        // 调用service,将状态更新为已读
        messageService.updateStateById(id, (byte) 1);
        // 返回结果
        return AppResult.success();

    }

    /**
     * 回复站内信
     * @param repliedId 要回复的站内信id
     * @param content 站内信的内容
     * @return AppResult
     */
    @ApiOperation("回复站内信")
    @PostMapping("/reply")
    public AppResult reply(HttpServletRequest request,
                           @ApiParam("要回复的站内信id") @RequestParam("repliedId") @NotNull Long repliedId,
                           @ApiParam("站内信的内容") @RequestParam("content") @NotNull String content) {
        // 校验当前登录用户状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 校验要回复的站内信状态
        Message existsMessage = messageService.selectById(repliedId);
        if (existsMessage == null || existsMessage.getDeleteState() == 1) {
            // 返回错误描述
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }
        // 不能给自己回复
        if (user.getId() == existsMessage.getPostUserId()) {
            // 返回错误描述
            return AppResult.failed("不能回复自己的站内信");
        }
        // 从request获取登录postId,从repliedId获取receiveUserId与content构造message对象
        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(existsMessage.getPostUserId());
        message.setContent(content);
        // 调用service
        messageService.reply(repliedId,message);
        // 返回结果
        return AppResult.success();
    }
}

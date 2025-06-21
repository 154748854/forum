package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.MessageMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Message;
import com.example.forum.model.User;
import com.example.forum.services.IMessageService;
import com.example.forum.services.IUserService;
import com.example.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private IUserService userService;

    @Override
    public void create(Message message) {
        // 非空校验
        if (message == null || message.getPostUserId() == null || message.getReceiveUserId() == null
                || StringUtil.isEmpty(message.getContent())) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 校验接受者是否存在
        User user = userService.selectById(message.getReceiveUserId());
        if (user == null || user.getDeleteState() == 1) {
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 设置默认值
        message.setState((byte) 0); // 表示未读状态
        message.setDeleteState((byte) 0);
        Date date = new Date();
        message.setUpdateTime(date);
        message.setCreateTime(date);

        // 调用DAO写入
        int row = messageMapper.insertSelective(message);
        if (row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public Integer selectUnreadCount(Long receiveUserId) {
        // 非空校验
        if (receiveUserId == null || receiveUserId <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用DAO
        Integer count = messageMapper.selectUnreadCount(receiveUserId);
        // 正常的查询不可能出现null,如果为null则抛出异常
        if (count == null) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        return count;
    }

    @Override
    public Message selectById(Long id) {
        if (id == null || id <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Message message = messageMapper.selectByPrimaryKey(id);
        return message;
    }

    @Override
    public List<Message> selectByReceiveUserId(Long receiveUserId) {
        // 非空校验
        if (receiveUserId == null || receiveUserId <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用DAO
        List<Message> messages = messageMapper.selectByReceiveUserId(receiveUserId);
        return messages;
    }

    @Override
    public void updateStateById(Long id, Byte state) {
        // 非空校验 state: 0 未读  1 已读  2 已回复
        if (id == null || id <= 0 || state < 0 || state > 2) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 构造一个更新对象
        Message updateMessage = new Message();
        updateMessage.setId(id);
        updateMessage.setState(state);
        Date date = new Date();
        updateMessage.setUpdateTime(date);
        // 调用DAO
        int row = messageMapper.updateByPrimaryKeySelective(updateMessage);
        if (row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void reply(Long repliedId, Message message) {
        // 非空校验
        if (repliedId == null || repliedId <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 校验repliedId对应站内信是否存在
        Message existsMessage = messageMapper.selectByPrimaryKey(repliedId);
        if (existsMessage == null || existsMessage.getDeleteState() == 1) {
            // 打印日志
            log.warn(ResultCode.FAILED_MESSAGE_NOT_EXISTS.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS));
        }
        // 更新状态为已回复
        updateStateById(repliedId, (byte) 2);
        // 回复内容写入数据库
        create(message);
    }
}

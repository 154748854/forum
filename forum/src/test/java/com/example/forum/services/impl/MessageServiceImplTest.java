package com.example.forum.services.impl;

import com.example.forum.model.Message;
import com.example.forum.services.IMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class MessageServiceImplTest {

    @Resource
    private IMessageService messageService;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    void create() {
        Message message = new Message();
        message.setPostUserId(2L);
        message.setReceiveUserId(30L);
        message.setContent("我不喜欢你");
        messageService.create(message);
        System.out.println("发送成功");
    }

    @Test
    void selectUnreadCount() {
        Integer count = messageService.selectUnreadCount(1L);
        System.out.println("未读数量:{}" + count);
        count = messageService.selectUnreadCount(20L);
        System.out.println("未读数量:{}" + count);
    }

    @Test
    void selectByReceiveUserId() throws JsonProcessingException {
        List<Message> messages = messageService.selectByReceiveUserId(1L);
        System.out.println(objectMapper.writeValueAsString(messages));
        messages = messageService.selectByReceiveUserId(3L);
        System.out.println(objectMapper.writeValueAsString(messages));
    }

    @Test
    @Transactional
    void updateStateById() {
        messageService.updateStateById(1L, (byte) 2);
        System.out.println("更新成功");
    }

    @Test
    void selectById() throws JsonProcessingException {
        Message message = messageService.selectById(1l);
        System.out.println(objectMapper.writeValueAsString(message));
    }

    @Test
    void reply() {
        Message message = new Message();
        message.setPostUserId(3L);
        message.setContent("你只是个单元测试对象");
        message.setReceiveUserId(2L);
        // 调用service
        messageService.reply(1l,message);
        System.out.println("回复成功");
    }
}
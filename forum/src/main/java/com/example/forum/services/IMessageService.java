package com.example.forum.services;

import com.example.forum.model.Message;

import java.util.List;

public interface IMessageService {

    /**
     * 发送站内信息
     * @param message 站内信对象
     */
    void create (Message message);

    /**
     * 根据用户id查询给用户的未读信息数
     * @param receiveUserId 用户id
     * @return 未读数量
     */
    Integer selectUnreadCount(Long receiveUserId);


    /**
     * 根据接受者用户Id查询所有站内信
     * @param receiveUserId 接受者用户id
     * @return List<Message>
     */
    List<Message> selectByReceiveUserId(Long receiveUserId);

    /**
     * 更新指定站内信的状态
     * @param id 站内信Id
     * @param state 目标状态
     */
    void updateStateById(Long id, Byte state);
}

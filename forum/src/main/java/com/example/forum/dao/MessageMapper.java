package com.example.forum.dao;

import com.example.forum.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    /**
     * 根据用户id查询给用户的未读信息数
     * @param receiveUserId 用户id
     * @return 未读数量
     */
    Integer selectUnreadCount(@Param("receiveUserId") Long receiveUserId);

    /**
     * 根据接受者用户Id查询所有站内信
     * @param receiveUserId 接受者用户id
     * @return List<Message>
     */
    List<Message> selectByReceiveUserId(@Param("receiveUserId") Long receiveUserId);
}
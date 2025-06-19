package com.example.forum.model;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleReply {
    // 编号(主键
    private Long id;

    // 帖子id,关联article
    private Long articleId;

    // 回复用户的编号
    private Long postUserId;

    // 忽略,需求中楼中楼功能
    private Long replyId;

    // 忽略,楼中楼功能
    private Long replyUserId;

    // 回复的正文
    private String content;

    // 忽略,需求中回复点赞功能
    private Integer likeCount;

    // 状态 0 正常  1 禁用
    private Byte state;

    // 状态 0 正常  1 删除
    private Byte deleteState;

    private Date createTime;

    private Date updateTime;

    // 关联对象  -回复的发布者
    private User user;
}
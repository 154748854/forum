package com.example.forum.services.impl;

import com.example.forum.model.ArticleReply;
import com.example.forum.services.IArticleReplyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class ArticleReplyServiceImplTest {

    @Resource
    private IArticleReplyService articleReplyService;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    void create() {
        // 构造回复对象
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(5L);
        articleReply.setPostUserId(4L);
        articleReply.setContent("单元测试回复");
        articleReplyService.create(articleReply);
        System.out.println("回复成功");
    }

    @Test
    void selectByArticleId() throws JsonProcessingException {
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(1L);
        System.out.println(objectMapper.writeValueAsString(articleReplies));
    }
}
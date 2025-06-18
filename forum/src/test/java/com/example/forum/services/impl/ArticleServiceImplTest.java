package com.example.forum.services.impl;

import com.example.forum.model.Article;
import com.example.forum.services.IArticleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class ArticleServiceImplTest {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IArticleService articleService;
    @Test
    @Transactional
    void create() {
        Article article = new Article();
        article.setUserId(1L);
        article.setBoardId(1L);
        article.setTitle("单元测试");
        article.setContent("测试内容");
        articleService.create(article);
        System.out.println("发帖成功");
    }

    @Test
    void selectAll() throws JsonProcessingException {
        // 调用service
        List<Article> articles = articleService.selectAll();
        // 转换成Json并打印
        System.out.println(objectMapper.writeValueAsString(articles));
    }

    @Test
    void selectAllByBoardId() throws JsonProcessingException {
        List<Article> articles = articleService.selectAllByBoardId(12L);
        System.out.println(objectMapper.writeValueAsString(articles));
    }

    @Test
    void selectDetailById() throws JsonProcessingException {
        Article article = articleService.selectDetailById(1L);
        System.out.println(objectMapper.writeValueAsString(article));
    }

    @Test
    @Transactional
    void modify() {
        articleService.modify(1L,"单元测试111","测试内容111");
        System.out.println("更新成功");
    }

    @Test
    void selectById() throws JsonProcessingException {
        Article article = articleService.selectById(1L);
        System.out.println(objectMapper.writeValueAsString(article));
    }

    @Test
    @Transactional
    void thumbsUpById() {
        articleService.thumbsUpById(6l);
        System.out.println("点赞成功");
    }

    @Test
    @Transactional
    void deleteById() {
        articleService.deleteById(6L);
        System.out.println("删除成功");
    }
}
package com.example.forum.services.impl;

import com.example.forum.model.Article;
import com.example.forum.services.IArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ArticleServiceImplTest {

    @Resource
    private IArticleService articleService;
    @Test
    void create() {
        Article article = new Article();
        article.setUserId(1L);
        article.setBoardId(1L);
        article.setTitle("单元测试");
        article.setContent("测试内容");
        articleService.create(article);
        System.out.println("发帖成功");
    }
}
package com.example.forum.services;

import com.example.forum.model.Article;
import org.springframework.transaction.annotation.Transactional;

public interface IArticleService {
    /**
     * 发布帖子
     * @param article 要发布的帖子
     */
    @Transactional // 当前方法中的执行过程会被事务管理起来
    void create(Article article);
}

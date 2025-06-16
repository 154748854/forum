package com.example.forum.services;

import com.example.forum.model.Article;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleService {
    /**
     * 发布帖子
     * @param article 要发布的帖子
     */
    @Transactional // 当前方法中的执行过程会被事务管理起来
    void create(Article article);

    /**
     * 查询所有帖子列表
     * @return
     */
    List<Article> selectAll();

    /**
     * 根据板块Id查询所有帖子列表
     * @param boardId
     * @return
     */
    List<Article> selectAllByBoardId(Long boardId);
}

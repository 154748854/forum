package com.example.forum.services;

import com.example.forum.model.Board;

import java.util.List;

/**
 * 板块接口
 */
public interface IBoardService {
    /**
     * 查询num条记录
     * @param num 要查询的条数
     * @return
     */
    List<Board> selectByNum(Integer num);

    /**
     * 根据板块Id查询板块
     * @param id 板块id
     * @return 板块对象
     */
    Board selectById(Long id);

    /**
     * 更新板块中的帖子数量
     * @param id 板块id
     */
    void addOneArticleCountById(Long id);

    /**
     * 板块中帖子数量 -1
     * @param id 板块id
     */
    void subOneArticleCountById(Long id);
}

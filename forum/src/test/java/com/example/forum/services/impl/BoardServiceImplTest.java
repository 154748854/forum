package com.example.forum.services.impl;

import com.example.forum.services.IBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@SpringBootTest
class BoardServiceImplTest {

    @Resource
    private IBoardService boardService;

    @Test
    void selectByNum() {
        System.out.println(boardService.selectByNum(1));
    }

    @Test
    @Transactional
    void addOneArticleCountById() {
        boardService.addOneArticleCountById(1L);
        System.out.println("更新成功");
    }
}
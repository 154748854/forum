package com.example.forum.services.impl;

import com.example.forum.services.IBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceImplTest {

    @Resource
    private IBoardService boardService;

    @Test
    void selectByNum() {
        System.out.println(boardService.selectByNum(1));
    }
}
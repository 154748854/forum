package com.example.forum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
// 表示返回的结果是数据
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("test1")
    public String test1() {
        return "hello, Spring";
    }
}

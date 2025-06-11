package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.exception.ApplicationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
// 表示返回的结果是数据
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("hello")
    public String test1() {
        return "hello, Spring";
    }

    @GetMapping("/exception")
    public AppResult testException() throws Exception {
        throw new Exception("这是一个Exception..");
    }

    @GetMapping("/appException")
    public AppResult applicationException () {
        throw new ApplicationException("这是一个ApplicationException");
    }
}

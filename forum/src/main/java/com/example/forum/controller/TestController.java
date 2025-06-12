package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.exception.ApplicationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 *
 */

@Api(tags = "测试类的相关接口")
// 表示返回的结果是数据
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation("测试接口1, 显示hello, Spring")
    @GetMapping("hello")
    public String test1() {
        return "hello, Spring";
    }

    @ApiOperation("测试接口4, 按传入的信命显示 hello: name")
    @PostMapping("helloByName")
    public AppResult helloByName(@ApiParam("姓名") @RequestParam("name") String name) {
        return AppResult.success(name);
    }




    @ApiOperation("测试接口1, 显示抛出的异常信息")
    @GetMapping("/exception")
    public AppResult testException() throws Exception {
        throw new Exception("这是一个Exception..");
    }

    @ApiOperation("测试接口1, 显示自定义的异常信息")
    @GetMapping("/appException")
    public AppResult applicationException () {
        throw new ApplicationException("这是一个ApplicationException");
    }
}

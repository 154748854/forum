package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Board;
import com.example.forum.services.IBoardService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "板块接口")
@RestController
@RequestMapping("/board")
public class BoardController {

    @Value("${forum.index.board-num:9}")
    private Integer indexBoardNum;

    @Resource
    private IBoardService boardService;
    /**
     * 查询板块列表
     * @return
     */
    @ApiOperation("获取首页板块列表")
    @GetMapping("/topList")
    public AppResult<List<Board>> topList() {
        log.info("首页板块个数为:{}",indexBoardNum);
        // 调用service查询结果
        List<Board> boards = boardService.selectByNum(indexBoardNum);
        if (boards == null) {
            // new一个是为了方便序列化统一返回结果
            boards = new ArrayList<>();
        }
        // 返回结果
        return AppResult.success(boards);
    }


    @ApiOperation("获取板块信息")
    @GetMapping("/getById")
    public AppResult<Board> getById(@ApiParam("板块id") @RequestParam("id") @NotNull Long id) {
        // 调用Service
        Board board = boardService.selectById(id);
        // 对查询结果进行校验
        if (board == null || board.getDeleteState() == 1) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        // 返回结果
        return AppResult.success(board);
    }
}

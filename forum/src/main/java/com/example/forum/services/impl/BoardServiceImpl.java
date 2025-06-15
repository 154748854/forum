package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.BoardMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Board;
import com.example.forum.services.IBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class BoardServiceImpl implements IBoardService {

    @Resource
    private BoardMapper boardMapper;

    @Override
    public List<Board> selectByNum(Integer num) {
        // 非空校验
        if (num <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用DAO
        List<Board> result = boardMapper.selectByNum(num);
        // 使用方去校验,此处的service不做校验
        return result;
    }

    @Override
    public Board selectById(Long id) {
        // 非空校验
        if (id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用DAO查询结果
        Board board = boardMapper.selectByPrimaryKey(id);
        return board;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if (id == null || id <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        // 根据id查询板块信息
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString()+", board id = "+id);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        // 更新帖子数量
        // new 一个是因为要用Selective的语句,不是在原来的板块信息上改的
        Board updateBoard = new Board();
        updateBoard.setId(board.getId());
        // 调用DAO执行更新
        updateBoard.setArticleCount(board.getArticleCount()+1);
        //判断受影响行数
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        if (row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}

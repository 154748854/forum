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
}

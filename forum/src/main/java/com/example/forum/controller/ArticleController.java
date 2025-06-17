package com.example.forum.controller;


import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.Article;
import com.example.forum.model.Board;
import com.example.forum.model.User;
import com.example.forum.services.IArticleService;
import com.example.forum.services.IBoardService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "文章接口")
@RestController
@Slf4j
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;
    @Resource
    private IBoardService boardService;

    /**
     * 发布新帖子
     * @param boardId 板块Id
     * @param title 文章标题
     * @param content 文章内容
     * @return
     */
    @ApiOperation("发布新帖")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("板块Id") @RequestParam("boardId") @NotNull Long boardId,
                            @ApiParam("文章标题") @RequestParam("title") @NotNull String title,
                            @ApiParam("文章内容") @RequestParam("content") @NotNull String content) {
        // 校验用户是否禁言
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            // 表示用户已禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 板块校验
        Board board = boardService.selectById(boardId);
        if (board == null || board.getDeleteState() == 1 || board.getState() == 1) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            // 返回错误信息
            return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
        }
        // 封装文章对象(因为service接收的是Article对象
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setBoardId(boardId);
        article.setUserId(user.getId());
        // 调用service
        articleService.create(article);

        // 发帖成功
        return AppResult.success();
    }

    @ApiOperation("获取帖子列表")
    @GetMapping("/getAllByBoardId")
    public AppResult<List<Article>> getAllByBoardId(@ApiParam("板块Id") @RequestParam(value = "boardId",required = false) Long boardId) {

        // 定义返回的结果集
        List<Article> articles = null;

        if (boardId == null) {
            // 查询所有
            articles = articleService.selectAll();
        }else {
            articles = articleService.selectAllByBoardId(boardId);
        }

        // 判断结果集是否为空
        if (articles == null) {
            articles = new ArrayList<>();
        }

        return AppResult.success(articles);
    }

    @ApiOperation("根据帖子id获取详情")
    @GetMapping("/details")
    public AppResult<Article> getDetails(@ApiParam("帖子id") @RequestParam("id") @NotNull Long id) {
        // 调用Service获取帖子详情
        Article article = articleService.selectDetailById(id);
        // 判断结果是否为空
        if (article == null) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        // 返回结果
        return AppResult.success(article);
    }

}

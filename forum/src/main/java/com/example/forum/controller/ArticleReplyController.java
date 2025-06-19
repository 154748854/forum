package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.Article;
import com.example.forum.model.ArticleReply;
import com.example.forum.model.User;
import com.example.forum.services.IArticleReplyService;
import com.example.forum.services.IArticleService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Api(tags = "回复接口")
@RestController
@Slf4j
@RequestMapping("/reply")
public class ArticleReplyController {

    @Resource
    private IArticleService articleService;

    @Resource
    private IArticleReplyService articleReplyService;

    @ApiOperation("回复帖子")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("帖子id") @RequestParam("articleId") @NotNull Long articleId,
                            @ApiParam("帖子内容") @RequestParam("content") @NotNull String content) {
        // 获取用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 判断用户状态
        if (user.getState() == 1) {
            // 已禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 获取要回复的帖子信息
        Article article = articleService.selectById(articleId);
        // 校验帖子对象
        if (article == null || article.getDeleteState() == 1) {
            // 已删除或不存在
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        if (article.getState() == 1) {
            // 已封贴
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }

        // 构造更新对象
        ArticleReply updateArticleReply = new ArticleReply();
        updateArticleReply.setArticleId(articleId); // 要回复的帖子id
        updateArticleReply.setPostUserId(user.getId()); // 回复者的id
        updateArticleReply.setContent(content); // 回复内容
        // 写入数据库
        articleReplyService.create(updateArticleReply);
        // 返回结果

        return AppResult.success();
    }

    @ApiOperation("获取回复列表")
    @GetMapping("/getReplies")
    public AppResult<List<ArticleReply>> getRepliesByArticleId(@ApiParam("帖子id") @RequestParam("articleId") @NotNull Long articleId) {
        // 校验帖子是否存在
        Article article = articleService.selectById(articleId);
        if (article == null || article.getState() == 1) {
            // 返回错误提示
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        // 调用service
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(articleId);
        // 返回结果
        return AppResult.success(articleReplies);
    }
}

package com.zhouyuanzhi.weblog.web.controller;

import com.zhouyuanzhi.weblog.common.aspect.ApiOperationLog;
import com.zhouyuanzhi.weblog.common.utils.Response;
import com.zhouyuanzhi.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.zhouyuanzhi.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.zhouyuanzhi.weblog.web.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zyz
 * @description: 文章
 **/
@RestController
@RequestMapping("/article")
@Tag(name = "文章")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/list")
    @Operation(summary = "获取首页文章分页数据")
    @ApiOperationLog(description = "获取首页文章分页数据")
    public Response findArticlePageList(@RequestBody FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        return articleService.findArticlePageList(findIndexArticlePageListReqVO);
    }


    @PostMapping("/detail")
    @Operation(summary = "获取文章详情")
    @ApiOperationLog(description = "获取文章详情")
    public Response findArticleDetail(@RequestBody FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }

}

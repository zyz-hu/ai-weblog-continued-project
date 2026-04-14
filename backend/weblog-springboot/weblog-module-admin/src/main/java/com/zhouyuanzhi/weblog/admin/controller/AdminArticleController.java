package com.zhouyuanzhi.weblog.admin.controller;

import com.zhouyuanzhi.weblog.admin.model.vo.article.*;
import com.zhouyuanzhi.weblog.admin.service.AdminArticleService;
import com.zhouyuanzhi.weblog.common.aspect.ApiOperationLog;
import com.zhouyuanzhi.weblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zyz
 * @description: 文章模块
 **/
@RestController
@RequestMapping("/admin/article")
@Tag(name = "Admin 文章模块")
public class AdminArticleController {

    @Autowired
    private AdminArticleService articleService;

    @PostMapping("/publish")
    @Operation(summary = "文章发布")
    @ApiOperationLog(description = "文章发布")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return articleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "文章删除")
    @ApiOperationLog(description = "文章删除")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO) {
        return articleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/list")
    @Operation(summary = "查询文章分页数据")
    @ApiOperationLog(description = "查询文章分页数据")
    public Response findArticlePageList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO) {
        return articleService.findArticlePageList(findArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @Operation(summary = "查询文章详情")
    @ApiOperationLog(description = "查询文章详情")
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticlePageListReqVO) {
        return articleService.findArticleDetail(findArticlePageListReqVO);
    }

    @PostMapping("/update")
    @Operation(summary = "更新文章")
    @ApiOperationLog(description = "更新文章")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO) {
        return articleService.updateArticle(updateArticleReqVO);
    }

}

package com.zhouyuanzhi.weblog.web.controller;

import com.zhouyuanzhi.weblog.common.aspect.ApiOperationLog;
import com.zhouyuanzhi.weblog.common.utils.Response;
import com.zhouyuanzhi.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.zhouyuanzhi.weblog.web.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zyz
 * @description: 分类
 **/
@RestController
@RequestMapping("/category")
@Tag(name = "分类")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    @Operation(summary = "前台获取分类列表")
    @ApiOperationLog(description = "前台获取分类列表")
    public Response findCategoryList() {
        return categoryService.findCategoryList();
    }

    @PostMapping("/article/list")
    @Operation(summary = "前台获取分类下文章分页数据")
    @ApiOperationLog(description = "前台获取分类下文章分页数据")
    public Response findCategoryArticlePageList(@RequestBody @Validated FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO) {
        return categoryService.findCategoryArticlePageList(findCategoryArticlePageListReqVO);
    }

}

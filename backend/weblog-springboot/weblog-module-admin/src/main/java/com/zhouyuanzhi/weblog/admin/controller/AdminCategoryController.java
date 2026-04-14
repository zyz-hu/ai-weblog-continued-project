package com.zhouyuanzhi.weblog.admin.controller;

import com.zhouyuanzhi.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.zhouyuanzhi.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.zhouyuanzhi.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.zhouyuanzhi.weblog.admin.service.AdminCategoryService;
import com.zhouyuanzhi.weblog.common.aspect.ApiOperationLog;
import com.zhouyuanzhi.weblog.common.utils.PageResponse;
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
 * @description: 分类
 **/
@RestController
@RequestMapping("/admin/category")
@Tag(name = "Admin 分类模块")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService categoryService;

    @PostMapping("/add")
    @Operation(summary = "添加分类")
    @ApiOperationLog(description = "添加分类")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO) {
        return categoryService.addCategory(addCategoryReqVO);
    }

    @PostMapping("/list")
    @Operation(summary = "分类分页数据获取")
    @ApiOperationLog(description = "分类分页数据获取")
    public PageResponse findCategoryPageList(@RequestBody @Validated FindCategoryPageListReqVO findCategoryPageListReqVO) {
        return categoryService.findCategoryPageList(findCategoryPageListReqVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除分类")
    @ApiOperationLog(description = "删除分类")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteCategory(@RequestBody @Validated DeleteCategoryReqVO deleteCategoryReqVO) {
        return categoryService.deleteCategory(deleteCategoryReqVO);
    }

    @PostMapping("/select/list")
    @Operation(summary = "分类 Select 下拉列表数据获取")
    @ApiOperationLog(description = "分类 Select 下拉列表数据获取")
    public Response findCategorySelectList() {
        return categoryService.findCategorySelectList();
    }


}

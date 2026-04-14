package com.zhouyuanzhi.weblog.admin.controller;

import com.zhouyuanzhi.weblog.admin.service.AdminDashboardService;
import com.zhouyuanzhi.weblog.common.aspect.ApiOperationLog;
import com.zhouyuanzhi.weblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zyz
 * @description: 仪表盘
 **/
@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "Admin 仪表盘")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @PostMapping("/statistics")
    @Operation(summary = "获取后台仪表盘基础统计信息")
    @ApiOperationLog(description = "获取后台仪表盘基础统计信息")
    public Response findDashboardStatistics() {
        return dashboardService.findDashboardStatistics();
    }

    @PostMapping("/publishArticle/statistics")
    @Operation(summary = "获取后台仪表盘文章发布热点统计信息")
    @ApiOperationLog(description = "获取后台仪表盘文章发布热点统计信息")
    public Response findDashboardPublishArticleStatistics() {
        return dashboardService.findDashboardPublishArticleStatistics();
    }

}

package com.zhouyuanzhi.weblog.web.controller;

import com.zhouyuanzhi.weblog.common.aspect.ApiOperationLog;
import com.zhouyuanzhi.weblog.common.utils.Response;
import com.zhouyuanzhi.weblog.web.service.BlogSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zyz
 * @description: 博客设置
 **/
@RestController
@RequestMapping("/blog/settings")
@Tag(name = "博客设置")
public class BlogSettingsController {

    @Autowired
    private BlogSettingsService blogSettingsService;

    @PostMapping("/detail")
    @Operation(summary = "前台获取博客详情")
    @ApiOperationLog(description = "前台获取博客详情")
    public Response findDetail() {
        return blogSettingsService.findDetail();
    }

}

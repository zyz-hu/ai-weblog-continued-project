package com.zhouyuanzhi.weblog.admin.service;

import com.zhouyuanzhi.weblog.common.utils.Response;

/**
 * @author: zyz
 * @description: 仪表盘
 **/
public interface AdminDashboardService {

    /**
     * 获取仪表盘基础统计信息
     * @return
     */
    Response findDashboardStatistics();

    /**
     * 获取文章发布热点统计信息
     * @return
     */
    Response findDashboardPublishArticleStatistics();
}

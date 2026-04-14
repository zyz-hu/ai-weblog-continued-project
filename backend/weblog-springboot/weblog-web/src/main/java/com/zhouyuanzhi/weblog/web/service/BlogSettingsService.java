package com.zhouyuanzhi.weblog.web.service;

import com.zhouyuanzhi.weblog.common.utils.Response;

/**
 * @author: zyz
 * @description: 博客设置
 **/
public interface BlogSettingsService {
    /**
     * 获取博客设置信息
     * @return
     */
    Response findDetail();
}

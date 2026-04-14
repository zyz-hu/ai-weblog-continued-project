package com.zhouyuanzhi.weblog.admin.service;

import com.zhouyuanzhi.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.zhouyuanzhi.weblog.common.utils.Response;

/**
 * @author: zyz
 * @description: TODO
 **/
public interface AdminBlogSettingsService {
    /**
     * 更新博客设置信息
     * @param updateBlogSettingsReqVO
     * @return
     */
    Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO);

    /**
     * 获取博客设置详情
     * @return
     */
    Response findDetail();
}

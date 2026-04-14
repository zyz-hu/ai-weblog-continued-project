package com.zhouyuanzhi.weblog.web.service;

import com.zhouyuanzhi.weblog.common.utils.Response;
import com.zhouyuanzhi.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;

/**
 * @author: zyz
 * @description: 分类
 **/
public interface TagService {
    /**
     * 获取标签列表
     * @return
     */
    Response findTagList();

    /**
     * 获取标签下文章分页列表
     * @param findTagArticlePageListReqVO
     * @return
     */
    Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO);
}

package com.zhouyuanzhi.weblog.web.service;

import com.zhouyuanzhi.weblog.common.utils.Response;
import com.zhouyuanzhi.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;

/**
 * @author: zyz
 * @description: 分类
 **/
public interface CategoryService {
    /**
     * 获取分类列表
     * @return
     */
    Response findCategoryList();

    /**
     * 获取分类下文章分页数据
     * @param findCategoryArticlePageListReqVO
     * @return
     */
    Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO);
}

package com.zhouyuanzhi.weblog.web.service;

import com.zhouyuanzhi.weblog.common.utils.Response;
import com.zhouyuanzhi.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.zhouyuanzhi.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;

/**
 * @author: zyz
 * @description: 文章
 **/
public interface ArticleService {
    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);

    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

}

package com.zhouyuanzhi.weblog.web.service;

import com.zhouyuanzhi.weblog.common.utils.Response;
import com.zhouyuanzhi.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;

/**
 * @author: zyz
 * @description: 归档文章
 **/
public interface ArchiveService {
    /**
     * 获取文章归档分页数据
     * @param findArchiveArticlePageListReqVO
     * @return
     */
    Response findArchivePageList(FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO);
}

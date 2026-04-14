package com.zhouyuanzhi.weblog.admin.convert;

import com.zhouyuanzhi.weblog.admin.model.vo.article.FindArticleDetailRspVO;
import com.zhouyuanzhi.weblog.common.domain.dos.ArticleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author: zyz
 * @description: 文章详情转换
 **/
@Mapper
public interface ArticleDetailConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleDetailConvert INSTANCE = Mappers.getMapper(ArticleDetailConvert.class);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    FindArticleDetailRspVO convertDO2VO(ArticleDO bean);

}

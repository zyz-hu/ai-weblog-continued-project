package com.zhouyuanzhi.weblog.web.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 周元智
 * @url: www.zhouyuanzhi.com
 * @date: 2025-09-15 14:07
 * @description: 文章详情
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "查询文章详情 VO")
public class FindArticleDetailReqVO {
    /**
     * 文章 ID
     */
    private Long articleId;
}

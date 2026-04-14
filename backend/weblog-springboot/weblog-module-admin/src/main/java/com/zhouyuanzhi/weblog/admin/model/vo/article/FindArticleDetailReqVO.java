package com.zhouyuanzhi.weblog.admin.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

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
@Schema(description = "查询文章详情入参 VO")
public class FindArticleDetailReqVO {

    /**
     * 文章 ID
     */
    @NotNull(message = "文章 ID 不能为空")
    private Long id;

}

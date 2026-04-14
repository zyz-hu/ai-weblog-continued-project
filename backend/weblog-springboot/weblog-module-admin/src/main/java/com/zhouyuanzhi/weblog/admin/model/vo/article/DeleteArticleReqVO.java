package com.zhouyuanzhi.weblog.admin.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * @author: zyz
 * @description: 删除文章
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "删除文章 VO")
public class DeleteArticleReqVO {

    @NotNull(message = "文章 ID 不能为空")
    private Long id;
}

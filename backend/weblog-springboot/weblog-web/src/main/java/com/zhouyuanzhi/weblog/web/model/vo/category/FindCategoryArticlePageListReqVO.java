package com.zhouyuanzhi.weblog.web.model.vo.category;

import com.zhouyuanzhi.weblog.common.model.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * @author: zyz
 * @description: 分类文章
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCategoryArticlePageListReqVO extends BasePageQuery {

    /**
     * 分类 ID
     */
    @NotNull(message = "分类 ID 不能为空")
    private Long id;

}

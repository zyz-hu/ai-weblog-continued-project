package com.zhouyuanzhi.weblog.web.model.vo.tag;

import com.zhouyuanzhi.weblog.common.model.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * @author: zyz
 * @description: 标签下文章列表
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTagArticlePageListReqVO extends BasePageQuery {

    /**
     * 标签 ID
     */
    @NotNull(message = "标签 ID 不能为空")
    private Long id;

}

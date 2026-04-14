package com.zhouyuanzhi.weblog.web.model.vo.article;

import com.zhouyuanzhi.weblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author: zyz
 * @description: 首页-文章分页
 **/
@Data
@Builder
@Schema(description = "首页查询文章分页 VO")
public class FindIndexArticlePageListReqVO extends BasePageQuery {
}

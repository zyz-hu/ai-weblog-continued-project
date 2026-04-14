package com.zhouyuanzhi.weblog.web.model.vo.archive;

import com.zhouyuanzhi.weblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author: zyz
 * @description: 文章归档
 **/
@Data
@Builder
@Schema(description = "文章归档分页 VO")
public class FindArchiveArticlePageListReqVO extends BasePageQuery {
}

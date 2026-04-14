package com.zhouyuanzhi.weblog.web.model.vo.archive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

/**
 * @author: zyz
 * @description: 文章归档
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArchiveArticlePageListRspVO {
    /**
     * 归档的月份
     */
    private YearMonth month;

    private List<FindArchiveArticleRspVO> articles;

}

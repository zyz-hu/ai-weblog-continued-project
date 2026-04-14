package com.zhouyuanzhi.weblog.admin.model.vo.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author: zyz
 * @description: 分类分页
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCategoryPageListRspVO {

    /**
     * 分类 ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}

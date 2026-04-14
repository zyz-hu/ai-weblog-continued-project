package com.zhouyuanzhi.ai.robot.model.common;

import lombok.Data;

/**
 * @author: 周元智
 * @url: www.zhouyuanzhi.com
 * @date: 2023-09-19 8:54
 * @description: TODO
 **/
@Data
public class BasePageQuery {
    /**
     * 当前页码, 默认第一页
     */
    private Long current = 1L;
    /**
     * 每页展示的数据数量，默认每页展示 10 条数据
     */
    private Long size = 10L;
}

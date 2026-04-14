package com.zhouyuanzhi.ai.robot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 私有知识库 Markdown 文件处理状态
 */
@Getter
@AllArgsConstructor
public enum AiCustomerServiceMdStatusEnum {

    PENDING(0, "待处理"),
    VECTORIZING(1, "向量化中"),
    COMPLETED(2, "已完成"),
    FAILED(3, "失败");

    private final Integer code;
    private final String description;
}

package com.zhouyuanzhi.ai.robot.model.vo.knowledge;

import lombok.Data;

/**
 * 知识库文件分页请求
 */
@Data
public class KnowledgeFilePageReqVO {

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    /**
     * 关键字（原始文件名匹配）
     */
    private String keyword;

    /**
     * 处理状态筛选
     */
    private Integer status;
}

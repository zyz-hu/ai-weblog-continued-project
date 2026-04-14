package com.zhouyuanzhi.ai.robot.model.vo.knowledge;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库文件分页响应
 */
@Data
@Builder
public class KnowledgeFilePageRspVO {

    private Long id;

    private String originalFileName;

    private String newFileName;

    private String remark;

    private Integer status;

    private Long fileSize;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

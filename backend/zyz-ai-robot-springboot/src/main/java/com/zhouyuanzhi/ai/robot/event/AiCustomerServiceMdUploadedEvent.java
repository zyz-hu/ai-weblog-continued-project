package com.zhouyuanzhi.ai.robot.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 知识库 Markdown 文件上传事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiCustomerServiceMdUploadedEvent {

    /**
     * t_ai_customer_service_md_storage 表主键
     */
    private Long id;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 向量存储的元数据
     */
    private Map<String, Object> metadatas;
}

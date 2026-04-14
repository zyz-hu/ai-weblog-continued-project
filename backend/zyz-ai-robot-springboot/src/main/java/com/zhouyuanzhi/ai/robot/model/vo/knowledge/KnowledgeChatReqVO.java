package com.zhouyuanzhi.ai.robot.model.vo.knowledge;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 私有知识库对话请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnowledgeChatReqVO {

    @NotBlank(message = "用户问题不能为空")
    private String message;

    @NotBlank(message = "调用模型名称不能为空")
    private String modelName;

    private Double temperature = 0.7;

    /**
     * 检索召回数量
     */
    private Integer topK = 4;

    /**
     * 指定仅使用的知识文件ID集合
     */
    private List<Long> mdStorageIds;
}

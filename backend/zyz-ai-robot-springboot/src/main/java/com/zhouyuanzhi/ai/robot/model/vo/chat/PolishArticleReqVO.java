package com.zhouyuanzhi.ai.robot.model.vo.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章润色入参
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolishArticleReqVO {

    @NotBlank(message = "文章内容不能为空")
    private String article;

    /**
     * 自定义的润色提示词
     */
    private String prompt;

    @NotBlank(message = "调用的 AI 大模型名称不能为空")
    private String modelName;

    /**
     * 温度值，默认 0.7
     */
    private Double temperature = 0.7;
}

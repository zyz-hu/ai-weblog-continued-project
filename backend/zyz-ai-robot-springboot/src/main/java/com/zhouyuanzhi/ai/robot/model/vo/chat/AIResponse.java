package com.zhouyuanzhi.ai.robot.model.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 周元智
 * @Date: 2025/6/15 9:00
 * @Version: v1.0.0
 * @Description: AI 对话响应类
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
    // 流式响应内容
    private String v;

    /**
     * 消息类型:
     * "reasoning" - 思考过程
     * "content" - 正文内容
     */
    private String type;
}

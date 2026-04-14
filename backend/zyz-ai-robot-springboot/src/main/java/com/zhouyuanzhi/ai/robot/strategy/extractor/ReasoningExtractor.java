package com.zhouyuanzhi.ai.robot.strategy.extractor;

import org.springframework.ai.chat.client.ChatClientResponse;

/**
 * 思考过程提取器接口
 * 不同模型(DeepSeek, OpenAI o1)有不同的提取逻辑
 */
public interface ReasoningExtractor {

    /**
     * 从响应中提取思考过程片段
     * @param response 单次流式响应
     * @return 思考内容 (如果没有则返回 null)
     */
    String extract(ChatClientResponse response);
}
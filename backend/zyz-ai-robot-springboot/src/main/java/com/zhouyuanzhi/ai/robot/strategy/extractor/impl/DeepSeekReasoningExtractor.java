package com.zhouyuanzhi.ai.robot.strategy.extractor.impl;

import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class DeepSeekReasoningExtractor implements ReasoningExtractor {

    @Override
    public String extract(ChatClientResponse response) {
        // 1. 基础判空
        if (response == null || response.chatResponse() == null || response.chatResponse().getResult() == null) {
            return null;
        }

        // 2. 获取 Output 消息对象
        AssistantMessage output = response.chatResponse().getResult().getOutput();

        // 3. 核心修复：判断是否为 DeepSeek 专用消息类型
        if (output instanceof DeepSeekAssistantMessage) {
            DeepSeekAssistantMessage deepSeekMessage = (DeepSeekAssistantMessage) output;

            // 直接调用 getReasoningContent() 方法获取
            String reasoning = deepSeekMessage.getReasoningContent();

            // 判空返回
            if (StringUtils.hasText(reasoning)) {
                return reasoning;
            }
        }

        return null;
    }
}
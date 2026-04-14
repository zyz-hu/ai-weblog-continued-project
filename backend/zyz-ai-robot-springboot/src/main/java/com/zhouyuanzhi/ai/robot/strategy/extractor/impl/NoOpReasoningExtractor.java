package com.zhouyuanzhi.ai.robot.strategy.extractor.impl;

import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.stereotype.Component;

@Component
public class NoOpReasoningExtractor implements ReasoningExtractor {
    @Override
    public String extract(ChatClientResponse response) {
        return null; // 不做任何提取
    }
}
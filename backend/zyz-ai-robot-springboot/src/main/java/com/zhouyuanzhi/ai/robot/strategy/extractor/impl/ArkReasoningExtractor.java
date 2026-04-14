package com.zhouyuanzhi.ai.robot.strategy.extractor.impl;

import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ArkReasoningExtractor implements ReasoningExtractor {

    @Override
    public String extract(ChatClientResponse response) {
        if (response == null || response.chatResponse() == null || response.chatResponse().getResult() == null) {
            return null;
        }
        AssistantMessage output = response.chatResponse().getResult().getOutput();
        if (output == null || output.getMetadata() == null) {
            return null;
        }
        Object reasoning = output.getMetadata().get("reasoning_content");
        if (reasoning instanceof String reasoningStr && StringUtils.hasText(reasoningStr)) {
            return reasoningStr;
        }
        return null;
    }
}

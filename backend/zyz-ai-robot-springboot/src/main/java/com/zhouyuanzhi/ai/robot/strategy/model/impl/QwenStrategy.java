package com.zhouyuanzhi.ai.robot.strategy.model.impl;

import com.zhouyuanzhi.ai.robot.model.vo.chat.AIResponse;
import com.zhouyuanzhi.ai.robot.strategy.model.AIModelStrategy;
import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import com.zhouyuanzhi.ai.robot.strategy.extractor.impl.NoOpReasoningExtractor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class QwenStrategy implements AIModelStrategy {

    private final ChatModel openAiChatModel;
    private final NoOpReasoningExtractor noOpReasoningExtractor;

    // 假设 Qwen 是通过 OpenAI 协议兼容接入的
    public QwenStrategy(Map<String, ChatModel> chatModels, NoOpReasoningExtractor noOpReasoningExtractor) {
        this.openAiChatModel = chatModels.get("openAiChatModel");
        this.noOpReasoningExtractor = noOpReasoningExtractor;
    }

    @Override
    public ReasoningExtractor getReasoningExtractor() {
        // 普通模型不需要提取思考过程，返回空实现
        return this.noOpReasoningExtractor;
    }

    @Override
    public boolean supports(String modelName) {
        return modelName != null && modelName.startsWith("qwen");
    }

    @Override
    public ChatClient.ChatClientRequestSpec createRequest(String modelName, String userMessage, Double temperature) {
        return ChatClient.create(openAiChatModel).prompt()
                .user(userMessage)
                .options(OpenAiChatOptions.builder()
                        .model(modelName)
                        .temperature(temperature)
                        .build());
    }

    /**
     * 修正：参数改为 ChatResponse
     */
    @Override
    public AIResponse convertResponse(ChatResponse response) {
        if (response == null || response.getResult() == null) {
            return null;
        }

        String text = response.getResult().getOutput().getText();

        if (StringUtils.hasText(text)) {
            return AIResponse.builder().v(text).type("content").build();
        }
        return null;
    }
}
package com.zhouyuanzhi.ai.robot.strategy.model.impl;

import com.zhouyuanzhi.ai.robot.config.ArkClientProperties;
import com.zhouyuanzhi.ai.robot.model.vo.chat.AIResponse;
import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import com.zhouyuanzhi.ai.robot.strategy.extractor.impl.ArkReasoningExtractor;
import com.zhouyuanzhi.ai.robot.strategy.model.AIModelStrategy;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class ArkDeepSeekStrategy implements AIModelStrategy {

    private final ChatModel arkChatModel;
    private final ArkClientProperties arkClientProperties;
    private final ArkReasoningExtractor arkReasoningExtractor;

    public ArkDeepSeekStrategy(Map<String, ChatModel> chatModels,
                               ArkClientProperties arkClientProperties,
                               ArkReasoningExtractor arkReasoningExtractor) {
        this.arkChatModel = chatModels.get("arkChatModel");
        this.arkClientProperties = arkClientProperties;
        this.arkReasoningExtractor = arkReasoningExtractor;
    }

    @Override
    public boolean supports(String modelName) {
        return StringUtils.hasText(modelName) &&
                (arkClientProperties.getModels().containsKey(modelName)
                        || arkClientProperties.getModels().values().stream()
                        .anyMatch(cfg -> modelName.equals(cfg.getModelId())));
    }

    @Override
    public ChatClient.ChatClientRequestSpec createRequest(String modelName, String userMessage, Double temperature) {
        String resolvedModelName = resolveModelName(modelName);
        ChatOptions options = ChatOptions.builder()
                .model(resolvedModelName)
                .temperature(temperature)
                .build();

        return ChatClient.create(arkChatModel).prompt()
                .user(userMessage)
                .options(options);
    }

    @Override
    public ReasoningExtractor getReasoningExtractor() {
        return arkReasoningExtractor;
    }

    @Override
    public AIResponse convertResponse(ChatResponse response) {
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            return null;
        }
        var message = response.getResult().getOutput();
        String reasoning = null;
        if (message.getMetadata() != null) {
            Object val = message.getMetadata().get("reasoning_content");
            if (val instanceof String rs && StringUtils.hasText(rs)) {
                reasoning = rs;
            }
        }
        if (StringUtils.hasText(reasoning)) {
            return AIResponse.builder().v(reasoning).type("reasoning").build();
        }
        String text = message.getText();
        if (StringUtils.hasText(text)) {
            return AIResponse.builder().v(text).type("content").build();
        }
        return null;
    }

    private String resolveModelName(String requestedName) {
        if (arkClientProperties.getModels().containsKey(requestedName)) {
            return requestedName;
        }
        return arkClientProperties.getModels().entrySet().stream()
                .filter(entry -> requestedName.equals(entry.getValue().getModelId()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(requestedName);
    }
}

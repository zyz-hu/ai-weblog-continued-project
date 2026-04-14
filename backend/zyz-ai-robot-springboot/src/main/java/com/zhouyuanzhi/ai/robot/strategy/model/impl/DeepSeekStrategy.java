package com.zhouyuanzhi.ai.robot.strategy.model.impl;

import com.zhouyuanzhi.ai.robot.model.vo.chat.AIResponse;
import com.zhouyuanzhi.ai.robot.strategy.model.AIModelStrategy;
import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import com.zhouyuanzhi.ai.robot.strategy.extractor.impl.DeepSeekReasoningExtractor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class DeepSeekStrategy implements AIModelStrategy {

    private final ChatModel deepSeekChatModel;

    private final ReasoningExtractor deepSeekReasoningExtractor; // 注入

    public DeepSeekStrategy(Map<String, ChatModel> chatModels,
                            DeepSeekReasoningExtractor deepSeekReasoningExtractor) {
        this.deepSeekChatModel = chatModels.get("deepSeekChatModel");
        this.deepSeekReasoningExtractor = deepSeekReasoningExtractor;
    }

    @Override
    public boolean supports(String modelName) {
        return modelName != null && modelName.startsWith("deepseek");
    }

    @Override
    public ReasoningExtractor getReasoningExtractor() {
        // DeepSeek 使用专用的提取器
        return this.deepSeekReasoningExtractor;
    }

    @Override
    public ChatClient.ChatClientRequestSpec createRequest(String modelName, String userMessage, Double temperature) {
        // 针对 reasoner 模型进行特殊处理
        // 注意：深度思考模型(reasoner)通常不建议设置 temperature，或者必须设为 0，否则可能报错或效果变差
        if (modelName.contains("reasoner")) {
            // 如果是推理模型，可能需要忽略前端传来的 temperature，或者强制设为特定值
            return ChatClient.create(deepSeekChatModel).prompt()
                    .user(userMessage)
                    .options(DeepSeekChatOptions.builder()
                            .model(modelName)
                            // .temperature(0.0) // 推理模型通常建议由模型自动控制或设为0
                            .build());
        }

        // 普通 DeepSeek Chat 模型
        return ChatClient.create(deepSeekChatModel).prompt()
                .user(userMessage)
                .options(DeepSeekChatOptions.builder()
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

        AssistantMessage output = response.getResult().getOutput();

        if (output instanceof DeepSeekAssistantMessage) {
            DeepSeekAssistantMessage deepSeekMessage = (DeepSeekAssistantMessage) output;

            // 1. 尝试获取思考内容
            String reasoning = deepSeekMessage.getReasoningContent();
            if (StringUtils.hasText(reasoning)) {
                return AIResponse.builder().v(reasoning).type("reasoning").build();
            }

            // 2. 获取正文
            String text = deepSeekMessage.getText();
            if (StringUtils.hasText(text)) {
                return AIResponse.builder().v(text).type("content").build();
            }
        } else {
            // 兜底
            String text = output.getText();
            if (StringUtils.hasText(text)) {
                return AIResponse.builder().v(text).type("content").build();
            }
        }
        return null;
    }
}
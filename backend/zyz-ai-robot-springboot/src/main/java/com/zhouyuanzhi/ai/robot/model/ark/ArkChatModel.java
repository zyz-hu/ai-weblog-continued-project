package com.zhouyuanzhi.ai.robot.model.ark;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChunk;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChoice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import com.zhouyuanzhi.ai.robot.config.ArkClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.StreamingModel;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Ark 官方 SDK 适配为 Spring AI ChatModel，便于沿用 ChatClient 与 Advisor 体系。
 */
@RequiredArgsConstructor
public class ArkChatModel implements ChatModel, StreamingModel<Prompt, ChatResponse> {

    private final ArkService arkService;
    private final ArkClientProperties properties;

    @Override
    public ChatResponse call(Prompt prompt) {
        ChatCompletionRequest request = buildRequest(prompt, false);
        Map<String, String> headers = buildHeaders(prompt);
        ChatCompletionResult result = CollectionUtils.isEmpty(headers)
                ? arkService.createChatCompletion(request)
                : arkService.createChatCompletion(request, headers);
        return toChatResponseFromChoice(extractFirstChoice(result.getChoices()));
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        ChatCompletionRequest request = buildRequest(prompt, true);
        Map<String, String> headers = buildHeaders(prompt);
        Flux<ChatCompletionChunk> chunkFlux = CollectionUtils.isEmpty(headers)
                ? Flux.from(arkService.streamChatCompletion(request))
                : Flux.from(arkService.streamChatCompletion(request, headers));

        return chunkFlux
                .map(chunk -> extractFirstChoice(chunk.getChoices()))
                .filter(Objects::nonNull)
                .map(this::toChatResponseFromChoice);
    }

    private ChatCompletionRequest buildRequest(Prompt prompt, boolean stream) {
        String modelName = prompt.getOptions() != null ? prompt.getOptions().getModel() : null;
        if (!StringUtils.hasText(modelName)) {
            throw new IllegalArgumentException("Ark modelName is required in ChatOptions");
        }
        ArkClientProperties.ModelConfig modelConfig = properties.getModels().get(modelName);
        if (modelConfig == null || !StringUtils.hasText(modelConfig.getModelId())) {
            throw new IllegalArgumentException("Ark model config not found for: " + modelName);
        }

        List<ChatMessage> messages = new ArrayList<>();
        for (Message message : prompt.getInstructions()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRole(mapRole(message.getMessageType()));
            chatMessage.setContent(message.getText());
            messages.add(chatMessage);
        }

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(modelConfig.getModelId());
        request.setMessages(messages);
        if (prompt.getOptions() != null && prompt.getOptions().getTemperature() != null) {
            request.setTemperature(prompt.getOptions().getTemperature());
        }
        request.setStream(stream);
        return request;
    }

    private Map<String, String> buildHeaders(Prompt prompt) {
        String modelName = prompt.getOptions() != null ? prompt.getOptions().getModel() : null;
        ArkClientProperties.ModelConfig modelConfig = properties.getModels().get(modelName);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Client-Request-Id", UUID.randomUUID().toString());
        if (modelConfig != null && modelConfig.getExtraHeaders() != null) {
            headers.putAll(modelConfig.getExtraHeaders());
        }
        return headers;
    }

    private ChatMessageRole mapRole(MessageType type) {
        return switch (type) {
            case SYSTEM -> ChatMessageRole.SYSTEM;
            case ASSISTANT -> ChatMessageRole.ASSISTANT;
            case USER -> ChatMessageRole.USER;
            default -> ChatMessageRole.USER;
        };
    }

    private ChatCompletionChoice extractFirstChoice(List<ChatCompletionChoice> choices) {
        if (choices == null || choices.isEmpty()) {
            return null;
        }
        return choices.getFirst();
    }

    private ChatResponse toChatResponseFromChoice(ChatCompletionChoice choice) {
        if (choice == null || choice.getMessage() == null) {
            return null;
        }
        ChatMessage message = choice.getMessage();
        String content = message.stringContent();
        String reasoning = message.getReasoningContent();

        Map<String, Object> metadata = new HashMap<>();
        if (StringUtils.hasText(reasoning)) {
            metadata.put("reasoning_content", reasoning);
        }

        AssistantMessage assistantMessage = new AssistantMessage(StringUtils.hasText(content) ? content : "", metadata);
        Generation generation = new Generation(assistantMessage);
        return new ChatResponse(List.of(generation));
    }

    // ChatModel 接口的便捷方法实现
    @Override
    public String call(String prompt) {
        ChatResponse response = call(new Prompt(prompt));
        return response != null && response.getResult() != null ? response.getResult().getOutput().getText() : null;
    }

    @Override
    public String call(Message... messages) {
        ChatResponse response = call(new Prompt(List.of(messages)));
        return response != null && response.getResult() != null ? response.getResult().getOutput().getText() : null;
    }

    @Override
    public Flux<String> stream(String prompt) {
        return stream(new Prompt(prompt)).map(res -> res.getResult().getOutput().getText());
    }

    @Override
    public Flux<String> stream(Message... messages) {
        return stream(new Prompt(List.of(messages))).map(res -> res.getResult().getOutput().getText());
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return null;
    }
}



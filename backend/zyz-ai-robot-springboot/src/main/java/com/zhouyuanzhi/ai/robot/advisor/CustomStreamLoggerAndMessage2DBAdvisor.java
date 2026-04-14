package com.zhouyuanzhi.ai.robot.advisor;

import com.zhouyuanzhi.ai.robot.domain.dos.ChatMessageDO;
import com.zhouyuanzhi.ai.robot.domain.mapper.ChatMessageMapper;
import com.zhouyuanzhi.ai.robot.model.vo.chat.AiChatReqVO;
import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: 周元智
 * @Description: 自定义打印流式日志 & 消息持久化 Advisor (修复版)
 **/
@Slf4j
public class CustomStreamLoggerAndMessage2DBAdvisor implements StreamAdvisor {

    private final ChatMessageMapper chatMessageMapper;
    private final AiChatReqVO aiChatReqVO;
    private final TransactionTemplate transactionTemplate;
    private final Map<String, Object> sharedContext;
    private final ReasoningExtractor reasoningExtractor;

    private List<Map<String, Object>> getSearchSourcesFromContext() {
        if (sharedContext != null && sharedContext.containsKey("search_sources")) {
            try {
                return (List<Map<String, Object>>) sharedContext.get("search_sources");
            } catch (Exception e) {
                log.warn("获取搜索来源转换失败", e);
            }
        }
        return Collections.emptyList();
    }

    public CustomStreamLoggerAndMessage2DBAdvisor(ChatMessageMapper chatMessageMapper,
                                                  AiChatReqVO aiChatReqVO,
                                                  TransactionTemplate transactionTemplate, Map<String, Object> sharedContext, ReasoningExtractor reasoningExtractor) {
        this.chatMessageMapper = chatMessageMapper;
        this.aiChatReqVO = aiChatReqVO;
        this.transactionTemplate = transactionTemplate;
        this.sharedContext = sharedContext;
        this.reasoningExtractor = reasoningExtractor;
    }

    @Override
    public int getOrder() {
        return 99;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        String chatUuid = aiChatReqVO.getChatId();
        String userMessage = aiChatReqVO.getMessage();

        // 1. 记录开始时间 (修复 timeCost 报错)
        long startTime = System.currentTimeMillis();

        Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);

        // 容器
        AtomicReference<StringBuilder> fullContent = new AtomicReference<>(new StringBuilder());
        AtomicReference<StringBuilder> fullReasoning = new AtomicReference<>(new StringBuilder());

        return chatClientResponseFlux
                .doOnNext(response -> {
                    // --- 提取正文 ---
                    // 增加空指针保护，防止 getResult() 为空
                    if (response.chatResponse() != null && response.chatResponse().getResult() != null) {
                        String contentChunk = response.chatResponse().getResult().getOutput().getText();
                        if (StringUtils.hasText(contentChunk)) {
                            fullContent.get().append(contentChunk);
                        }

                        // 2. 提取思考过程 (多态调用)
                        // Advisor 不知道这是 DeepSeek 还是 OpenAI，它只管调用 extract
                        String reasoningChunk = reasoningExtractor.extract(response);

                        if (StringUtils.hasText(reasoningChunk)) {
                            fullReasoning.get().append(reasoningChunk);
                            log.debug("Thinking: {}", reasoningChunk);
                        }
                    }
                })
                .doOnComplete(() -> {
                    // 2. 计算耗时
                    long timeCost = System.currentTimeMillis() - startTime;

                    String finalContent = fullContent.get().toString();
                    String finalReasoning = fullReasoning.get().toString();

                    log.info("==== 思考过程 ({} chars) ====", finalReasoning.length());
                    log.info("==== 最终回复 ({} chars) ====", finalContent.length());

                    // 开启编程式事务存储
                    transactionTemplate.execute(status -> {
                        try {
                            // 存储用户消息
                            chatMessageMapper.insert(ChatMessageDO.builder()
                                    .chatUuid(chatUuid)
                                    .content(userMessage)
                                    .role(MessageType.USER.getValue())
                                    .createTime(LocalDateTime.now())
                                    .build());

                            // 获取搜索源 (如果有)
                            List<Map<String, Object>> searchSources = getSearchSourcesFromContext();

                            // 存储 AI 回答 (修复：只保留这一个 Insert，删除了原来重复的那个)
                            chatMessageMapper.insert(ChatMessageDO.builder()
                                    .chatUuid(chatUuid)
                                    .role(MessageType.ASSISTANT.getValue())
                                    .content(finalContent)
                                    .reasoningContent(finalReasoning) // 存入思考
                                    .modelName(aiChatReqVO.getModelName()) // 存入模型名
                                    // 存入元数据
                                    .metaData(Map.of(
                                            "latency", timeCost, // 现在 timeCost 有定义了
                                            "search_sources", searchSources
                                    ))
                                    .createTime(LocalDateTime.now())
                                    .build());

                            return true;
                        } catch (Exception ex) {
                            status.setRollbackOnly();
                            log.error("保存消息失败", ex);
                        }
                        return false;
                    });
                })
                .doOnError(error -> {
                    log.error("流式响应异常", error);
                });
    }
}
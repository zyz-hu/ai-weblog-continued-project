package com.zhouyuanzhi.ai.robot.controller;

import com.google.common.collect.Lists;
import com.zhouyuanzhi.ai.robot.advisor.CustomChatMemoryAdvisor;
import com.zhouyuanzhi.ai.robot.advisor.CustomStreamLoggerAndMessage2DBAdvisor;
import com.zhouyuanzhi.ai.robot.advisor.NetworkSearchAdvisor;
import com.zhouyuanzhi.ai.robot.aspect.ApiOperationLog;
import com.zhouyuanzhi.ai.robot.domain.mapper.ChatMapper;
import com.zhouyuanzhi.ai.robot.domain.mapper.ChatMessageMapper;
import com.zhouyuanzhi.ai.robot.enums.ResponseCodeEnum;
import com.zhouyuanzhi.ai.robot.exception.BizException;
import com.zhouyuanzhi.ai.robot.factory.AIModelFactory;
import com.zhouyuanzhi.ai.robot.model.vo.chat.*;
import com.zhouyuanzhi.ai.robot.service.ChatService;
import com.zhouyuanzhi.ai.robot.service.SearXNGService;
import com.zhouyuanzhi.ai.robot.service.SearchResultContentFetcherService;
import com.zhouyuanzhi.ai.robot.strategy.extractor.ReasoningExtractor;
import com.zhouyuanzhi.ai.robot.strategy.model.AIModelStrategy;
import com.zhouyuanzhi.ai.robot.utils.PageResponse;
import com.zhouyuanzhi.ai.robot.utils.Response;
import com.zhouyuanzhi.ai.robot.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.http.MediaType;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: 周元智
 * @Date: 2025/5/22 12:25
 * @Version: v1.0.0
 * @Description: 对话接口
 **/
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    private static final String DEFAULT_POLISH_PROMPT = "You are an expert editor. Polish the following article to improve clarity and fluency while keeping the original meaning. Return only the polished article.";

    private final ChatService chatService;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatMapper chatMapper;
    private final TransactionTemplate transactionTemplate;
    private final SearXNGService searXNGService;
    private final SearchResultContentFetcherService searchResultContentFetcherService;
    private final AIModelFactory aiModelFactory;

    public ChatController(ChatService chatService,
                          ChatMessageMapper chatMessageMapper,
                          ChatMapper chatMapper,
                          TransactionTemplate transactionTemplate,
                          SearXNGService searXNGService,
                          SearchResultContentFetcherService searchResultContentFetcherService,
                          AIModelFactory aiModelFactory) {
        this.chatService = chatService;
        this.chatMessageMapper = chatMessageMapper;
        this.chatMapper = chatMapper;
        this.transactionTemplate = transactionTemplate;
        this.searXNGService = searXNGService;
        this.searchResultContentFetcherService = searchResultContentFetcherService;
        this.aiModelFactory = aiModelFactory;
    }

    @PostMapping("/new")
    @ApiOperationLog(description = "新建对话")
    public Response<?> newChat(@RequestBody @Validated NewChatReqVO newChatReqVO) {
        return chatService.newChat(newChatReqVO);
    }

    /**
     * 流式对话接口
     * 1) 策略模式：解耦不同模型的构造与调用
     * 2) 思考提取：支持推理内容单独透传
     * 3) 共享上下文：在各 Advisor 间传递搜索/日志等信息
     */
    @PostMapping(value = "/completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperationLog(description = "流式对话")
    public Flux<AIResponse> chat(@RequestBody @Validated AiChatReqVO aiChatReqVO) {
        try {
            String modelName = aiChatReqVO.getModelName();
            String userMessage = aiChatReqVO.getMessage();
            Double temperature = aiChatReqVO.getTemperature();
            boolean networkSearch = aiChatReqVO.getNetworkSearch();
            String currentUser = UserContext.requireUser();

            if (StringUtils.hasText(aiChatReqVO.getChatId())) {
                validateChatOwner(aiChatReqVO.getChatId(), currentUser);
            }

            AIModelStrategy strategy = aiModelFactory.getStrategy(modelName);

            //从响应中提取思考片段
            ReasoningExtractor extractor = strategy.getReasoningExtractor();

            Map<String, Object> sharedContext = new ConcurrentHashMap<>();
            List<Advisor> advisors = Lists.newArrayList();

            // 记忆
            advisors.add(new CustomChatMemoryAdvisor(chatMessageMapper, aiChatReqVO, 10));
            // 联网搜索
            if (networkSearch) {
                advisors.add(new NetworkSearchAdvisor(searXNGService, searchResultContentFetcherService, sharedContext));
            }
            // 日志与持久化
            advisors.add(new CustomStreamLoggerAndMessage2DBAdvisor(
                    chatMessageMapper,
                    aiChatReqVO,
                    transactionTemplate,
                    sharedContext,
                    extractor
            ));

            AtomicBoolean isStreamFinished = new AtomicBoolean(false);

            Flux<AIResponse> aiStream = strategy.streamResponse(modelName, userMessage, temperature, advisors)
                    .doOnCancel(() -> log.info("【信号捕获】客户端断开，取消 AI 任务"))
                    .doOnTerminate(() -> isStreamFinished.set(true))
                    .onErrorResume(e -> {
                        if (e.getMessage().contains("Broken pipe") || e.getMessage().contains("Connection reset")) {
                            return Flux.empty();
                        }
                        log.error("AI Stream Error: ", e);
                        return Flux.just(AIResponse.builder().v("服务异常: " + e.getMessage()).type("content").build());
                    });

            Flux<AIResponse> heartbeatStream = Flux.interval(Duration.ofSeconds(1))
                    .takeWhile(i -> !isStreamFinished.get())
                    .map(i -> AIResponse.builder()
                            .type("ping")
                            .v("")
                            .build());

            return Flux.merge(aiStream, heartbeatStream);

        } catch (IllegalArgumentException e) {
            log.warn("Model selection error: {}", e.getMessage());
            return Flux.just(AIResponse.builder().v("[ERROR] " + e.getMessage()).type("content").build());
        } catch (Exception e) {
            log.error("System Error during chat init: ", e);
            return Flux.just(AIResponse.builder().v("[ERROR] 系统内部初始化错误").type("content").build());
        }
    }

    @PostMapping(value = "/polish", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperationLog(description = "文章润色（流式）")
    public Flux<AIResponse> polishArticle(@RequestBody @Validated PolishArticleReqVO polishArticleReqVO) {
        try {
            String modelName = polishArticleReqVO.getModelName();
            String article = polishArticleReqVO.getArticle();
            Double temperature = polishArticleReqVO.getTemperature();
            String prompt = polishArticleReqVO.getPrompt();

            String userMessage = buildPolishPrompt(prompt, article);

            AIModelStrategy strategy = aiModelFactory.getStrategy(modelName);

            AtomicBoolean isStreamFinished = new AtomicBoolean(false);

            Flux<AIResponse> aiStream = strategy.streamResponse(modelName, userMessage, temperature, java.util.List.of())
                    .doOnCancel(() -> log.info("【信号捕获】客户端断开，取消润色任务"))
                    .doOnTerminate(() -> isStreamFinished.set(true))
                    .onErrorResume(e -> {
                        if (e.getMessage().contains("Broken pipe") || e.getMessage().contains("Connection reset")) {
                            return Flux.empty();
                        }
                        log.error("AI Polish Stream Error: ", e);
                        return Flux.just(AIResponse.builder().v("服务异常: " + e.getMessage()).type("content").build());
                    });

            Flux<AIResponse> heartbeatStream = Flux.interval(Duration.ofSeconds(1))
                    .takeWhile(i -> !isStreamFinished.get())
                    .map(i -> AIResponse.builder()
                            .type("ping")
                            .v("")
                            .build());

            return Flux.merge(aiStream, heartbeatStream);
        } catch (IllegalArgumentException e) {
            log.warn("Model selection error: {}", e.getMessage());
            return Flux.just(AIResponse.builder().v("[ERROR] " + e.getMessage()).type("content").build());
        } catch (Exception e) {
            log.error("System Error during polish init: ", e);
            return Flux.just(AIResponse.builder().v("[ERROR] 系统内部初始化错误").type("content").build());
        }
    }

    @PostMapping("/list")
    @ApiOperationLog(description = "查询历史对话")
    public PageResponse<FindChatHistoryPageListRspVO> findChatHistoryPageList(@RequestBody @Validated FindChatHistoryPageListReqVO findChatHistoryPageListReqVO) {
        return chatService.findChatHistoryPageList(findChatHistoryPageListReqVO);
    }

    @PostMapping("/message/list")
    @ApiOperationLog(description = "查询对话历史消息")
    public PageResponse<FindChatHistoryMessagePageListRspVO> findChatMessagePageList(@RequestBody @Validated FindChatHistoryMessagePageListReqVO findChatHistoryMessagePageListReqVO) {
        return chatService.findChatHistoryMessagePageList(findChatHistoryMessagePageListReqVO);
    }

    @PostMapping("/summary/rename")
    @ApiOperationLog(description = "重命名对话摘要")
    public Response<?> renameChatSummary(@RequestBody @Validated RenameChatReqVO renameChatReqVO) {
        return chatService.renameChatSummary(renameChatReqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "删除对话")
    public Response<?> deleteChat(@RequestBody @Validated DeleteChatReqVO deleteChatReqVO) {
        return chatService.deleteChat(deleteChatReqVO);
    }

    private void validateChatOwner(String chatUuid, String userId) {
        boolean exists = chatMapper.exists(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<com.zhouyuanzhi.ai.robot.domain.dos.ChatDO>lambdaQuery()
                        .eq(com.zhouyuanzhi.ai.robot.domain.dos.ChatDO::getUuid, chatUuid)
                        .eq(com.zhouyuanzhi.ai.robot.domain.dos.ChatDO::getUserId, userId));
        if (!exists) {
            throw new BizException(ResponseCodeEnum.CHAT_NOT_EXISTED);
        }
    }

    private String buildPolishPrompt(String customPrompt, String article) {
        String instruction = StringUtils.hasText(customPrompt) ? customPrompt : DEFAULT_POLISH_PROMPT;
        return instruction + "\n\n---\n" + article;
    }

}

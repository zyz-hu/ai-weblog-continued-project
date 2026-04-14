package com.zhouyuanzhi.ai.robot.advisor;

import com.zhouyuanzhi.ai.robot.model.dto.SearchResultDTO;
import com.zhouyuanzhi.ai.robot.service.SearXNGService;
import com.zhouyuanzhi.ai.robot.service.SearchResultContentFetcherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NetworkSearchAdvisor implements StreamAdvisor {

    private final SearXNGService searXNGService;
    private final SearchResultContentFetcherService searchResultContentFetcherService;
    private final Map<String, Object> sharedContext;

    // 优化后的提示词模板
    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("""
            ### 角色设定
            你是一个专业的智能助手。请严格基于下方的【搜索结果上下文】回答【用户问题】。
            
            ### 搜索结果上下文
            ---------------------
            {context}
            ---------------------
            
            ### 回答格式严格要求（重要）
            1. **Markdown 语法规范**：
               - **标题格式**：标题的井号 `###` 与文字之间必须有一个空格（例如：`### 1. 网速测试`，严禁写成 `###1.网速测试`）。
               - **列表格式**：使用 `- ` 或 `1. ` 开头，并在符号后保留空格。
               - **重点加粗**：对关键名词或结论使用 **加粗**。
            
            2. **引用与链接规范**：
               - **生成链接**：如果上下文中提供了具体的 URL 链接，**请务必**将对应的网站名称或工具名称封装为 Markdown 链接。
                 - 错误示例：Speedtest.net [来源1]
                 - 正确示例：[Speedtest.net](https://www.speedtest.net) [来源1]
               - **标注来源**：每一处引用的末尾必须严格使用 `[来源x]` 的格式标注。
            
            3. **排版要求**：
               - 回答应分点陈述，逻辑清晰。
               - 段落之间保留空行，确保阅读舒适。
            
            ### 用户问题
            {question}
            """);

    public NetworkSearchAdvisor(SearXNGService searXNGService,
                                SearchResultContentFetcherService searchResultContentFetcherService,
                                Map<String, Object> sharedContext) {
        this.searXNGService = searXNGService;
        this.searchResultContentFetcherService = searchResultContentFetcherService;
        this.sharedContext = sharedContext;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        // 使用 Flux.defer 确保只有在订阅时才开始执行逻辑，且支持响应式流的取消信号
        return Flux.defer(() -> {
            Prompt prompt = chatClientRequest.prompt();
            UserMessage userMessage = prompt.getUserMessage();
            String query = userMessage.getText();

            log.info("## 开始联网搜索 (Reactive): {}", query);

            // 将阻塞的搜索和爬取逻辑封装在 Mono.fromCallable 中，并在 boundedElastic 线程池执行
            return Mono.fromCallable(() -> {
                        // 1. 调用 SearXNG
                        List<SearchResultDTO> searchResults = searXNGService.search(query);
                        log.info("## SearXNG 初步返回 {} 条结果", searchResults.size());

                        // 2. 并发爬取内容
                        List<SearchResultDTO> finalResults;
                        if (searchResults.isEmpty()) {
                            finalResults = new ArrayList<>();
                        } else {
                            // 设置 5秒 超时
                            CompletableFuture<List<SearchResultDTO>> resultsFuture = searchResultContentFetcherService.batchFetch(searchResults, 5, TimeUnit.SECONDS);
                            try {
                                finalResults = resultsFuture.join();
                            } catch (Exception e) {
                                log.error("## 爬取网页内容失败或超时", e);
                                finalResults = searchResults;
                            }
                        }
                        return finalResults;
                    })
                    .subscribeOn(Schedulers.boundedElastic()) // 关键：在弹性线程池执行阻塞IO，不阻塞主线程
                    .flatMapMany(finalResults -> {
                        // *** 如果流被取消（前端断开），这里将不会执行，从而阻断了后续的 LLM 调用 ***

                        // 3. 构建上下文
                        List<SearchResultDTO> validResults = new ArrayList<>();
                        List<Map<String, Object>> sourceMetaList = new ArrayList<>();

                        for (SearchResultDTO result : finalResults) {
                            String shouldUseContent = result.getContent();
                            if (StringUtils.isBlank(shouldUseContent)) {
                                shouldUseContent = result.getSnippet();
                            }

                            if (StringUtils.isNotBlank(shouldUseContent)) {
                                result.setContent(shouldUseContent);
                                validResults.add(result);

                                Map<String, Object> meta = new HashMap<>();
                                meta.put("title", result.getTitle());
                                meta.put("url", result.getUrl());
                                meta.put("score", result.getScore());
                                sourceMetaList.add(meta);
                            }
                        }

                        log.info("## 最终有效上下文条数: {}", validResults.size());
                        sharedContext.put("search_sources", sourceMetaList);

                        // 4. 如果没有搜索结果，直接放行
                        if (validResults.isEmpty()) {
                            log.warn("## 警告：本次搜索未获得任何有效内容，将退回纯 LLM 模式");
                            return streamAdvisorChain.nextStream(chatClientRequest);
                        }

                        // 5. 构建增强 Prompt
                        String searchContext = buildContext(validResults);
                        Prompt newPrompt = DEFAULT_PROMPT_TEMPLATE.create(Map.of(
                                "question", query,
                                "context", searchContext
                        ), chatClientRequest.prompt().getOptions());

                        // 6. 使用 mutate 保留原请求配置
                        ChatClientRequest newRequest = chatClientRequest.mutate()
                                .prompt(newPrompt)
                                .build();

                        // 继续执行链条（调用 LLM）
                        return streamAdvisorChain.nextStream(newRequest);
                    });
        });
    }

    private String buildContext(List<SearchResultDTO> successfulResults) {
        int i = 1;
        StringBuilder sb = new StringBuilder();
        for (SearchResultDTO result : successfulResults) {
            sb.append(String.format("""
                        ### 来源 [%d]
                        - 标题: %s
                        - 链接: %s 
                        - 内容:
                        %s
                        \n
                        """, i, result.getTitle(), result.getUrl(), result.getContent()));
            i++;
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
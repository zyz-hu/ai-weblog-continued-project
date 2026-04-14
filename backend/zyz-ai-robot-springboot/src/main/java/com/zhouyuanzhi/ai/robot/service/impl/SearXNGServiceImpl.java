package com.zhouyuanzhi.ai.robot.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyuanzhi.ai.robot.model.dto.SearchResultDTO;
import com.zhouyuanzhi.ai.robot.service.SearXNGService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @Author: 周元智
 * @Description: SearXNG 搜索服务实现类 (修复版：增加标题和摘要提取)
 **/
@Service
@Slf4j
public class SearXNGServiceImpl implements SearXNGService {

    @Resource
    private OkHttpClient okHttpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Value("${searxng.url}")
    private String searxngUrl;
    @Value("${searxng.count}")
    private int count;

    @Override
    public List<SearchResultDTO> search(String query) {
        // 构建 SearXNG API 请求 URL
        HttpUrl httpUrl = HttpUrl.parse(searxngUrl).newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("format", "json")
                // 记得删掉 engines 参数，或者只保留 google,bing,wikipedia
                .build();

        // 🔥 核心修改：添加 Header 伪装 IP
        Request request = new Request.Builder()
                .url(httpUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                // 👇 新增这两行，骗过 SearXNG 的 IP 检测
                .header("X-Forwarded-For", "127.0.0.1")
                .header("X-Real-IP", "127.0.0.1")
                .get()
                .build();

        // 发送 HTTP 请求
        try (Response response = okHttpClient.newCall(request).execute()) {
            // 判断请求是否成功
            if (response.isSuccessful() && response.body() != null) {
                // 拿到返回结果
                String result = response.body().string();
                log.info("## SearXNG 搜索结果: {}", result);

                // 解析 JSON 响应
                JsonNode root = objectMapper.readTree(result);
                JsonNode results = root.get("results"); // 获取结果数组节点

                if (results == null || !results.isArray()) {
                    return Collections.emptyList();
                }

                // 定义 Record 类型：用于临时存储分数和节点引用
                record NodeWithUrlAndScore(double score, JsonNode node) {}

                // 处理搜索结果流
                List<NodeWithUrlAndScore> nodesWithScore = StreamSupport.stream(results.spliterator(), false)
                        .map(node -> {
                            // 只提取分数，避免构建完整对象
                            double score = node.path("score").asDouble(0.0); // 提取评分
                            return new NodeWithUrlAndScore(score, node);
                        })
                        .sorted(Comparator.comparingDouble(NodeWithUrlAndScore::score).reversed()) // 按评分降序
                        .limit(count) // 限制返回结果数量
                        .toList();

                // 转换为 SearchResult 对象集合
                return nodesWithScore.stream()
                        .map(n -> {
                            JsonNode node = n.node();
                            String originalUrl = node.path("url").asText(""); // 提取 URL

                            // 🌟 核心修改：提取标题和摘要 🌟
                            String title = node.path("title").asText("无标题");
                            // SearXNG 返回的 JSON 里，"content" 字段通常就是摘要 snippet
                            String snippet = node.path("content").asText("");

                            return SearchResultDTO.builder()
                                    .url(originalUrl)
                                    .title(title)     // ✅ 设置标题
                                    .snippet(snippet) // ✅ 设置摘要 (这就是保底内容)
                                    .score(n.score()) // 保留评分
                                    .build();
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("SearXNG search error", e);
        }
        // 返回空集合
        return Collections.emptyList() ;
    }
}
package com.zhouyuanzhi.ai.robot.domain.dos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.zhouyuanzhi.ai.robot.handler.PgJsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_chat_message", autoResultMap = true) // 必须开启 autoResultMap
public class ChatMessageDO {

    private Long id;

    private String chatUuid;

    /**
     * user / assistant / system
     */
    private String role;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 思考过程 (DeepSeek R1 专用)
     */
    private String reasoningContent;

    /**
     * 模型名称 (例如: deepseek-reasoner)
     * 记录这个是为了以后回看时，知道是哪个模型说的废话
     */
    private String modelName;

    /**
     * Token 使用情况
     * 存储结构: {"promptTokens": 10, "completionTokens": 20, "totalTokens": 30}
     */
    @TableField(typeHandler = PgJsonTypeHandler.class)
    private Map<String, Integer> tokenUsage;

    /**
     * 扩展元数据 (RAG 来源、耗时等)
     * 存储结构:
     * {
     * "latency": 1500,
     * "search_sources": [
     * {"title": "百度百科", "url": "..."}
     * ]
     * }
     */
    @TableField(typeHandler = PgJsonTypeHandler.class)
    private Map<String, Object> metaData;

    private LocalDateTime createTime;
}
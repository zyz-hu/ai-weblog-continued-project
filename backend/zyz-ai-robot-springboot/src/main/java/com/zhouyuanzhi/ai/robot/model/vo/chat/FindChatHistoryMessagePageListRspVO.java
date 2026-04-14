package com.zhouyuanzhi.ai.robot.model.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * @author: 周元智
 * @url: www.zhouyuanzhi.com
 * @date: 2023-09-15 14:07
 * @description: 查询对话历史消息
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindChatHistoryMessagePageListRspVO {
    /**
     * 消息 ID
     */
    private Long id;
    /**
     * 对话 ID
     */
    private String chatId;
    /**
     * 内容
     */
    private String content;
    /**
     * 思考内容（深度思考模型）
     */
    private String reasoningContent;
    /**
     * 消息类型
     */
    private String role;
    /**
     * 发布时间
     */
    private LocalDateTime createTime;

    private String modelName;

}

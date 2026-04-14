package com.zhouyuanzhi.ai.robot.advisor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.zhouyuanzhi.ai.robot.domain.dos.ChatMessageDO;
import com.zhouyuanzhi.ai.robot.domain.mapper.ChatMessageMapper;
import com.zhouyuanzhi.ai.robot.model.vo.chat.AiChatReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @Author: 周元智
 * @Description: 自定义对话记忆 Advisor (修复版：过滤空消息防止 400 Error)
 **/
@Slf4j
public class CustomChatMemoryAdvisor implements StreamAdvisor {

    private final ChatMessageMapper chatMessageMapper;
    private final AiChatReqVO aiChatReqVO;
    private final int limit;

    public CustomChatMemoryAdvisor(ChatMessageMapper chatMessageMapper, AiChatReqVO aiChatReqVO, int limit) {
        this.chatMessageMapper = chatMessageMapper;
        this.aiChatReqVO = aiChatReqVO;
        this.limit = limit;
    }

    @Override
    public int getOrder() {
        return 2; // 必须大于 NetworkSearchAdvisor 的 order (1)
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        log.info("## 自定义聊天记忆 Advisor...");

        String chatUuid = aiChatReqVO.getChatId();

        // 1. 查询数据库
        List<ChatMessageDO> messages = chatMessageMapper.selectList(Wrappers.<ChatMessageDO>lambdaQuery()
                .eq(ChatMessageDO::getChatUuid, chatUuid)
                .orderByDesc(ChatMessageDO::getCreateTime)
                .last(String.format("LIMIT %d", limit)));

        // 2. 按时间正序排列
        List<ChatMessageDO> sortedMessages = messages.stream()
                .sorted(Comparator.comparing(ChatMessageDO::getCreateTime))
                .toList();

        List<Message> messageList = Lists.newArrayList();

        // 3. 转换消息
        for (ChatMessageDO chatMessageDO : sortedMessages) {
            String content = chatMessageDO.getContent();

            //如果内容为空（可能是之前只生成了思考过程但没生成正文），跳过不传，否则 DeepSeek 会报 400
            if (!StringUtils.hasText(content)) {
                continue;
            }

            String type = chatMessageDO.getRole();
            if (Objects.equals(type, MessageType.USER.getValue())) {
                messageList.add(new UserMessage(content));
            } else if (Objects.equals(type, MessageType.ASSISTANT.getValue())) {
                messageList.add(new AssistantMessage(content));
            }
        }

        // 4. 添加当前请求中的消息（此时可能已经被 NetworkSearchAdvisor 修改过）
        messageList.addAll(chatClientRequest.prompt().getInstructions());

        // 5. 构建新请求
        ChatClientRequest processedChatClientRequest = chatClientRequest
                .mutate()
                .prompt(chatClientRequest.prompt().mutate().messages(messageList).build())
                .build();

        return streamAdvisorChain.nextStream(processedChatClientRequest);
    }
}
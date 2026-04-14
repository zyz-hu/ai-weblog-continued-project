package com.zhouyuanzhi.ai.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhouyuanzhi.ai.robot.domain.dos.ChatDO;
import com.zhouyuanzhi.ai.robot.domain.dos.ChatMessageDO;
import com.zhouyuanzhi.ai.robot.domain.mapper.ChatMapper;
import com.zhouyuanzhi.ai.robot.domain.mapper.ChatMessageMapper;
import com.zhouyuanzhi.ai.robot.enums.ResponseCodeEnum;
import com.zhouyuanzhi.ai.robot.exception.BizException;
import com.zhouyuanzhi.ai.robot.model.vo.chat.*;
import com.zhouyuanzhi.ai.robot.service.ChatService;
import com.zhouyuanzhi.ai.robot.utils.PageResponse;
import com.zhouyuanzhi.ai.robot.utils.Response;
import com.zhouyuanzhi.ai.robot.utils.StringUtil;
import com.zhouyuanzhi.ai.robot.utils.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 对话服务实现类（适配新表结构）
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatMapper chatMapper;
    @Resource
    private ChatMessageMapper chatMessageMapper;

    /**
     * 新建对话
     */
    @Override
    public Response<NewChatRspVO> newChat(NewChatReqVO newChatReqVO) {
        String message = newChatReqVO.getMessage();
        String userId = UserContext.requireUser();

        String uuid = UUID.randomUUID().toString();
        String title = StringUtil.truncate(message, 20);

        chatMapper.insert(ChatDO.builder()
                .title(title)
                .uuid(uuid)
                .userId(userId)
                .params(Collections.emptyMap())
                .isDeleted(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build());

        return Response.success(NewChatRspVO.builder()
                .uuid(uuid)
                .summary(title)
                .build());
    }

    /**
     * 查询历史消息
     */
    @Override
    public PageResponse<FindChatHistoryMessagePageListRspVO> findChatHistoryMessagePageList(FindChatHistoryMessagePageListReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();
        String chatId = reqVO.getChatId();
        String userId = UserContext.requireUser();

        // 校验归属
        getOwnedChat(chatId, userId);

        Page<ChatMessageDO> chatMessageDOPage = chatMessageMapper.selectPageList(current, size, chatId);
        List<ChatMessageDO> chatMessageDOS = chatMessageDOPage.getRecords();

        List<FindChatHistoryMessagePageListRspVO> vos = null;
        if (CollUtil.isNotEmpty(chatMessageDOS)) {
            vos = chatMessageDOS.stream()
                    .map(chatMessageDO -> FindChatHistoryMessagePageListRspVO.builder()
                            .id(chatMessageDO.getId())
                            .chatId(chatMessageDO.getChatUuid())
                            .content(chatMessageDO.getContent())
                            .reasoningContent(chatMessageDO.getReasoningContent())
                            .role(chatMessageDO.getRole())
                            .modelName(chatMessageDO.getModelName())
                            .createTime(chatMessageDO.getCreateTime())
                            .build())
                    .sorted(Comparator.comparing(FindChatHistoryMessagePageListRspVO::getCreateTime))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(chatMessageDOPage, vos);
    }

    /**
     * 查询历史对话列表
     */
    @Override
    public PageResponse<FindChatHistoryPageListRspVO> findChatHistoryPageList(FindChatHistoryPageListReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();
        String userId = UserContext.requireUser();

        Page<ChatDO> chatDOPage = chatMapper.selectPageList(current, size, userId);
        List<ChatDO> chatDOS = chatDOPage.getRecords();

        List<FindChatHistoryPageListRspVO> vos = null;
        if (CollUtil.isNotEmpty(chatDOS)) {
            vos = chatDOS.stream()
                    .map(chatDO -> FindChatHistoryPageListRspVO.builder()
                            .id(chatDO.getId())
                            .uuid(chatDO.getUuid())
                            .summary(chatDO.getTitle())
                            .updateTime(chatDO.getUpdateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(chatDOPage, vos);
    }

    /**
     * 重命名对话标题
     */
    @Override
    public Response<?> renameChatSummary(RenameChatReqVO renameChatReqVO) {
        Long chatId = renameChatReqVO.getId();
        String newTitle = renameChatReqVO.getSummary();
        String userId = UserContext.requireUser();

        int updated = chatMapper.update(ChatDO.builder().title(newTitle).build(),
                Wrappers.<ChatDO>lambdaUpdate()
                        .eq(ChatDO::getId, chatId)
                        .eq(ChatDO::getUserId, userId));
        if (updated == 0) {
            throw new BizException(ResponseCodeEnum.CHAT_NOT_EXISTED);
        }
        return Response.success();
    }

    /**
     * 删除对话
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> deleteChat(DeleteChatReqVO deleteChatReqVO) {
        String uuid = deleteChatReqVO.getUuid();
        String userId = UserContext.requireUser();

        int count = chatMapper.delete(Wrappers.<ChatDO>lambdaQuery()
                .eq(ChatDO::getUuid, uuid)
                .eq(ChatDO::getUserId, userId));

        if (count == 0) {
            throw new BizException(ResponseCodeEnum.CHAT_NOT_EXISTED);
        }

        chatMessageMapper.delete(Wrappers.<ChatMessageDO>lambdaQuery()
                .eq(ChatMessageDO::getChatUuid, uuid));

        return Response.success();
    }

    private ChatDO getOwnedChat(String chatUuid, String userId) {
        ChatDO chat = chatMapper.selectOne(Wrappers.<ChatDO>lambdaQuery()
                .eq(ChatDO::getUuid, chatUuid)
                .eq(ChatDO::getUserId, userId));
        if (chat == null) {
            throw new BizException(ResponseCodeEnum.CHAT_NOT_EXISTED);
        }
        return chat;
    }
}

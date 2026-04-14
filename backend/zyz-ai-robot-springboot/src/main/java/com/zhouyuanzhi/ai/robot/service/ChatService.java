package com.zhouyuanzhi.ai.robot.service;


import com.zhouyuanzhi.ai.robot.model.vo.chat.*;
import com.zhouyuanzhi.ai.robot.utils.PageResponse;
import com.zhouyuanzhi.ai.robot.utils.Response;

/**
 * @author: 周元智
 * @url: www.zhouyuanzhi.com
 * @date: 2023-09-15 14:03
 * @description: 对话
 **/
public interface ChatService {

    /**
     * 新建对话
     * @param newChatReqVO
     * @return
     */
    Response<NewChatRspVO> newChat(NewChatReqVO newChatReqVO);

    /**
     * 查询历史消息
     * @param findChatHistoryMessagePageListReqVO
     * @return
     */
    PageResponse<FindChatHistoryMessagePageListRspVO> findChatHistoryMessagePageList(FindChatHistoryMessagePageListReqVO findChatHistoryMessagePageListReqVO);

    /**
     * 查询历史对话
     * @param findChatHistoryPageListReqVO
     * @return
     */
    PageResponse<FindChatHistoryPageListRspVO> findChatHistoryPageList(FindChatHistoryPageListReqVO findChatHistoryPageListReqVO);

    /**
     * 重命名对话摘要
     * @param renameChatReqVO
     * @return
     */
    Response<?> renameChatSummary(RenameChatReqVO renameChatReqVO);

    /**
     * 删除对话
     * @param deleteChatReqVO
     * @return
     */
    Response<?> deleteChat(DeleteChatReqVO deleteChatReqVO);
}

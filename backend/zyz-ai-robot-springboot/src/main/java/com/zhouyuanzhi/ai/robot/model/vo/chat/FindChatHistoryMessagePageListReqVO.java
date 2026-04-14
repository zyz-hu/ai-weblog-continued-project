package com.zhouyuanzhi.ai.robot.model.vo.chat;

import com.zhouyuanzhi.ai.robot.model.common.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
public class FindChatHistoryMessagePageListReqVO extends BasePageQuery {

    @NotBlank(message = "对话 ID 不能为空")
    private String chatId;
}

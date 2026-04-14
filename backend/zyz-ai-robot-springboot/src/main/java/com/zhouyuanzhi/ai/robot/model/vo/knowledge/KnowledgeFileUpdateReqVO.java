package com.zhouyuanzhi.ai.robot.model.vo.knowledge;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 知识库文件更新请求
 */
@Data
public class KnowledgeFileUpdateReqVO {

    /**
     * 文件主键
     */
    @NotNull(message = "文件ID不能为空")
    private Long id;

    /**
     * 备注
     */
    private String remark;
}

package com.zhouyuanzhi.ai.robot.domain.dos;

import com.baomidou.mybatisplus.annotation.*;
import com.zhouyuanzhi.ai.robot.handler.PgJsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: 周元智
 * @Date: 2025/8/11 11:32
 * @Version: v1.0.1
 * @Description: 对话 DO 实体类 (适配 PostgreSQL JSONB 和新表结构)
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_chat", autoResultMap = true)
public class ChatDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 对话UUID
     */
    private String uuid;

    /**
     * 用户ID (对应 SQL: user_id)
     */
    private String userId;

    /**
     * 标题 (对应 SQL: title)
     * 原 summary 字段已重命名
     */
    private String title;

    /**
     * 会话参数配置 (对应 SQL: params jsonb)
     * 例如: {"temperature": 0.7, "model": "deepseek-reasoner"}
     * 必须使用自定义的 PgJsonTypeHandler 防止报错
     */
    @TableField(typeHandler = PgJsonTypeHandler.class)
    private Map<String, Object> params;

    /**
     * 逻辑删除 (对应 SQL: is_deleted)
     * 0-正常, 1-删除
     * 加了 @TableLogic 后，MP 执行 deleteById 时会自动变成 update is_deleted = 1
     */
    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
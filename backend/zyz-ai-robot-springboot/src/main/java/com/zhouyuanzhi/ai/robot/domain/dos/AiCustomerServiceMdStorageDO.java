package com.zhouyuanzhi.ai.robot.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 私有知识库 Markdown 文件存储
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_ai_customer_service_md_storage")
public class AiCustomerServiceMdStorageDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String originalFileName;

    private String newFileName;

    private String filePath;

    private Long fileSize;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

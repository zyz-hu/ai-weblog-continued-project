package com.zhouyuanzhi.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_role")
public class UserRoleDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

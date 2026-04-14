package com.zhouyuanzhi.weblog.admin.model.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * @author: zyz
 * @description: 删除标签
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "删除标签 VO")
public class DeleteTagReqVO {

    @NotNull(message = "标签 ID 不能为空")
    private Long id;

}

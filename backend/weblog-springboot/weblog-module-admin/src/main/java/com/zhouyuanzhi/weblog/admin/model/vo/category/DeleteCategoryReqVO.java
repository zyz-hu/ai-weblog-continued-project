package com.zhouyuanzhi.weblog.admin.model.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * @author: zyz
 * @description: 删除分类
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "删除分类 VO")
public class DeleteCategoryReqVO {

    @NotNull(message = "分类 ID 不能为空")
    private Long id;

}

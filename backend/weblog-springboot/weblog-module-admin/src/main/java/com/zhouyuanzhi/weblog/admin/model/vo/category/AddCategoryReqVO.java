package com.zhouyuanzhi.weblog.admin.model.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

/**
 * @author: zyz
 * @description: 分类新增
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "添加分类 VO")
public class AddCategoryReqVO {

    @NotBlank(message = "分类名称不能为空")
    @Length(min = 1, max = 20, message = "分类名称字数限制 1 ~ 20 之间")
    private String name;

}

package com.zhouyuanzhi.weblog.admin.model.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: zyz
 * @description: 标签新增
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "添加标签 VO")
public class AddTagReqVO {

    @NotEmpty(message = "标签集合不能为空")
    private List<String> tags;

}

package com.zhouyuanzhi.weblog.admin.model.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * @author: zyz
 * @description: 标签模糊查询
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "标签模糊查询 VO")
public class SearchTagsReqVO {

    @NotBlank(message = "标签查询关键词不能为空")
    private String key;

}

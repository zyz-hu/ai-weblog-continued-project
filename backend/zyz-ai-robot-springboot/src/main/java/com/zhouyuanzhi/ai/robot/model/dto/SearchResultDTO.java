package com.zhouyuanzhi.ai.robot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 周元智
 * @Date: 2025/7/30 12:12
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@Builder
public class SearchResultDTO {
    private String url;
    private String title;   // 新增
    private String snippet; // 新增
    private String content; // 网页正文
    private double score;
}
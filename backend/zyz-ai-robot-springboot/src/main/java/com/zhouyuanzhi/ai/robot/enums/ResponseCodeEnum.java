package com.zhouyuanzhi.ai.robot.enums;

import com.zhouyuanzhi.ai.robot.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: 周元智
 * @url: www.zhouyuanzhi.com
 * @date: 2023-08-15 10:33
 * @description: 响应异常码
 **/
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("10001", "参数错误"),
    UNAUTHORIZED("10002", "未登录或登录已过期"),


    // ----------- 业务异常状态码 -----------
    CHAT_NOT_EXISTED("20000", "此对话不存在"),
    UPLOAD_FILE_CANT_EMPTY("20001", "上传文件不能为空"),
    ONLY_SUPPORT_MARKDOWN("20002", "仅支持 Markdown 文件（.md）"),
    UPLOAD_FILE_FAILED("20003", "文件上传失败"),
    MD_STORAGE_NOT_FOUND("20004", "知识文件不存在"),
    MD_STATUS_NOT_READY("20005", "知识文件尚未完成向量化"),
    RAG_QUERY_FAILED("20006", "知识检索失败"),
    ;

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

}

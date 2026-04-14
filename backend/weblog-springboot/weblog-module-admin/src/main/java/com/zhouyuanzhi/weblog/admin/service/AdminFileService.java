package com.zhouyuanzhi.weblog.admin.service;

import com.zhouyuanzhi.weblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: zyz
 * @description: TODO
 **/
public interface AdminFileService {
    /**
     * 上传文件
     * @param file
     * @return
     */
    Response uploadFile(MultipartFile file);
}

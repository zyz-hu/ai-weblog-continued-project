package com.zhouyuanzhi.weblog.admin.service;

import com.zhouyuanzhi.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.zhouyuanzhi.weblog.common.utils.Response;

/**
 * @author: zyz
 * @description: TODO
 **/
public interface AdminUserService {
    /**
     * 修改密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);

    /**
     * 获取当前登录用户信息
     * @return
     */
    Response findUserInfo();
}

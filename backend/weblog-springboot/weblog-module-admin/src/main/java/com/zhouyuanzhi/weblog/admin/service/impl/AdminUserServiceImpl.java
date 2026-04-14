package com.zhouyuanzhi.weblog.admin.service.impl;

import com.zhouyuanzhi.weblog.admin.model.vo.user.FindUserInfoRspVO;
import com.zhouyuanzhi.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.zhouyuanzhi.weblog.admin.service.AdminUserService;
import com.zhouyuanzhi.weblog.common.domain.dos.UserRoleDO;
import com.zhouyuanzhi.weblog.common.domain.mapper.UserMapper;
import com.zhouyuanzhi.weblog.common.domain.mapper.UserRoleMapper;
import com.zhouyuanzhi.weblog.common.enums.ResponseCodeEnum;
import com.zhouyuanzhi.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zyz
 * @description: TODO
 **/
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 修改密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    @Override
    public Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        // 拿到用户名、密码
        String username = updateAdminUserPasswordReqVO.getUsername();
        String password = updateAdminUserPasswordReqVO.getPassword();

        // 加密密码
        String encodePassword = passwordEncoder.encode(password);

        // 更新到数据库
        int count = userMapper.updatePasswordByUsername(username, encodePassword);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
    }

    /**
     * 获取当前登录用户信息
     * @return
     */
    @Override
    public Response findUserInfo() {
        // 获取存储在ThreadLocal中的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 拿到用户名
        String username = authentication.getName();

        List<UserRoleDO> roles = userRoleMapper.selectByUsername(username);
        List<String> roleCodes = roles.stream().map(UserRoleDO::getRole).collect(Collectors.toList());

        return Response.success(FindUserInfoRspVO.builder()
                .username(username)
                .roles(roleCodes)
                .build());
    }
}

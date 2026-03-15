package com.expert;

import com.expert.dto.LoginRequestDTO;
import com.expert.dto.RegisterRequestDTO;
import com.expert.dto.UserPageRequestDTO;
import com.expert.entity.SysUser;
import com.expert.entity.UserInfo;
import com.expert.result.PageResult;
import com.expert.vo.LoginResponseVO;

import java.util.List;

/**
 * 用户服务
 */
public interface SysUserService {
    LoginResponseVO login(LoginRequestDTO loginRequestDTO);

    UserInfo getMe();

    String refresh(String token);

    PageResult getUserPageList(UserPageRequestDTO userPageRequestDTO);

    SysUser getUserById(Long id);

    void saveUser(SysUser user);

    void updateUser(SysUser user);

    void deleteUser(List<Integer> ids);

    void toggleUserStatus(Long userId, String status);

    void resetPassword(Long userId);

    UserInfo register(RegisterRequestDTO registerRequestDTO);
}

package com.expert.impl;

import com.expert.ExpertsService;
import com.expert.SysUserService;
import com.expert.context.BaseContext;
import com.expert.dto.LoginRequestDTO;
import com.expert.dto.UserPageRequestDTO;
import com.expert.entity.SysUser;
import com.expert.entity.UserInfo;
import com.expert.enums.UserRole;
import com.expert.enums.UserStatus;
import com.expert.exception.BaseException;
import com.expert.mapper.ExpertsMapper;
import com.expert.mapper.SysUserMapper;
import com.expert.properties.JwtProperties;
import com.expert.result.PageResult;
import com.expert.utils.JwtUtil;
import com.expert.vo.LoginResponseVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ExpertsService expertsService;

    @Autowired
    private ExpertsMapper expertsMapper;

    @Override
    public LoginResponseVO login(LoginRequestDTO loginRequestDTO) {

        SysUser sysUser = sysUserMapper.getByUsername(loginRequestDTO.getUsername());

        if (sysUser == null) {
            // 用户不存在
            return null;
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), sysUser.getPassword())) {
            // 密码错误
            return null;
        }

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", sysUser.getUserId());
        claims.put("role", sysUser.getRole());
        claims.put("status", sysUser.getStatus());

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(sysUser, userInfo);

        return LoginResponseVO.builder()
                .token(token)
                .userInfo(userInfo)
                .build();

    }

    @Override
    public UserInfo getMe() {
        Long currentId = BaseContext.getCurrentId();
        return sysUserMapper.getInfo(currentId);
    }

    @Override
    public String refresh(String token) {
        // 校验令牌，获取令牌中的时间
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            long exp = claims.getExpiration().getTime();// token过期时间
            long current = System.currentTimeMillis();// 当前时间

            if (exp - current < 10 * 60 * 1000) {// token在10分钟内过期
                return JwtUtil.createJWT(
                        jwtProperties.getAdminSecretKey(),
                        jwtProperties.getAdminTtl(),
                        claims);
            } else {
                log.info("Token还有较长时间才过期，无需刷新");
            }
        } catch (Exception e) {
            throw new BaseException("令牌解析失败");
        }

        return token;
    }

    @Override
    public PageResult getUserPageList(UserPageRequestDTO userPageRequestDTO) {
        PageHelper.startPage(userPageRequestDTO.getPage(), userPageRequestDTO.getPageSize());
        Page<UserInfo> pageList = sysUserMapper.getPageList(userPageRequestDTO);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public SysUser getUserById(Long id) {
        return sysUserMapper.getUserById(id);
    }

    @Override
    public void saveUser(SysUser user) {
        // 检查用户名是否已存在
        boolean existingUser = sysUserMapper.existsByUsername(user.getUsername());
        if (existingUser) {
            throw new RuntimeException("用户名已存在");
        }

        // 设置默认密码,后续需要加密存储
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        } else {
            user.setPassword(user.getPassword());
        }

        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setCreatedAdminId(BaseContext.getCurrentId());
        sysUserMapper.insert(user);
    }

    @Override
    public void updateUser(SysUser user) {
        user.setUpdatedTime(LocalDateTime.now());
        sysUserMapper.update(user);
    }

    @Override
    // todo 这里的删除，如果对象是专家，应该调用专家管理板块的删除逻辑
    public void deleteUser(List<Integer> ids) {
        // 分离专家用户ID和普通用户ID
        List<Integer> expertUserIds = new ArrayList<>();
        List<Integer> regularUserIds = new ArrayList<>();

        for (Integer id : ids) {
            SysUser user = sysUserMapper.getUserById(Long.valueOf(id));
            if (user == null) {
                throw new RuntimeException("用户不存在，ID: " + id);
            }

            // 检查是否包含超级管理员
            if (UserRole.SUPER_ADMIN.equals(user.getRole())) {
                throw new RuntimeException("不能删除超级管理员");
            }

            // 分离专家用户和普通用户
            if (UserRole.EXPERT.equals(user.getRole())) {
                // 通过用户id获取专家id从而调用专家逻辑完成删除
                Integer expertId = expertsMapper.getExpertIdByUserId(id);
                if (expertId != null) {
                    expertUserIds.add(expertId);
                } else {
                    // 如果找不到专家ID，按普通用户处理或抛出异常
                    log.warn("用户ID {} 标记为专家，但未找到对应的专家记录，按普通用户删除", id);
                    regularUserIds.add(id);
                }
            } else {
                regularUserIds.add(id);
            }
        }

        // 调用专家服务的删除方法（传递专家ID列表）
        if (!expertUserIds.isEmpty()) {
            try {
                expertsService.delete(expertUserIds);
            } catch (Exception e) {
                throw new RuntimeException("删除专家失败: " + e.getMessage());
            }
        }

        // 删除普通用户（管理员等）
        if (!regularUserIds.isEmpty()) {
            try {
                sysUserMapper.delete(regularUserIds);
            } catch (Exception e) {
                throw new RuntimeException("删除管理失败: " + e.getMessage());
            }
        }
    }

    @Override
    public void toggleUserStatus(Long userId, String status) {
        SysUser user = sysUserMapper.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能禁用超级管理员
        if (UserRole.SUPER_ADMIN.equals(user.getRole()) && UserStatus.INACTIVE.equals(status)) {
            throw new RuntimeException("不能禁用超级管理员");
        }

        user.setStatus(UserStatus.valueOf(status));
        user.setUpdatedTime(LocalDateTime.now());
        sysUserMapper.updateStatus(user);
    }

    @Override
    public void resetPassword(Long userId) {
        SysUser user = sysUserMapper.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdatedTime(LocalDateTime.now());
        sysUserMapper.updatePassword(user);
    }

    @Override
    public UserInfo register(com.expert.dto.RegisterRequestDTO registerRequestDTO) {
        // 1. 检查用户是否存在
        if (sysUserMapper.existsByUsername(registerRequestDTO.getUsername())) {
            throw new BaseException("用户名已存在");
        }
        if (sysUserMapper.existsByPhone(registerRequestDTO.getPhone())) {
            throw new BaseException("手机号已存在");
        }

        // 2. 密码加密
        String encodedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());

        // 3. 构建User对象
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(registerRequestDTO, sysUser);
        sysUser.setPassword(encodedPassword);
        sysUser.setCreatedTime(LocalDateTime.now());
        sysUser.setStatus(UserStatus.ACTIVE); 

        // 4. 插入数据库
        sysUserMapper.insert(sysUser);

        // 5. 返回UserInfo
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(sysUser, userInfo);
        return userInfo;
    }
}

package com.expert.controller;

import com.expert.SysUserService;
import com.expert.annotation.RequireRole;
import com.expert.enums.UserRole;
import com.expert.dto.UserPageRequestDTO;
import com.expert.entity.SysUser;
import com.expert.result.PageResult;
import com.expert.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/pageList")
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    Result<PageResult> getUserPageList(UserPageRequestDTO userPageRequestDTO) {
        log.info("分页查询用户列表：{}", userPageRequestDTO);
        PageResult pageResult = sysUserService.getUserPageList(userPageRequestDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    Result<SysUser> getUserById(@PathVariable Long id) {
        log.info("获取用户信息：{}", id);
        SysUser user = sysUserService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    Result saveUser(@RequestBody SysUser user) {
        log.info("添加用户：{}", user);
        sysUserService.saveUser(user);
        return Result.success();
    }

    @PutMapping
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    Result updateUser(@RequestBody SysUser user) {
        log.info("更新用户信息：{}", user);
        sysUserService.updateUser(user);
        return Result.success();
    }

    @DeleteMapping
    @RequireRole({UserRole.SUPER_ADMIN})
    Result deleteUser(@RequestBody List<Integer> ids) {
        log.info("删除用户：{}", ids);
        sysUserService.deleteUser(ids);
        return Result.success();
    }

    @PatchMapping("/{userId}/status")
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    Result toggleUserStatus(@PathVariable Long userId,@RequestParam String status) {
        log.info("切换用户状态：{}, {}", userId, status);
        sysUserService.toggleUserStatus(userId, status);
        return Result.success();
    }

    @PostMapping("/{userId}/reset-password")
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    Result resetPassword(@PathVariable Long userId) {
        log.info("重置用户密码：{}", userId);
        sysUserService.resetPassword(userId);
        return Result.success();
    }
}

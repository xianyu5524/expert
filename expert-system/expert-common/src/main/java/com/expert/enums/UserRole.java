package com.expert.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    SUPER_ADMIN("超级管理员"),
    ADMIN("管理员"),
    EXPERT("专家");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }
}

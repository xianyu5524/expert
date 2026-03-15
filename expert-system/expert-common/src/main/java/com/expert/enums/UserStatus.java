package com.expert.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatus {
    ACTIVE("激活"),
    INACTIVE("未激活"),
    PENDING("待审核");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public static String format(String userStatus){
        if(userStatus ==null) return "";
        try {
            return valueOf(userStatus).getDescription();
        } catch (IllegalArgumentException e) {
            return "无";
        }
    }
}

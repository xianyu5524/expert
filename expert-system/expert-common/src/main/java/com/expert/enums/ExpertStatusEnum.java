package com.expert.enums;


import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum ExpertStatusEnum {
    PENDING("待审核"),
    APPROVED("已通过"),
    REJECTED("已拒绝");

    private final String description;

    ExpertStatusEnum(String description){
        this.description=description;
    }

    public static String format(String status) {
        if (status == null) return "";
        try {
            return valueOf(status).getDescription();
        } catch (IllegalArgumentException e) {
            return "无"; // 或者根据需求返回默认值
        }
    }
}

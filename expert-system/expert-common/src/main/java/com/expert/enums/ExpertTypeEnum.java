package com.expert.enums;


import lombok.Getter;

@Getter
public enum ExpertTypeEnum {
    EDUCATION("教育专家"),
    ENTERPRISE("企业专家");

    private final String description;

    ExpertTypeEnum(String description){
        this.description=description;
    }

    public static String format(String expertType) {
        if (expertType == null) return "";
        try {
            return valueOf(expertType).getDescription();
        } catch (IllegalArgumentException e) {
            return "无"; // 或者根据需求返回默认值
        }
    }
}

package com.expert.enums;

import lombok.Getter;

/**
 * 获奖情况枚举
 */
@Getter
public enum ProjectAwardEnum {
    FIRST_PRIZE("FIRST_PRIZE", "一等奖"),
    SECOND_PRIZE("SECOND_PRIZE", "二等奖"),
    THIRD_PRIZE("THIRD_PRIZE", "三等奖");

    private final String code;
    private final String description;

    ProjectAwardEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String format(String award){
        if(award==null) return "";
        try {
            return valueOf(award).getDescription();
        } catch (IllegalArgumentException e) {
            return "无";
        }
    }
}

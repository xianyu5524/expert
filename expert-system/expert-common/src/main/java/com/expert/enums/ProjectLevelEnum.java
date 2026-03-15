package com.expert.enums;

import lombok.Getter;

/**
 * 项目等级枚举
 */
@Getter
public enum ProjectLevelEnum {
    KEY("KEY", "重点"),
    GENERAL("GENERAL", "一般"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String description;

    ProjectLevelEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String format(String projectLevel){
        if(projectLevel==null) return "";
        try {
            return valueOf(projectLevel).getDescription();
        } catch (IllegalArgumentException e) {
            return "无";
        }
    }
}

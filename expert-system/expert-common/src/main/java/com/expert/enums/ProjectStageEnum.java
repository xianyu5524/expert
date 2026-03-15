package com.expert.enums;

import lombok.Getter;

/**
 * 项目阶段枚举
 */
@Getter
public enum ProjectStageEnum {
    SCHOOL("SCHOOL", "校赛"),
    PROVINCIAL("PROVINCIAL", "省赛"),
    NATIONAL("NATIONAL", "国赛");

    private final String code;
    private final String description;

    ProjectStageEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public static String format(String stage){
        if(stage ==null) return "";
        try {
            return valueOf(stage).getDescription();
        } catch (IllegalArgumentException e) {
            return "无";
        }
    }
}

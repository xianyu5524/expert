package com.expert.enums;

import lombok.Getter;

/**
 * 项目状态枚举
 */

@Getter
public enum ProjectStatusEnum {
    PREPARING("PREPARING", "准备中"),
    ONGOING("ONGOING", "进行中"),
    SUSPENDED("SUSPENDED", "已暂停"),
    COMPLETED("COMPLETED", "已完成"),
    TERMINATED("TERMINATED", "已终止");

    private final String code;
    private final String description;

    ProjectStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String format(String projectStatus){
        if(projectStatus ==null) return "";
        try {
            return valueOf(projectStatus).getDescription();
        } catch (IllegalArgumentException e) {
            return "无";
        }
    }
}

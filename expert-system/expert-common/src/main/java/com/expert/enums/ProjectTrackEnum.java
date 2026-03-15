package com.expert.enums;

import lombok.Getter;

/**
 * 项目赛道枚举
 */
@Getter
public enum ProjectTrackEnum {
    MAIN_TRACK("MAIN_TRACK", "高教主赛道"),
    RED_TOURISM_TRACK("RED_TOURISM_TRACK", "红旅赛道"),
    INDUSTRY_TRACK("INDUSTRY_TRACK", "产业赛道");

    private final String code;
    private final String description;

    ProjectTrackEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String format(String track){
        if(track ==null) return "";
        try {
            return valueOf(track).getDescription();
        } catch (IllegalArgumentException e) {
            return "无";
        }
    }
}
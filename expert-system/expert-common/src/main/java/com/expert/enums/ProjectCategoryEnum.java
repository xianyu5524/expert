package com.expert.enums;

import lombok.Getter;


/**
 * 项目组别枚举
 */
@Getter
public enum ProjectCategoryEnum {
    // 高教主赛道组别
    UNDERGRADUATE_CREATIVE("UNDERGRADUATE_CREATIVE", "本科生创意组", "MAIN_TRACK"),
    UNDERGRADUATE_ENTREPRENEURIAL("UNDERGRADUATE_ENTREPRENEURIAL", "本科生创业组", "MAIN_TRACK"),
    POSTGRADUATE_CREATIVE("POSTGRADUATE_CREATIVE", "研究生创意组", "MAIN_TRACK"),
    POSTGRADUATE_ENTREPRENEURIAL("POSTGRADUATE_ENTREPRENEURIAL", "研究生创业组", "MAIN_TRACK"),

    // 红旅赛道组别
    PUBLIC_WELFARE("PUBLIC_WELFARE", "公益组", "RED_TOURISM_TRACK"),
    RED_CREATIVE("RED_CREATIVE", "创意组", "RED_TOURISM_TRACK"),
    RED_ENTREPRENEURIAL("RED_ENTREPRENEURIAL", "创业组", "RED_TOURISM_TRACK"),

    // 产业赛道组别
    ENTERPRISE_PROPOSITION("ENTERPRISE_PROPOSITION", "企业命题组", "INDUSTRY_TRACK"),
    REGIONAL_INDUSTRY("REGIONAL_INDUSTRY", "区域特色产业组", "INDUSTRY_TRACK"),
    ACHIEVEMENT_TRANSFORMATION("ACHIEVEMENT_TRANSFORMATION", "成果转化组", "INDUSTRY_TRACK");

    private final String code;
    private final String description;
    private final String track;

    ProjectCategoryEnum(String code, String description, String track) {
        this.code = code;
        this.description = description;
        this.track = track;
    }

    public static String format(String track){
        if(track==null) return "";
        try {
            return valueOf(track).getDescription();
        } catch (IllegalArgumentException e) {
            return "无";
        }
    }
}

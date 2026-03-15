package com.expert.enums;

import lombok.Getter;

@Getter
public enum GenderEnum {

    MALE("男"),
    FEMALE("女");

    private final String description;

    GenderEnum(String description){
        this.description=description;
    }

    public static String format(String gender) {
        if (gender == null) return "";
        try {
            return valueOf(gender).getDescription();
        } catch (IllegalArgumentException e) {
            return "无"; // 或者根据需求返回默认值
        }
    }
}

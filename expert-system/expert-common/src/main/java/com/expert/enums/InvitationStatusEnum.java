package com.expert.enums;

import lombok.Getter;

/**
 * 邀请状态枚举
 */
@Getter
public enum InvitationStatusEnum {
    PENDING("待处理"),
    ACCEPTED("已接受"),
    REJECTED("已拒绝");

    private final String description;

    InvitationStatusEnum(String description) {
        this.description = description;
    }
}

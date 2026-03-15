package com.expert.enums;

import lombok.Getter;

/**
 * 统一响应码枚举
 */
@Getter
public enum ResponseCodeEnum {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器异常");

    private final int code;
    private final String message;

    ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

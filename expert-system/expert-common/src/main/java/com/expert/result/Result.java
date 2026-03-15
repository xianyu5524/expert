package com.expert.result;

import com.expert.enums.ResponseCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;


    private boolean success;
    private int code;
    private String message;
    private T data;

    // 成功的静态方法
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.success = true;
        result.code = ResponseCodeEnum.SUCCESS.getCode();
        result.message = ResponseCodeEnum.SUCCESS.getMessage();
        return result;
    }

    // 成功且携带数据的静态方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.success = true;
        result.code = ResponseCodeEnum.SUCCESS.getCode();
        result.message = ResponseCodeEnum.SUCCESS.getMessage();
        result.data = data;
        return result;
    }

    // 失败的静态方法
    public static <T> Result<T> error(ResponseCodeEnum codeEnum) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = codeEnum.getCode();
        result.message = codeEnum.getMessage();
        return result;
    }

    // 失败且携带自定义消息的静态方法
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = ResponseCodeEnum.BAD_REQUEST.getCode();
        result.message = message;
        return result;
    }
}
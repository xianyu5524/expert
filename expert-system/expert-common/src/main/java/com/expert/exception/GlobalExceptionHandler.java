package com.expert.exception;

import com.expert.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public Result exceptionHandler(BaseException ex){
        log.error("业务异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获参数校验异常
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result exceptionHandler(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常信息：{}", errorMessage);
        return Result.error(errorMessage);
    }

    /**
     * 捕获其他未知异常
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception ex){
        log.error("系统异常信息：", ex);
        return Result.error("系统繁忙，请稍后再试");
    }
}

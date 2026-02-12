package com.bookstore.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// 全局异常处理：统一返回业务响应结构
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBiz(BizException ex) {
        // 业务异常直出错误码与提示
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValid(MethodArgumentNotValidException ex) {
        // 表单校验错误取首个提示
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数错误" : fieldError.getDefaultMessage();
        return ApiResponse.fail(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraint(ConstraintViolationException ex) {
        // 路径/参数校验错误
        return ApiResponse.fail(400, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) {
        // 未捕获异常统一返回 500
        return ApiResponse.fail(500, ex.getMessage());
    }
}

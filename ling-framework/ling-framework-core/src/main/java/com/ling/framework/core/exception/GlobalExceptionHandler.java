package com.ling.framework.core.exception;

import com.ling.framework.core.result.R;
import com.ling.framework.core.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String getRequestInfo(HttpServletRequest request) {
        return "请求路径: " + request.getRequestURI() + ", 请求方法: " + request.getMethod() + ", 请求参数: " + getRequestParams(request);
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            params.put(paramName, paramValue);
        }
        return params;
    }

    @ExceptionHandler(LingException.class)
    public R<Void> handleBizException(LingException e, HttpServletRequest request) {
        log.error("业务异常: {}, 请求信息: {}", e.getMsg(), getRequestInfo(request), e);
        return R.failed(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常: {}, 请求信息: {}", errorMsg, getRequestInfo(request), e);
        return R.failed(ResultCode.VALIDATE_FAILED.getCode(), errorMsg);
    }

    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e, HttpServletRequest request) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定异常: {}, 请求信息: {}", errorMsg, getRequestInfo(request), e);
        return R.failed(ResultCode.VALIDATE_FAILED.getCode(), errorMsg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String errorMsg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.error("约束校验异常: {}, 请求信息: {}", errorMsg, getRequestInfo(request), e);
        return R.failed(ResultCode.VALIDATE_FAILED.getCode(), errorMsg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.error("缺少请求参数: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.PARAM_NOT_COMPLETE.getCode(), "缺少必填参数: " + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("参数类型错误: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.PARAM_TYPE_ERROR.getCode(), "参数类型错误: " + e.getName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("请求体读取异常: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.PARAM_IS_INVALID.getCode(), "请求体格式错误");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("请求方法不支持: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.FAILED.getCode(), "不支持的请求方法: " + e.getMethod());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("处理器未找到: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.NOT_FOUND.getCode(), "请求的资源不存在");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public R<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("非法参数异常: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.PARAM_IS_INVALID.getCode(), e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public R<Void> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.error("非法状态异常: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.FAILED.getCode(), e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public R<Void> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }

    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {}, 请求信息: {}", e.getMessage(), getRequestInfo(request), e);
        return R.failed(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }
}
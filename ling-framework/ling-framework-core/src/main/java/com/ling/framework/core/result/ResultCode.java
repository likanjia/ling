package com.ling.framework.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    VALIDATE_FAILED(400, "参数校验失败"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    LOGIN_SUCCESS(200, "登录成功"),
    LOGIN_FAILED(401, "登录失败"),
    TOKEN_EXPIRED(401, "token已过期"),
    TOKEN_INVALID(401, "token无效"),

    USER_NOT_FOUND(404, "用户不存在"),
    USER_DISABLED(403, "用户已被禁用"),
    USER_PASSWORD_ERROR(401, "密码错误"),
    USER_ALREADY_EXISTS(400, "用户已存在"),

    PERMISSION_DENIED(403, "没有权限访问该资源"),
    ROLE_NOT_FOUND(404, "角色不存在"),
    ROLE_ALREADY_EXISTS(400, "角色已存在"),

    PARAM_IS_INVALID(400, "参数无效"),
    PARAM_IS_BLANK(400, "参数为空"),
    PARAM_TYPE_ERROR(400, "参数类型错误"),
    PARAM_NOT_COMPLETE(400, "参数缺失"),

    DATA_NOT_FOUND(404, "数据不存在"),
    DATA_ALREADY_EXISTS(400, "数据已存在"),
    DATA_IS_NULL(400, "数据为空"),

    FILE_UPLOAD_ERROR(500, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(500, "文件下载失败"),
    FILE_NOT_FOUND(404, "文件不存在"),
    FILE_TYPE_NOT_SUPPORT(400, "文件类型不支持"),
    FILE_SIZE_EXCEED(400, "文件大小超出限制"),

    DATABASE_ERROR(500, "数据库操作失败"),
    CACHE_ERROR(500, "缓存操作失败"),
    REDIS_ERROR(500, "Redis操作失败"),

    SERVICE_UNAVAILABLE(503, "服务不可用"),
    SERVICE_TIMEOUT(504, "服务超时");

    private final Integer code;
    private final String msg;
}
package com.ling.framework.core.exception;

import com.ling.framework.core.result.ResultCode;
import lombok.Getter;

@Getter
public class LingException extends RuntimeException {

    private final Integer code;
    private final String msg;

    public LingException(String msg) {
        super(msg);
        this.code = ResultCode.FAILED.getCode();
        this.msg = msg;
    }

    public LingException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public LingException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    public LingException(ResultCode resultCode, String msg) {
        super(msg);
        this.code = resultCode.getCode();
        this.msg = msg;
    }

    public LingException(String msg, Throwable cause) {
        super(msg, cause);
        this.code = ResultCode.FAILED.getCode();
        this.msg = msg;
    }

    public LingException(Integer code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public LingException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMsg(), cause);
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    public LingException(ResultCode resultCode, String msg, Throwable cause) {
        super(msg, cause);
        this.code = resultCode.getCode();
        this.msg = msg;
    }
}
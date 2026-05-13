package com.ling.framework.core.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private T data;

    public static <T> R<T> ok() {
        return restResult(null, 200, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "操作成功");
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, 200, msg);
    }

    public static <T> R<T> failed() {
        return restResult(null, 500, "操作失败");
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, 500, msg);
    }

    public static <T> R<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}

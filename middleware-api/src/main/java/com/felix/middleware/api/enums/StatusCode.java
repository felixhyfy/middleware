package com.felix.middleware.api.enums;

/**
 * @description: 通用状态码
 * @author: Felix
 * @date: 2021/4/27 21:37
 */
public enum StatusCode {
    SUCCESS(0, "成功"),
    FAIL(-1, "失败"),
    INVALID_PARAMS(201, "非法的参数"),
    INVALID_GRANT_TYPE(202, "非法的授权类型")
    ;


    private Integer code;

    private String msg;

    StatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

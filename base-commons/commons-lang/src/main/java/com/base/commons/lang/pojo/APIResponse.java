package com.base.commons.lang.pojo;

import java.io.Serializable;

/**
 * @author heyawei
 * @date 2020-11-10
 */
public class APIResponse<T> implements Serializable {

    public static final int DEFAULT_SUCCESS_CODE = 0;
    public static final int DEFAULT_ERROR_CODE = 1000;
    /**
     * 状态码：0-成功，1-失败
     */
    private int code;

    /**
     * 错误消息，如果成功可为空或SUCCESS
     */
    private String msg;

    /**
     * 返回结果数据
     */
    private T data;

    public int getCode() {
        return code;
    }

    public APIResponse<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public APIResponse<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public APIResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> APIResponse<T> success() {
        return success(null);
    }

    public static <T> APIResponse<T> success(T data) {
        APIResponse<T> result = new APIResponse<>();
        result.setCode(DEFAULT_SUCCESS_CODE);
        result.setMsg("SUCCESS");
        result.setData(data);
        return result;
    }

    public static <T> APIResponse<T> error(String msg) {
        return error(DEFAULT_ERROR_CODE, msg);
    }

    public static <T> APIResponse<T> error(int code, String msg) {
        APIResponse<T> result = new APIResponse<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}

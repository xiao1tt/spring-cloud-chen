package com.base.commons.lang.pojo;

/**
 * @author chenxiaotong
 */
public class BizNotification extends RuntimeException {

    private int code = APIResponse.DEFAULT_ERROR_CODE;

    public BizNotification() {
        super();
    }

    public BizNotification(String message) {
        super(message);
    }

    public BizNotification(String message, Throwable cause) {
        super(message, cause);
    }

    public BizNotification(Throwable cause) {
        super(cause);
    }

    public BizNotification(int code) {
        super();
        this.code = code;
    }

    public BizNotification(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizNotification(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizNotification(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

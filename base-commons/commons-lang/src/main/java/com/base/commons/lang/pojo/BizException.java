package com.base.commons.lang.pojo;

/**
 * @author chenxiaotong
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -504955880063536901L;

    private int code = APIResponse.DEFAULT_ERROR_CODE;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(int code) {
        super();
        this.code = code;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

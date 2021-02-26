package com.github.huoyu820125.idstar.error;


/**
 * 调用异常
 * @author sq
 * @version 1.0
 */
public final class CallException extends RuntimeException{
    private RClassify classify;
    private String code;

    protected CallException(RClassify classify, String msg, String code) {
        super(msg);
        this.code = code;
        this.classify = classify;
    }

    protected CallException(RClassify classify, String msg, Throwable cause, String code) {
        super(msg, cause);
        this.code = code;
        this.classify = classify;
    }

    protected CallException(RClassify classify, Throwable cause, String code) {
        super(cause);
        this.code = code;
        this.classify = classify;
    }

    protected CallException(RClassify classify, String msg) {
        this(classify, msg, "");
    }

    protected CallException(RClassify classify, String msg, Throwable cause) {
        this(classify, msg, cause, "");
    }

    protected CallException(RClassify classify, Throwable cause) {
        this(classify, cause, "");
    }

    protected RClassify getClassify() {
        return classify;
    }

    protected String getCode() {
        return code;
    }
}

package com.github.huoyu820125.idstar.error;


/**
 * @Title 调用异常
 * @Athor SunQian
 * @CreateTime 2020/5/18 18:48
 * @Description:
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

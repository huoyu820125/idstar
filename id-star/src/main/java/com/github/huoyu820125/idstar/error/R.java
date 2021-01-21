package com.github.huoyu820125.idstar.error;



import java.io.Serializable;

/**
 * 函数返回值，调用结果
 */
public final class R<T> implements Serializable {
    private static final long serialVersionUID = -7170024457221876233L;
    //成功还是失败
    private boolean success = false;
    //返回数据，失败的时为null
    private T data;
    //错误分类，成功的时为null
    //具体参考FailedClassify，决定错误信息能否直接在前端显示，方便错误定位
    private String classify;
    //错误码，成功的时为200，兼容老代码
    //具体业务定义具体定义，不同接口的错误码是允许重复的，REnum中有一些老业务的错误枚举定义
    private String code;
    //错误信息，成功的时为null
    private String message;


    /**
     * @title: 构造不返回无数据的成功的回应
     * @author: SunQian
     * @date: 2020/10/28 14:48
     * @descritpion: todo

     * @return todo
     */
    public R() {
        success = true;
        this.data = null;
        this.classify = null;
        this.code = "200";//兼容老代码
        this.message = null;
    }

    /**
     * @title: 构造返回数据的成功的回应
     * @author: SunQian
     * @date: 2020/10/28 11:25
     * @descritpion: todo
     * @param result 回应数据，可为null
     * @return todo
     */
    public R(T result) {
        success = true;
        this.data = result;
        this.classify = null;
        this.code = "200";//兼容老代码
        this.message = null;
    }

    /**
     * @title: 构造错误回应
     * @author: SunQian
     * @date: 2020/10/28 11:25
     * @descritpion: 用户不可直接构造错误回应，应使用RClassify枚举抛异常，由统一拦截器构造异常回应
     * @param classify
     * @param message
     * @param code
     * @return todo
     */
    protected R(RClassify classify, String message, String code) {
        if (classify.equals(RClassify.success)) {
            throw RClassify.bug.exception("错误的应用api，R(RClassify classify, String code, String message)不能应用于成功的场景");
        }
        success = false;
        this.classify = classify.value();
        this.code = code;
        this.message = message;
    }

    /**
     * @title: 构造错误回应，直接将异常信息抛给前端显示
     * @author: SunQian
     * @date: 2020/10/28 11:26
     * @descritpion: 用户不可直接构造错误回应，应使用RClassify枚举抛异常，由统一拦截器构造异常回应
     * @param e
     * @return todo
     */
    protected R(CallException e) {
        this(e.getClassify(), e.getMessage(), e.getCode());
    }

    /**
     * @title: 构造错误回应，对前端屏蔽异常信息
     * @author: SunQian
     * @date: 2020/10/28 11:26
     * @descritpion: 用户不可直接构造错误回应，应使用RClassify枚举抛异常，由统一拦截器构造异常回应
     * @param e
     * @param message   让前端显示的错误信息，对前端屏蔽异常本来的信息
     * @return todo
     */
    protected R(CallException e, String message) {
        this(e.getClassify(), message, e.getCode());
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

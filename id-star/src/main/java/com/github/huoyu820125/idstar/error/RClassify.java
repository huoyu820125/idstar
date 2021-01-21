package com.github.huoyu820125.idstar.error;

import org.apache.commons.lang3.StringUtils;

/**
 * @Title 返回结果情况分类
 * @Athor SunQian
 * @CreateTime 2020/5/18 19:00
 * @Description: todo
 */
public enum RClassify {

    unknow("unknow", "未知错误", false),//禁止直接抛给前端
    success("success", "成功", true),//历史原因，前端习惯认code=200是成功
    param("param", "参数错误", true),//此类异常应该直接抛给前端
    dependence("dependence", "外部异常", false),//依赖的服务未启动/访问超时，第三方jar方法返回失败/异常等
    bug("bug", "服务异常", false),//因数据不完整，null异常等各种bug，导业务无法继续执行
    refused("refused", "存在业务限制", true),//余额不足等检查不通过
    noData("noData", "无数据", true),//此类异常可直接抛给前端
    noLogin("noLogin", "登录状态异常", true),//此类异常可直接抛给前端
    unauthorized("unauthorized", "授权状态异常", true),//此类异常可直接抛给前端
    ;

    private String value;
    private String title;
    //异常信息是否可以显示到前端
    private Boolean showAble;

    RClassify(String value, String title, Boolean showAble) {
        this.value = value;
        this.title = title;
        this.showAble = showAble;
    }

    public static Boolean isValid(String text) {
        return !toEnum(text).equals(unknow);
    }

    public static RClassify toEnum(String text) {
        if (StringUtils.isEmpty(text)) {
            return unknow;
        }

        for (RClassify oneEnum : RClassify.values()) {
            if(oneEnum.equals(text)){
                return oneEnum;
            }
        }

        return unknow;
    }

    public Boolean equals(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        return value.equals(text);
    }

    public String value() {
        return value;
    }

    public String title() {
        return title;
    }

    public CallException exception(String message, Throwable cause, String code) {
        return new CallException(this, message, cause, code);
    }

    public CallException exception() {
        return exception(title, null, value.toString());
    }

    public CallException exception(String message) {
        return exception(message, null, value.toString());
    }

    public CallException exception(String message, String code) {
        return exception(message, null, code);
    }

    public CallException exception(String message, Throwable cause) {
        return exception(message, cause, value.toString());
    }
}

package com.github.huoyu820125.idstar.http.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * http方法
 * @author SunQian
 * @version 1.1
 */
public enum EHttpMethod {

    UNKNOW("未知", "UNKNOW"),
    POST("POST", "POST"),
    GET("GET", "GET"),
    PUT("PUT", "PUT"),
    DELETE("DELETE", "DELETE"),
    ;


    private String title;
    private String value;

    EHttpMethod(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public static Boolean isValid(String text) {
        return !toEnum(text).equals(UNKNOW);
    }

    public static EHttpMethod toEnum(String text) {
        if (StringUtils.isEmpty(text)) {
            return UNKNOW;
        }

        for (EHttpMethod oneEnum : EHttpMethod.values()) {
            if(oneEnum.equals(text)){
                return oneEnum;
            }
        }

        return UNKNOW;
    }

    public Boolean equals(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        return value.equals(text);
    }

    public String title() {
        return title;
    }

    public String value() {
        return value;
    }
}

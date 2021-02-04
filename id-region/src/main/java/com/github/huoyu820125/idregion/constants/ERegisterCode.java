package com.github.huoyu820125.idregion.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * @Athor SunQian
 * @CreateTime 2019/7/8 9:46
 * @Description: 数值型枚举模板
 */
public enum ERegisterCode {

    unknow(-1, "未知"),
    success(0, "成功"),
    masterIniting(1, "master正在初始化"),
    existNodeId(2, "存在相同结点id的结点"),
    tooMoreNode(3, "最多4个结点"),
    ;


    private Integer value;
    private String title;

    ERegisterCode(Integer value, String title) {
        this.value = value;
        this.title = title;
    }

    public static ERegisterCode toEnum(Number num) {
        if (null == num) {
            return unknow;
        }

        for (ERegisterCode oneEnum : ERegisterCode.values()) {
            if(oneEnum.equals(num)){
                return oneEnum;
            }
        }

        return unknow;
    }

    public static ERegisterCode toEnum(String text) {
        if (StringUtils.isEmpty(text)) {
            return unknow;
        }

        Long num = null;
        try {
            num = Long.valueOf(text);
        } catch (Exception e) {
            return unknow;
        }

        return toEnum(num);
    }

    public static Boolean isValid(Number num) {
        return !toEnum(num).equals(unknow);
    }

    public static Boolean isValid(String text) {
        return !toEnum(text).equals(unknow);
    }

    public Boolean equals(Number num) {
        if (null == num) {
            return false;
        }

        return value.equals(num.intValue());
    }

    public Boolean equals(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        Integer value = null;
        try {
            value = Integer.valueOf(text);
        } catch (Exception e) {
            return false;
        }

        return equals(value);
    }

    public Integer value() {
        return value;
    }

    public String title() {
        return title;
    }
}

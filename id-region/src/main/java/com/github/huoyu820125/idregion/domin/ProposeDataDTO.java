package com.github.huoyu820125.idregion.domin;

import java.io.Serializable;

/**
 * @Title ProposeDataDTO
 * @Athor SunQian
 * @CreateTime 2021/2/1 16:12
 * @Description: todo
 */
public class ProposeDataDTO implements Serializable {
    private int	serialNum;//流水号,1开始递增，保证全局唯一
    private String	value;//提议内容

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

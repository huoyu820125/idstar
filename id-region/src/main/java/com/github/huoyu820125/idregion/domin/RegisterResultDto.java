package com.github.huoyu820125.idregion.domin;

import java.io.Serializable;

/**
 * @Title 结点注册结果
 * @Athor SunQian
 * @CreateTime 2021/2/3 15:30
 * @Description: todo
 */
public class RegisterResultDto implements Serializable {
    private Integer nodeId;
    private Integer code;
    private String reason;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

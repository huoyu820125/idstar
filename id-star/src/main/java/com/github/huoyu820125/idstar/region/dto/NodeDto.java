package com.github.huoyu820125.idstar.region.dto;

import java.io.Serializable;

/**
 * @Title 结点信息
 * @Athor SunQian
 * @CreateTime 2021/2/23 17:30
 * @Description: todo
 */
public class NodeDto implements Serializable {
    private Integer nodeId;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }
}

package com.github.huoyu820125.idstar.service.client.dto;

import java.io.Serializable;

/**
 * 结点信息
 * @author SunQian
 * @version 1.1
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

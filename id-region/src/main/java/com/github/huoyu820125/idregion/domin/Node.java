package com.github.huoyu820125.idregion.domin;

/**
 * @Title Node
 * @Athor SunQian
 * @CreateTime 2021/2/3 10:57
 * @Description: todo
 */
public class Node {
    private String address;
    private Boolean isInited;
    private Integer nodeId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean isInited() {
        return isInited;
    }

    public void isInited(Boolean inited) {
        isInited = inited;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }
}

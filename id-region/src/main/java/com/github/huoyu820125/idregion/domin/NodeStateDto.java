package com.github.huoyu820125.idregion.domin;

import java.io.Serializable;

/**
 * @Title 结点状态
 * @Athor SunQian
 * @CreateTime 2021/2/3 10:38
 * @Description: todo
 */
public class NodeStateDto implements Serializable {
    //是否已完成初始化
    private Boolean isInited;
    //上次被分配到的结点id,初始为null
    private Integer nodeId;

    public Boolean getIsInited() {
        return isInited;
    }

    public void setIsInited(Boolean inited) {
        isInited = inited;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }
}

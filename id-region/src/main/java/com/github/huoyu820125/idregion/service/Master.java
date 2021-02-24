package com.github.huoyu820125.idregion.service;

import com.github.huoyu820125.idregion.constants.ERegisterCode;
import com.github.huoyu820125.idregion.domin.Node;
import com.github.huoyu820125.idregion.domin.RegisterResultDto;
import com.github.huoyu820125.idstar.region.IdRegionClient;
import com.github.huoyu820125.idstar.region.dto.NodeStateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Title 主结点
 * @Athor SunQian
 * @CreateTime 2021/2/2 15:22
 * @Description: todo
 */
@Component
public class Master {
    private final Logger log = LoggerFactory.getLogger(getClass());

    Boolean isInited = false;

    @Autowired
    IdRegionService idRegionService;
    
    @Autowired
    Cluster cluster;

    /**
     * @title: 给master结点分配结点id
     * @author: SunQian
     * @date: 2021/2/2 17:04
     * @descritpion: todo
     * @param masterAddress master结点地址
     * @param addressList 集群结点列表
     * @return todo
    */
    public void init(String selfAddress, String masterAddress, String[] addressList) {
        cluster.master(masterAddress);
        //给master分配结点id
        Integer masterNodeId = idRegionService.readNodeId();
        if (null != masterNodeId) {
            cluster.addNode(masterAddress, masterNodeId);
        }

        //询问集群中各结点希望获得的结点id
        int i = 0;
        for (i = 0; i < addressList.length; i++) {
            if (masterAddress.equals(addressList[i])) {
                continue;
            }

            IdRegionClient nodeClient = new IdRegionClient(addressList[i]);
            NodeStateDto state = nodeClient.nodeState(3);
            if (null == state) {
                // 暂不加入集群，等结点自己来注册
                continue;
            }

            cluster.addNode(addressList[i], state.getNodeId());
        }

        if (null == masterNodeId) {
            masterNodeId = cluster.nextNodeId();
            if (null == masterNodeId) {
                log.error("master没有结点id可用");
                return;
            }
            cluster.addNode(masterAddress, masterNodeId);
        }
        isInited = true;

        idRegionService.init(masterNodeId, selfAddress);

        log.info("master初始化完成");
        return;
    }

    /**
     * @title: 非master结点，注册结点
     * @author: SunQian
     * @date: 2021/2/2 17:36
     * @descritpion: todo
     * @param address   结点地址
     * @param nodeId   结点id,集群内存在相同id时，拒绝注册
     * @return 结点id,结点已满时返回null
    */
    public RegisterResultDto nodeRegister(String address, Integer nodeId) {
        RegisterResultDto result = new RegisterResultDto();
        if (!isInited) {
            log.info("master正在初始化，请稍后");
            result.setCode(ERegisterCode.masterIniting.value());
            result.setReason("master正在初始化，请稍后");
            return result;
        }

        synchronized (this) {
            Node node = cluster.find(address);
            if (null != node) {
                //已在结点列表中
                if (null != node.getNodeId()) {
                    //已注册结点重启，返回已注册的结点id
                    result.setCode(ERegisterCode.success.value());
                    result.setNodeId(node.getNodeId());
                    return result;
                }

                //第一次注册
                if (null != nodeId) {
                    if (cluster.contains(nodeId)) {
                        log.warn("拒绝注册-{},存在相同结点id的结点", address);
                        result.setCode(ERegisterCode.existNodeId.value());
                        result.setReason("存在相同结点id的结点");
                        return result;
                    }
                } else {
                    //分配一个id
                    nodeId = cluster.nextNodeId();
                    if (null == nodeId) {
                        log.warn("拒绝注册-{},最多4个结点", address);
                        result.setCode(ERegisterCode.tooMoreNode.value());
                        result.setReason("最多4个结点");
                    }
                }
                node.setNodeId(nodeId);
                result.setCode(ERegisterCode.success.value());
                result.setNodeId(nodeId);
                log.info("结点-{}分配到结点id-{}", node.getAddress(), node.getNodeId());
                return result;
            }

            //第一次注册
            if (null == nodeId) {
                //分配一个id
                nodeId = cluster.nextNodeId();
                if (null == nodeId) {
                    log.warn("拒绝注册-{}结点数量已满", address);
                }
            }
            cluster.addNode(address, nodeId);
            result.setCode(ERegisterCode.success.value());
            result.setNodeId(nodeId);
            return result;
        }
    }
}

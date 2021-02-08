package com.github.huoyu820125.idregion.service;

import com.github.huoyu820125.idregion.client.IdRegionClient;
import com.github.huoyu820125.idregion.constants.ERegisterCode;
import com.github.huoyu820125.idregion.domin.Node;
import com.github.huoyu820125.idregion.domin.NodeStateDto;
import com.github.huoyu820125.idregion.domin.RegisterResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Title 主结点
 * @Athor SunQian
 * @CreateTime 2021/2/2 15:22
 * @Description: todo
 */
@Service
public class Master {
    private final Logger log = LoggerFactory.getLogger(getClass());

    Boolean isInited = false;
    List<Node> cluster = new ArrayList<>();

    @Autowired
    IdRegionService idRegionService;

    /**
     * @title: 给master结点分配结点id
     * @author: SunQian
     * @date: 2021/2/2 17:04
     * @descritpion: todo
     * @param masterAddress master结点地址
     * @param addressList 集群结点列表
     * @return todo
    */
    public void init(String masterAddress, String[] addressList) {
        //给master分配结点id
        Integer masterNodeId = idRegionService.readNodeId();
        if (null != masterNodeId) {
            addNode(masterAddress, masterNodeId);
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

            addNode(addressList[i], state.getNodeId());
        }

        if (null == masterNodeId) {
            masterNodeId = nextNodeId();
            if (null == masterNodeId) {
                log.error("master没有结点id可用");
                return;
            }
            addNode(masterAddress, masterNodeId);
        }
        isInited = true;

        idRegionService.init(masterNodeId);

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
    public RegisterResultDTO nodeRegister(String address, Integer nodeId) {
        RegisterResultDTO result = new RegisterResultDTO();
        if (!isInited) {
            log.info("master正在初始化，请稍后");
            result.setCode(ERegisterCode.masterIniting.value());
            result.setReason("master正在初始化，请稍后");
            return result;
        }

        synchronized (this) {
            Node node = cluster.stream().filter(n -> n.getAddress().equals(address))
                    .findAny().orElse(null);
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
                    Map<Integer, Boolean> idMap = cluster.stream().filter(n -> null != n.getNodeId())
                            .collect(Collectors.toMap(n -> n.getNodeId(), n -> true));
                    if (idMap.containsKey(nodeId)) {
                        log.warn("拒绝注册-{},存在相同结点id的结点", address);
                        result.setCode(ERegisterCode.existNodeId.value());
                        result.setReason("存在相同结点id的结点");
                        return result;
                    }
                } else {
                    //分配一个id
                    nodeId = nextNodeId();
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
                nodeId = nextNodeId();
                if (null == nodeId) {
                    log.warn("拒绝注册-{}结点数量已满", address);
                }
            }
            addNode(address, nodeId);
            result.setCode(ERegisterCode.success.value());
            result.setNodeId(nodeId);
            return result;
        }
    }

    private Boolean addNode(String address, Integer nodeId) {
        if (null != nodeId) {
            Boolean exist = cluster.stream().filter(n -> n.getNodeId().equals(nodeId)).findAny().isPresent();
            if (exist) {
                log.warn("结点id已被使用,结点地址-{}", address);
                return false;
            }
        }

        Node node = new Node();
        node.setNodeId(nodeId);
        node.setAddress(address);
        node.isInited(true);
        cluster.add(node);
        log.info("结点-{}加入集群", node.getAddress());
        if (null != nodeId) {
            log.info("结点-{}分配到结点id-{}", node.getAddress(), node.getNodeId());
        }

        return true;
    }

    /**
     * @title: 下一个空闲结点id
     * @author: SunQian
     * @date: 2021/2/3 14:44
     * @descritpion: todo

     * @return todo
     */
    private Integer nextNodeId() {
        //分配结点id;
        Map<Integer, Boolean> idMap = cluster.stream().filter(n -> null != n.getNodeId())
                .collect(Collectors.toMap(n -> n.getNodeId(), n -> true));
        Integer nodeId = 1;
        for (; nodeId <= 4; nodeId++) {
            if (!idMap.containsKey(nodeId)) {
                return nodeId;
            }
        }

        return null;
    }

}

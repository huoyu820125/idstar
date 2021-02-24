package com.github.huoyu820125.idregion.service;

import com.github.huoyu820125.idregion.domin.Node;
import com.github.huoyu820125.idstar.region.dto.NodeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Title Cluster
 * @Athor SunQian
 * @CreateTime 2021/2/24 9:49
 * @Description: todo
 */
@Component
public class Cluster {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String masterAddress;

    private List<Node> nodes = new ArrayList<>();

    public void master(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public String masterAddress() {
        return this.masterAddress;
    }

    /**
     * @title: 下一个空闲结点id
     * @author: SunQian
     * @date: 2021/2/3 14:44
     * @descritpion: todo

     * @return todo
     */
    public Integer nextNodeId() {
        //分配结点id;
        Map<Integer, Boolean> idMap = nodes.stream().filter(n -> null != n.getNodeId())
                .collect(Collectors.toMap(n -> n.getNodeId(), n -> true));
        Integer nodeId = 1;
        for (; nodeId <= 4; nodeId++) {
            if (!idMap.containsKey(nodeId)) {
                return nodeId;
            }
        }

        return null;
    }

    public Boolean addNode(String address, Integer nodeId) {
        if (null != nodeId) {
            Boolean exist = nodes.stream().filter(n -> nodeId.equals(n.getNodeId())).findAny().isPresent();
            if (exist) {
                log.warn("结点id已被使用,结点地址-{}", address);
                return false;
            }
        }

        Node node = new Node();
        node.setNodeId(nodeId);
        node.setAddress(address);
        node.isInited(true);
        nodes.add(node);
        log.info("结点-{}加入集群", node.getAddress());
        if (null != nodeId) {
            log.info("结点-{}分配到结点id-{}", node.getAddress(), node.getNodeId());
        }

        return true;
    }

    public Node find(String address) {
        Node node = nodes.stream().filter(n -> n.getAddress().equals(address))
                .findAny().orElse(null);

        return node;
    }

    public Boolean contains(Integer nodeId) {
        Map<Integer, Boolean> idMap = nodes.stream().filter(n -> null != n.getNodeId())
                .collect(Collectors.toMap(n -> n.getNodeId(), n -> true));
        return idMap.containsKey(nodeId);
    }

    public List<NodeDto> allNode() {
        List<NodeDto> nodeDTOS = new ArrayList<>();
        for (Node node: nodes) {
            NodeDto nodeDto = new NodeDto();
            BeanUtils.copyProperties(node, nodeDto);
            nodeDTOS.add(nodeDto);
        }

        return nodeDTOS;
    }

}

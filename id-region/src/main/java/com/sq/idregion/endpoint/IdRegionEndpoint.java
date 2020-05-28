package com.sq.idregion.endpoint;

import com.sq.idregion.service.IdRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sq
 * @version 1.0
 * @className IdRegionEndpoint
 * @description TODO
 * @date 2019/4/24 下午4:29
 */
@RestController
public class IdRegionEndpoint {

    @Autowired
    private DiscoveryClient client;

    @Autowired
    IdRegionService idRegionService;

    @RequestMapping(value = "idstar/region/noman", method = RequestMethod.GET)
    public Long idle(@RequestParam("version") Integer version) {
        //检查集群部署正确性
        List<ServiceInstance> svrs = client.getInstances("id-region");

        if (svrs.size() > 4) {
            throw new RuntimeException("发现" + svrs.size() + "个提供者服务节点，最多部署4个节点");
        }

        Map<Integer, Boolean> idMap = new HashMap<>();
        for (ServiceInstance serviceInstance : svrs) {
            int pos = serviceInstance.getInstanceId().lastIndexOf("nodeId-");
            String strId = serviceInstance.getInstanceId().substring(pos + 7);
            Integer id = Integer.valueOf(strId);
            if (idMap.containsKey(id)) {
                throw new RuntimeException("发现提供者服务节点id(" + id + ")重复");
            }
            idMap.put(id, true);
        }

        //提供服务
        return idRegionService.idle(version);
    }
}

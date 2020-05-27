package com.sq.idregion.endpoint;

import com.sq.idregion.service.IdRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        List<ServiceInstance> svrs = client.getInstances("id-page-provider");
        if (svrs.size() > 1) {
            throw new RuntimeException("发现" + svrs.size() + "个提供者服务节点，只能部署1个节点");
        }

        return idRegionService.idle(version);
    }
}

package com.sq.idstar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author sq
 * @version 1.0
 * @className IdPageDefaultProvider
 * @description 默认的id分页提供者
 * @date 2019/4/24 下午4:08
 */
@Service(value = "defaultPageProvider")
public class DefaultPageProvider implements IPageProvider {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Override
    public Long idlePageNo(Integer version) {
        RestTemplate restTemplate = new RestTemplate();
        ServiceInstance svr = loadBalancerClient.choose("id-page");
        String url = String.format("http://%s:%s%s?version=%d", svr.getHost(), svr.getPort(), "/id/page/idle", version);
        return restTemplate.getForObject(url, Long.class);
    }
}

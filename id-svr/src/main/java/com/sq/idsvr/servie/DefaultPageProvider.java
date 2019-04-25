package com.sq.idsvr.servie;

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
    public Long idlePageNo() {
        RestTemplate restTemplate = new RestTemplate();
        ServiceInstance svr = loadBalancerClient.choose("id-page-provider");
        String url = String.format("http://%s:%s", svr.getHost(), svr.getPort()) + "/id/page/idle";
        return restTemplate.getForObject(url, Long.class);
    }
}

package com.sq.idstar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author sq
 * @version 1.0
 * @className DefaultRegionProvider
 * @description 默认的id地区提供者
 * @date 2019/4/24 下午4:08
 */
@Service(value = "defaultRegionProvider")
public class DefaultRegionProvider implements IRegionProvider {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    /**
     * author: SunQian
     * date: 2019/5/23 15:25
     * title: 取一个空闲区号
     * descritpion: TODO
     * @param raceNo 种族编号
     * return: 区号
     */
    @Override
    public Long idleRegionNo(Integer raceNo) {
        RestTemplate restTemplate = new RestTemplate();
        ServiceInstance svr = loadBalancerClient.choose("id-page");
        String url = String.format("http://%s:%s%s?version=%d", svr.getHost(), svr.getPort(), "/id/page/idle", raceNo);
        return restTemplate.getForObject(url, Long.class);
    }
}

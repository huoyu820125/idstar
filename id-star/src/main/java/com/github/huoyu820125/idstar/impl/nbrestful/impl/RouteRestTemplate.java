package com.github.huoyu820125.idstar.impl.nbrestful.impl;

import com.github.huoyu820125.idstar.impl.nbrestful.IRestfulRouter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sq
 * @version 1.0
 */
public class RouteRestTemplate implements InitializingBean {
    public static RouteRestTemplate s_nbRestTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        s_nbRestTemplate = this;
    }

    public static RouteRestTemplate getInstance() {
        return s_nbRestTemplate;
    }

    private Map<String, List<ServiceInstance>> svrsMap = new HashMap<>();
    private Map<String, Timestamp> lastRefreshMap = new HashMap<>();

    public <T> T get(String svrName, Class<T> resultType, String uri, Map<String, Object> uriVariables) {
        return get(null, null, svrName, resultType, uri, uriVariables);
    }

    public <T> T get(IRestfulRouter router, Long rountValue, String svrName, Class<T> resultType, String uri, Map<String, Object> uriVariables) {
        Character character = '/';
        while (character.equals(uri.charAt(0))) {
            uri = uri.substring(1, uri.length());
        }

        ServiceInstance svr = choose(router, rountValue, svrName);
        RestTemplate restTemplate = new RestTemplate();

        if (null == uriVariables || uriVariables.isEmpty()) {
            String url = String.format("http://%s:%s/%s?version={version}", svr.getHost(), svr.getPort(), uri);
            return restTemplate.getForObject(url, resultType);
        }

        String uriParams = uriParams(uriVariables);
        String url = String.format("http://%s:%s/%s?%s", svr.getHost(), svr.getPort(), uri, uriParams);

        return restTemplate.getForObject(url, resultType, uriVariables);
    }

    public <T> T post(String svrName, Class<T> resultType, String uri, Object object, Map<String, Object> uriVariables) {
        return post(null, null, svrName, resultType, uri, object, uriVariables);
    }

    public <T> T post(IRestfulRouter router, Long rountValue, String svrName, Class<T> resultType, String uri, Object object, Map<String, Object> uriVariables) {
        Character character = '/';
        while (character.equals(uri.charAt(0))) {
            uri = uri.substring(1, uri.length());
        }

        ServiceInstance svr = choose(router, rountValue, svrName);
        RestTemplate restTemplate = new RestTemplate();

        if (null == uriVariables || uriVariables.isEmpty()) {
            String url = String.format("http://%s:%s/%s?version={version}", svr.getHost(), svr.getPort(), uri);
            return restTemplate.getForObject(url, resultType);
        }

        String uriParams = uriParams(uriVariables);
        String url = String.format("http://%s:%s/%s?%s", svr.getHost(), svr.getPort(), uri, uriParams);

        return restTemplate.postForObject(url, object, resultType, uriVariables);
    }

    /**
     * uri参数转uri字符串
     * @author: SunQian
     * @param paramMap
     * @return todo
    */
    private String uriParams(Map<String, Object> paramMap) {
        StringBuffer uriParams = new StringBuffer();
        int i = 0;
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (i > 0) {
                uriParams.append("&");
            }
            uriParams.append(String.format("%s={%s}", entry.getKey(), entry.getKey()));
        }

        return uriParams.toString();
    }
    /**
     * 选择结点
     * @author: SunQian
     * @param router    路由器
     * @param rountValue 路由值
     * @param svrName 服务名
     * @return 服务实例
    */
    private ServiceInstance choose(IRestfulRouter router, Long rountValue, String svrName) {
        if (null == router) {
            ServiceInstance svr = loadBalancerClient.choose(svrName);
            if (null == svr) {
                throw new RuntimeException("服务" + svrName + "尚未加入集群");
            }

            return svr;
        }

        List<ServiceInstance> svrList = svrsMap.get(svrName);
        Timestamp lastTime = lastRefreshMap.get(svrName);
        if (null == lastTime
                || 300 * 1000 < lastTime.getTime() - Timestamp.from(Instant.now()).getTime()) {
            //第一次刷新或5分钟刷新1次服务列表
            svrList = discoveryClient.getInstances(svrName);
            if (null == svrList || svrList.isEmpty()) {
                throw new RuntimeException("服务" + svrName + "尚未加入集群");
            }
            svrsMap.put(svrName, svrList);
            lastRefreshMap.put(svrName, Timestamp.from(Instant.now()));
        }
        Integer index = router.choose(rountValue, svrList.size());

        return svrList.get(index);
    }


}

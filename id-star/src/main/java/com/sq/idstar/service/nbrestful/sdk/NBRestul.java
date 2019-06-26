package com.sq.idstar.service.nbrestful.sdk;

import com.sq.idstar.service.nbrestful.util.RestfulBeanHelp;
import com.sq.idstar.service.nbrestful.util.RouteRestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Athor SunQian
 * @CreateTime 2019/6/26 19:16
 * @Description: TODO
 */
public class NBRestul {

    private Map<String, Object> uriVariables = new HashMap<>();

    /**
     * author: SunQian
     * date: 2019/6/26 19:22
     * title: 添加uri参数
     * descritpion: TODO
     * @param name  参数名
     * @param value 参数值
     * return: 链式调用支持
     */
    public NBRestul addUriVariables(String name, Object value) {
        uriVariables.put(name, value);

        return this;
    }


    public <T> T get(String svrName, Class<T> resultType, String uri) {
        return get(null, null, svrName, resultType, uri);
    }

    public <T> T get(IRestfulRouter router, Long rountValue, String svrName, Class<T> resultType, String uri) {
        RouteRestTemplate nbRestTemplate = RestfulBeanHelp.getRestTemplateInstance();
        return nbRestTemplate.get(router, rountValue, svrName, resultType, uri, uriVariables);
    }

    public <T> T post(String svrName, Class<T> resultType, String uri, Object object) {
        return post(null, null, svrName, resultType, uri, object);
    }

    public <T> T post(IRestfulRouter router, Long rountValue, String svrName, Class<T> resultType, String uri, Object object) {
        RouteRestTemplate nbRestTemplate = RestfulBeanHelp.getRestTemplateInstance();
        return nbRestTemplate.post(router, rountValue, svrName, resultType, uri, object, uriVariables);
    }
}

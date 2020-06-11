package com.github.huoyu820125.idstar.impl.nbrestful;

import com.github.huoyu820125.idstar.impl.nbrestful.impl.RouteRestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sq
 * @version 1.0
 */
public class NBRestful {

    private Map<String, Object> uriVariables = new HashMap<>();

    /**
     * 添加uri参数
     * @author: SunQian
     * @param name  参数名
     * @param value 参数值
     * @return: 链式调用支持
    */
    public NBRestful addUriVariables(String name, Object value) {
        uriVariables.put(name, value);

        return this;
    }


    public <T> T get(String svrName, Class<T> resultType, String uri) {
        return get(null, null, svrName, resultType, uri);
    }

    public <T> T get(IRestfulRouter router, Long rountValue, String svrName, Class<T> resultType, String uri) {
        RouteRestTemplate nbRestTemplate = RouteRestTemplate.getInstance();
        return nbRestTemplate.get(router, rountValue, svrName, resultType, uri, uriVariables);
    }

    public <T> T post(String svrName, Class<T> resultType, String uri, Object object) {
        return post(null, null, svrName, resultType, uri, object);
    }

    public <T> T post(IRestfulRouter router, Long rountValue, String svrName, Class<T> resultType, String uri, Object object) {
        RouteRestTemplate nbRestTemplate = RouteRestTemplate.getInstance();
        return nbRestTemplate.post(router, rountValue, svrName, resultType, uri, object, uriVariables);
    }
}

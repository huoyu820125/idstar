package com.sq.idstar.service.nbrestful.sdk;

/**
 * @Athor SunQian
 * @CreateTime 2019/6/26 18:33
 * @Description: 路由器
 */
public interface IRestfulRouter {
    default Integer choose(Long rountValue, Integer count) {
        Long index = rountValue%count;
        return index.intValue();
    }
}

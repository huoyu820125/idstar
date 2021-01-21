package com.github.huoyu820125.example.region.nbrestful;

/**
 * 路由器
 * @author sq
 * @version 1.0
 */
public interface IRestfulRouter {
    default Integer choose(Long rountValue, Integer count) {
        Long index = rountValue%count;
        return index.intValue();
    }
}

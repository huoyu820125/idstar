package com.github.huoyu820125.idstar.self;

import org.apache.commons.lang3.RandomUtils;

/**
 * @Title 整数生长算法
 * @Athor SunQian
 * @CreateTime 2021/2/4 11:22
 * @Description: todo
 */
public class IntegerAlgorithm implements IGrowAlgorithm<Integer> {
    public Integer grow() {
        return RandomUtils.nextInt(0, 10000);
    }
}

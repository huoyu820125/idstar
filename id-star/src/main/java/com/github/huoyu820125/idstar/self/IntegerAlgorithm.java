package com.github.huoyu820125.idstar.self;

import org.apache.commons.lang3.RandomUtils;

/**
 * 整数生长算法
 * @author SunQian
 * @version 1.1
 */
public class IntegerAlgorithm implements IGrowAlgorithm<Integer> {
    /**
     * 生长
     *  随机生长数一个int特征
     * @author: SunQian
     * @return 新长出的特征
     */
    public Integer grow() {
        return RandomUtils.nextInt(0, 10000);
    }
}

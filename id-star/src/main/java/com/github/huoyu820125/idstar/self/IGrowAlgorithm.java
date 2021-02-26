package com.github.huoyu820125.idstar.self;

/**
 * 生长算法，产生特征数据
 * @author SunQian
 * @version 1.1
 */
public interface IGrowAlgorithm<T> {
    /**
     * 生长
     *  实现特征数据生成算法
     * @author: SunQian
     * @return 新长出的特征
    */
    T grow();

    /**
     * 比较2个特征数据是否看起来一样
     * @author: SunQian
     * @param featrue1 特征1
     * @param featrue2 特征2
     * @return todo
    */
    default Boolean isSame(T featrue1, T featrue2) {
        return featrue1.equals(featrue2);
    }
}

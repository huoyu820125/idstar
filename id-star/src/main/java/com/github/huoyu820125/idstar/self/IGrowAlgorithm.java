package com.github.huoyu820125.idstar.self;

/**
 * @Title 生长算法，产生特征数据
 * @Athor SunQian
 * @CreateTime 2021/2/4 11:21
 * @Description: todo
 */
public interface IGrowAlgorithm<T> {
    /**
     * @title: 生长
     * @author: SunQian
     * @date: 2021/2/4 11:21
     * @descritpion: 实现特征数据生成算法
     * @return 新长出的特征
    */
    T grow();

    /**
     * @title: 比较2个特征数据是否看起来一样
     * @author: SunQian
     * @date: 2021/2/4 11:24
     * @descritpion: todo
     * @param featrue1 特征1
     * @param featrue2 特征2
     * @return todo
    */
    default Boolean isSame(T featrue1, T featrue2) {
        return featrue1.equals(featrue2);
    }
}

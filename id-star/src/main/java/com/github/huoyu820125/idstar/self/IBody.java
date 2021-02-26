package com.github.huoyu820125.idstar.self;

/**
 * 身体
 * @author SunQian
 * @version 1.1
 */
public interface IBody<T> {
    /**
     * 被触摸
     * @author: SunQian
     * @return 被触摸到的身体的特征
    */
    T onTouch();
}

package com.github.huoyu820125.idstar;

/**
 * id分区提供者
 * @author sq
 * @version 1.0
 */
public interface IRegionProvider {
    /**
     * 取一个无人区区号
     * 要求确保每次相同的raceNo调用，返回的区号是种族范围内分布式全局唯一的
     * @author: SunQian
     * @param raceNo 种族编号
     * @return 区号
    */
    Long noManRegionNo(Integer raceNo);
}

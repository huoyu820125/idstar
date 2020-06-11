package com.github.huoyu820125.idstar;

/**
 * id分区提供者
 * @author sq
 * @version 1.0
 */
public interface IRegionProvider {
    /**
     * 取一个无人区区号
     * @author: SunQian
     * @param raceNo 种族编号
     * @return 区号
    */
    Long noManRegionNo(Integer raceNo);
}

package com.github.huoyu820125.idstar;

/**
 * @Athor SunQian
 * @CreateTime 2019/5/23 15:21
 * @Description: id分区提供者
 */
public interface IRegionProvider {
    /**
     * author: SunQian
     * date: 2019/5/23 15:25
     * title: 取一个无人区区号
     * descritpion: TODO
     * @param raceNo 种族编号
     * return: 区号
     */
    Long noManRegionNo(Integer raceNo);
}

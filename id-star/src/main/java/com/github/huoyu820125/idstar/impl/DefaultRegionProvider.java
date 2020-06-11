package com.github.huoyu820125.idstar.impl;

import com.github.huoyu820125.idstar.IRegionProvider;
import com.github.huoyu820125.idstar.impl.nbrestful.NBRestful;

/**
 * 默认的id地区提供者
 * @author sq
 * @version 1.0
 */
public class DefaultRegionProvider implements IRegionProvider {

    /**
     * 取一个无人区区号
     * @author: SunQian
     * @param raceNo 种族编号
     * @return 区号
     */
    @Override
    public Long noManRegionNo(Integer raceNo) {
        NBRestful nbRestful = new NBRestful();
        return nbRestful.addUriVariables("version", raceNo)
                .get("id-region", Long.class, "idstar/region/noman");
    }

}

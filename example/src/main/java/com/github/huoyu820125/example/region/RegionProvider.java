package com.github.huoyu820125.example.region;

import com.github.huoyu820125.idstar.IRegionProvider;
import com.github.huoyu820125.example.region.nbrestful.NBRestful;
import com.github.huoyu820125.idstar.IdStarClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 默认的id地区提供者
 * @author sq
 * @version 1.0
 */
@Component
public class RegionProvider implements IRegionProvider, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        IdStarClient.assemble(this, null);
    }

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

package com.github.huoyu820125.example.region;

import com.github.huoyu820125.idstar.http.Http;
import com.github.huoyu820125.idstar.IRegionProvider;
import com.github.huoyu820125.idstar.IdStarClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 默认的id地区提供者
 * @author sq
 * @version 1.0
 */
@Component
public class RegionProvider implements IRegionProvider, InitializingBean {

    @Value("${idRegion.endpoint}")
    private String regionEndpoint;

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
        Http http = new Http();
        return (Long)http.addUriParam("version", raceNo)
                .get(regionEndpoint + "/idstar/region/noman", 0)
                .response(Long.class);
    }
}


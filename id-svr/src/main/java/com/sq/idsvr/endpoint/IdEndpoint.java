package com.sq.idsvr.endpoint;

import com.sq.idstar.service.IdStar;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sq
 * @version 1.0
 * @className IdEndpoint
 * @description id接口
 * @date 2019/4/24 上午11:36
 */
@RestController
public class IdEndpoint {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    IdStar idStar;

    @RequestMapping("/id/next")
    public Long nextId(@RequestParam("raceNo") Integer raceNo){
        return idStar.nextId(raceNo);
    }
}

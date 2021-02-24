package com.github.huoyu820125.example.endpoint;

import com.github.huoyu820125.idstar.IdStarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
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
public class ExampleEndpoint implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${idStar.address}")
    String idStarAddress;

    @Override
    public void afterPropertiesSet() throws Exception {
        IdStarClient.assemble(idStarAddress, null);
    }

    @RequestMapping("/idstar/next")
    public Long nextId(@RequestParam("raceNo") Integer raceNo){
        return IdStarClient.next(raceNo);
    }
}

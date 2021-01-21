package com.github.huoyu820125.example.endpoint;

import com.github.huoyu820125.idstar.IdStar;
import com.github.huoyu820125.idstar.IdStarClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ExampleEndpoint {
    private final Logger logger = Logger.getLogger(getClass());

    @RequestMapping("/idstar/next")
    public Long nextId(@RequestParam("raceNo") Integer raceNo){
        return IdStarClient.next(raceNo);
    }
}

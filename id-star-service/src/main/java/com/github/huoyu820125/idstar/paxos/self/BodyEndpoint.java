package com.github.huoyu820125.idstar.paxos.self;

import com.github.huoyu820125.idstar.paxos.MasterProposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Title PaxosEndpoint
 * @Athor SunQian
 * @CreateTime 2021/2/1 16:08
 * @Description: todo
 */
@RestController
@RequestMapping("/body")
public class BodyEndpoint {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    MasterProposer proposer;

    @RequestMapping(value = "/onTouch", method = RequestMethod.GET)
    public Integer touch() {
        return proposer.lastFeatrue();
    }
}

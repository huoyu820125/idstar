package com.github.huoyu820125.idregion.paxos;

import com.github.huoyu820125.idregion.domin.ProposeDataDTO;
import com.github.huoyu820125.idstar.paxos.Acceptor;
import com.github.huoyu820125.idstar.paxos.ProposeData;
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
@RequestMapping("/idstar/paxos")
public class AcceptorEndpoint {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Acceptor acceptor;

    @Autowired
    MasterProposer proposer;

    @RequestMapping(value = "/who/are/you", method = RequestMethod.GET)
    public Integer whoAreYou() {
        return proposer.lastFeatrue();
    }

    @RequestMapping(value = "/propose", method = RequestMethod.GET)
    public ProposeDataDTO propose(@RequestParam("serialNum") int serialNum) {
        ProposeData<String> data = new ProposeData<String>();
        if (acceptor.propose(serialNum, data)) {
            ProposeDataDTO reply = new ProposeDataDTO();
            reply.setSerialNum(data.serialNum());
            reply.setValue(data.value());
            return reply;
        }

        return null;
    }

    @RequestMapping(value = "/accept", method = RequestMethod.POST)
    public Boolean accept(@RequestBody ProposeDataDTO value) {
        ProposeData<String> data = new ProposeData<String>();
        data.setValue(value.getValue());
        data.setSerialNum(value.getSerialNum());

        return acceptor.accept(data);
    }

}

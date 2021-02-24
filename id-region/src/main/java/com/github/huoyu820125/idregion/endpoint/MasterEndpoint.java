package com.github.huoyu820125.idregion.endpoint;

import com.github.huoyu820125.idregion.domin.RegisterResultDto;
import com.github.huoyu820125.idregion.service.Master;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sq
 * @version 1.0
 * @className IdRegionEndpoint
 * @description TODO
 * @date 2020/02/02 下午4:29
 */
@RestController
@RequestMapping("/idstar/master")
public class MasterEndpoint {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    Master masterService;

    /**
     * @title: 结点注册
     * @author: SunQian
     * @date: 2021/2/2 17:03
     * @descritpion: todo
     * @param address  结点地址
     * @param nodeId   结点id,集群内已存在相同id时，拒绝注册
     * @return 结点在集群内的id，最多4个结点，结点已满时，拒绝注册return null
    */
    @RequestMapping(value = "/node/register", method = RequestMethod.POST)
    public RegisterResultDto nodeRegister(
            @RequestParam(value = "address", required = true) String address,
            @RequestParam(value = "nodeId", required = false) Integer nodeId
    ) {
        return masterService.nodeRegister(address, nodeId);
    }
}

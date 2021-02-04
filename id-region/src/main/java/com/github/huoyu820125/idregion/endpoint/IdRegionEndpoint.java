package com.github.huoyu820125.idregion.endpoint;

import com.github.huoyu820125.idregion.domin.NodeStateDto;
import com.github.huoyu820125.idregion.service.IdRegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author sq
 * @version 1.0
 * @className IdRegionEndpoint
 * @description TODO
 * @date 2019/4/24 下午4:29
 */
@RestController
@RequestMapping("/idstar/region")
public class IdRegionEndpoint {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    IdRegionService idRegionService;

    /**
     * @title: 取结点状态
     * @author: SunQian
     * @date: 2021/2/3 10:14
     * @descritpion: todo
     * @return 结点状态
    */
    @RequestMapping(value = "/node/state", method = RequestMethod.GET)
    public NodeStateDto nodeState() {
        NodeStateDto dto = new NodeStateDto();
        dto.setIsInited(idRegionService.isInited());
        dto.setNodeId(idRegionService.nodeId());
        return dto;
    }

    @RequestMapping(value = "idstar/region/noman", method = RequestMethod.GET)
    public Long idle(@RequestParam("version") Integer version) {
        if (!idRegionService.isInited()) {
            throw new RuntimeException("集群尚未初始化完成");
        }

        //提供服务
        return idRegionService.idle(version);
    }
}

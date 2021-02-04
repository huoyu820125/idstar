package com.github.huoyu820125.idregion.service;

import com.github.huoyu820125.idregion.client.MasterClient;
import com.github.huoyu820125.idregion.constants.ERegisterCode;
import com.github.huoyu820125.idregion.domin.RegisterResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Title 从结点
 * @Athor SunQian
 * @CreateTime 2021/2/3 14:02
 * @Description: todo
 */
@Component
public class Slave {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    IdRegionService idRegionService;

    /**
     * @title: 注册到集群
     * @author: SunQian
     * @date: 2021/2/3 15:22
     * @descritpion: todo
     * @param masterAddress master地址
     * @param selfAddress   自身地址
     * @return todo
    */
    public void nodeRegister(String masterAddress, String selfAddress) {
        Integer nodeId = idRegionService.nodeId();
        while (true) {
            try {
                MasterClient master = new MasterClient(masterAddress);
                RegisterResultDTO resultDTO = master.nodeRegister(selfAddress, nodeId);
                if (ERegisterCode.success.equals(resultDTO.getCode())) {
                    nodeId = resultDTO.getNodeId();
                    break;
                }
                log.warn("注册失败:{}", resultDTO.getReason());
                if (ERegisterCode.tooMoreNode.equals(resultDTO.getCode())) {
                    return;
                }
            } catch (Exception e) {
                log.warn("注册异常", e);
                continue;
            }

            break;
        }

        idRegionService.init(nodeId);
    }
}

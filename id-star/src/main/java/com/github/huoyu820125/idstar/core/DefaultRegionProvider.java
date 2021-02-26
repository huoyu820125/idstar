package com.github.huoyu820125.idstar.core;

import com.github.huoyu820125.idstar.IRegionProvider;
import com.github.huoyu820125.idstar.IdStar;
import com.github.huoyu820125.idstar.service.client.IdStarServiceClient;
import com.github.huoyu820125.idstar.service.client.dto.NodeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * 区号提供者默认实现，从idStar服务获取区号
 * @author SunQian
 * @version 2.0
 */
public class DefaultRegionProvider implements IRegionProvider {
    private static Logger log = LoggerFactory.getLogger(IdStar.class);

    private String svrAddress;
    private List<NodeDto> nodeDtos;
    private Long lastRefreshTime;

    public DefaultRegionProvider(String anyNodeAddress) {
        this.svrAddress = anyNodeAddress;
    }

    @Override
    public Long noManRegionNo(Integer raceNo) {
        IdStarServiceClient client = new IdStarServiceClient(svrAddress);
        if (null == nodeDtos) {
            try {
                nodeDtos = client.allNode();
                if (nodeDtos.isEmpty()) {
                    throw new RuntimeException("has not idStar service instance");
                }
                lastRefreshTime = Timestamp.from(Instant.now()).getTime();
            } catch (Exception e) {
                throw new RuntimeException("service not run or server address is error");
            }
        }

        Long currenttime = Timestamp.from(Instant.now()).getTime();
        if (currenttime - lastRefreshTime > 300 * 1000) {
            for (NodeDto nodeDto: nodeDtos) {
                client = new IdStarServiceClient(nodeDto.getAddress());
                try {
                    nodeDtos = client.allNode();
                    lastRefreshTime = Timestamp.from(Instant.now()).getTime();
                } catch (Exception e) {
                    log.warn("refresh node list is failed, master has no service or current node({} {}) has no service, try next node",
                            nodeDto.getNodeId(), nodeDto.getAddress());
                }
            }
        }

        Long regionNo = null;
        for (NodeDto nodeDto: nodeDtos) {
            client = new IdStarServiceClient(nodeDto.getAddress());
            try {
                regionNo = client.idle(raceNo);
                break;
            } catch (Exception e) {
                log.warn("service node({} {}) is not run or is initing, try next node",
                        nodeDto.getNodeId(), nodeDto.getAddress());
            }
        }
        if (null == regionNo) {
            throw new RuntimeException("idStar cluster is restarting or is shutdown");
        }

        return regionNo;
    }
}

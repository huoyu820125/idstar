package com.github.huoyu820125.idstar;

import com.github.huoyu820125.idstar.region.IdRegionClient;
import com.github.huoyu820125.idstar.region.dto.NodeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * @Title DefaultRegionProvider
 * @Athor SunQian
 * @CreateTime 2021/2/24 11:18
 * @Description: todo
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
        IdRegionClient client = new IdRegionClient(svrAddress);
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
                client = new IdRegionClient(nodeDto.getAddress());
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
            client = new IdRegionClient(nodeDto.getAddress());
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

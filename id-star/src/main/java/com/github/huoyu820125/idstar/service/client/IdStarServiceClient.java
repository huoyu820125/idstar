package com.github.huoyu820125.idstar.service.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.huoyu820125.idstar.http.Http;
import com.github.huoyu820125.idstar.service.client.dto.NodeDto;
import com.github.huoyu820125.idstar.service.client.dto.NodeStateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Title AcceptorClient
 * @Athor SunQian
 * @CreateTime 2021/2/1 16:26
 * @Description: todo
 */
public class IdStarServiceClient {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String address;
    private String endpoint;

    public IdStarServiceClient(String address) {
        this.endpoint = "http://" + address;
    }

    /**
     * @title: 取结点状态
     * @author: SunQian
     * @date: 2021/2/3 10:10
     * @descritpion: todo
     * @return 结点状态
     */
    public NodeStateDto nodeState(Integer tryCount) {
        for (; tryCount > 0; tryCount--) {
            try {
                Http http = new Http();
                String response = http.get(endpoint + "/idstar/node/state", 1000).response();
                NodeStateDto state = JSONObject.parseObject(response).toJavaObject(NodeStateDto.class);
                return state;
            } catch (Exception e) {
                if (tryCount.equals(0)) {
                    log.warn("取结点状态异常:结点地址-{}", address, e);
                }
            }
        }

        return null;
    }

    /**
     * @title: 获取所有结点
     * @author: SunQian
     * @date: 2021/2/23 17:41
     * @descritpion: todo
     * @return todo
     */
    public List<NodeDto> allNode() {
        Http http = new Http();
        String response = http.get(endpoint + "/idstar/node/all", 1000).response();
        List<NodeDto> state = JSONArray.parseArray(response).toJavaList(NodeDto.class);
        return state;
    }

    /**
     * @title: 取无人占用的地区区号
     * @author: SunQian
     * @date: 2021/2/24 11:08
     * @descritpion: todo
     * @param version
     * @return todo
    */
    public Long idle(Integer version) {
        Http http = new Http();
        Long regionNo = (Long)http.addUriParam("version", version)
                .get(endpoint + "/idstar/region/noman", 1000)
                .response(Long.class);
        return regionNo;
    }
}

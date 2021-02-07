package com.github.huoyu820125.idregion.client;

import com.alibaba.fastjson.JSONObject;
import com.github.huoyu820125.idregion.domin.NodeStateDto;
import com.github.huoyu820125.idstar.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title AcceptorClient
 * @Athor SunQian
 * @CreateTime 2021/2/1 16:26
 * @Description: todo
 */
public class IdRegionClient {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String address;
    private String endpoint;

    public IdRegionClient(String address) {
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
                String response = http.get(endpoint + "/idstar/region/node/state", 1000).response();
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

}

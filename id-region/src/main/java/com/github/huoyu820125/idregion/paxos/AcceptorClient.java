package com.github.huoyu820125.idregion.paxos;

import com.alibaba.fastjson.JSONObject;
import com.github.huoyu820125.idregion.domin.ProposeDataDTO;
import com.github.huoyu820125.idstar.http.Http;
import com.github.huoyu820125.idstar.paxos.IAcceptorClient;
import com.github.huoyu820125.idstar.paxos.ProposeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title AcceptorClient
 * @Athor SunQian
 * @CreateTime 2021/2/1 16:26
 * @Description: todo
 */
public class AcceptorClient implements IAcceptorClient<String> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    String endpoint;

    @Override
    public void address(String address) {
        endpoint = "http://" + address;
    }

    @Override
    public ProposeData<String> propose(int serialNum) {
        String response;
        try {
            Http http = new Http();
            response = http.addUriParam("serialNum", serialNum)
                    .get(endpoint + "/propose", 1000).response();
        } catch (Exception e) {
            log.error("请求{}/propose异常", endpoint, e);
            return null;
        }

        if (null == response) {
            return null;
        }

        ProposeDataDTO value = JSONObject.parseObject(response).toJavaObject(ProposeDataDTO.class);
        ProposeData<String> proposeData = new ProposeData<>();
        proposeData.setSerialNum(value.getSerialNum());
        proposeData.setValue(value.getValue());

        return proposeData;
    }

    @Override
    public Boolean accept(ProposeData<String> value) {
        ProposeDataDTO param = new ProposeDataDTO();
        param.setValue(value.value());
        param.setSerialNum(value.serialNum());
        JSONObject body = (JSONObject)JSONObject.toJSON(param);

        Boolean ok = null;
        try {
            Http http = new Http();
            ok = (Boolean)http.setBody(body.toJSONString())
                    .get(endpoint + "/accept", 1000).response(Boolean.class);
        } catch (Exception e) {
            log.error("请求{}/accept异常", endpoint, e);
            return false;
        }

        return ok;
    }
}

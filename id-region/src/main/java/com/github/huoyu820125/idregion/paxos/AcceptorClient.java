package com.github.huoyu820125.idregion.paxos;

import com.alibaba.fastjson.JSONObject;
import com.github.huoyu820125.idregion.domin.ProposeDataDto;
import com.github.huoyu820125.idstar.error.CallException;
import com.github.huoyu820125.idstar.http.Http;
import com.github.huoyu820125.idstar.paxos.IAcceptorClient;
import com.github.huoyu820125.idstar.paxos.ProposeData;
import org.apache.commons.lang3.StringUtils;
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

    String address;
    String endpoint;

    @Override
    public void address(String address) {
        this.address = address;
        this.endpoint = "http://" + this.address;
    }

    @Override
    public ProposeData<String> propose(int serialNum) {
        ProposeDataDto value;
        try {
            Http http = new Http();
            String response = http.addUriParam("serialNum", serialNum)
                    .get(endpoint + "/idstar/paxos/propose", 1000).response();
            if (StringUtils.isEmpty(response)) {
                return null;
            }
            JSONObject json = JSONObject.parseObject(response);
            value = json.toJavaObject(ProposeDataDto.class);
        } catch (CallException e) {
            log.error("拉票请求{}异常：{}", endpoint, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("拉票请求{}异常", endpoint, e);
            return null;
        }

        if (null == value) {
            return null;
        }

        ProposeData<String> proposeData = new ProposeData<>();
        proposeData.setSerialNum(value.getSerialNum());
        proposeData.setValue(value.getValue());

        return proposeData;
    }

    @Override
    public Boolean accept(ProposeData<String> value) {
        ProposeDataDto param = new ProposeDataDto();
        param.setValue(value.value());
        param.setSerialNum(value.serialNum());
        JSONObject body = (JSONObject)JSONObject.toJSON(param);

        Boolean ok = null;
        try {
            Http http = new Http();
            ok = (Boolean)http.setBody(body.toJSONString())
                    .post(endpoint + "/idstar/paxos/accept", 1000).response(Boolean.class);
        } catch (CallException e) {
            log.error("发送提案{}异常：{}", endpoint, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("发送提案{}异常", endpoint, e);
            return false;
        }

        return ok;
    }
}

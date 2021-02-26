package com.github.huoyu820125.idstar.paxos.self;

import com.github.huoyu820125.idstar.self.IBody;
import com.github.huoyu820125.idstar.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title Body
 * @Athor SunQian
 * @CreateTime 2021/2/4 10:57
 * @Description: todo
 */
public class Body implements IBody<Integer> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String address;
    private String endpoint;

    public Body(String address) {
        this.address = address;
        this.endpoint = "http://" + this.address;
    }

    @Override
    public Integer onTouch() {
        Http http = new Http();
        Integer featrue = (Integer)http
                .get(endpoint + "/body/onTouch", 1000)
                .response(Integer.class);
        return featrue;
    }

    public String address() {
        return address;
    }
}

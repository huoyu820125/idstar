package com.github.huoyu820125.idregion.paxos;

import com.github.huoyu820125.idregion.paxos.self.Body;
import com.github.huoyu820125.idstar.self.IBody;
import com.github.huoyu820125.idstar.self.IntegerAlgorithm;
import com.github.huoyu820125.idstar.self.Self;
import com.github.huoyu820125.idregion.service.Master;
import com.github.huoyu820125.idregion.service.Slave;
import com.github.huoyu820125.idstar.paxos.IAcceptorClient;
import com.github.huoyu820125.idstar.paxos.Proposer;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title 选主提议者
 * @Athor SunQian
 * @CreateTime 2021/2/1 16:32
 * @Description: todo
 */
@Component
public class MasterProposer implements InitializingBean, Runnable {
    private final Logger log = LoggerFactory.getLogger(getClass());

    //提议者
    private Proposer<String> proposer;

    //用于识别自身的基础组件
    private Self<Integer> self = new Self<>(new IntegerAlgorithm(), 6);
    //自身地址，通过self识别
    private String selfAddress;

    //结点地址列表
    @Value("${nodelist}")
    protected String nodeList;

    @Autowired
    Master master;

    @Autowired
    Slave slave;

    public Integer lastFeatrue() {
        return self.lastFeatrue();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread init = new Thread(this);
        init.start();
    }

    @Override
    public void run() {
        String[] addressList = StringUtils.split(nodeList, ",");
        if (addressList.length < 3) {
            log.error("集群不能少于3个结点，请修改配置后重启服务");
            return;
        }
        if (addressList.length > 4) {
            log.error("集群最多4个结点，请修改配置后重启服务");
            return;
        }

        //确定自身
        List<IBody<Integer>> bodys = new ArrayList<>();
        int i = 0;
        for (i = 0; i < addressList.length; i++) {
            bodys.add(new Body(addressList[i]));
        }
        if (self.wake(bodys)) {
            selfAddress = ((Body)self.body()).address();
        }

        /**************************************************
         *              开始选主(paxos算法)                *
         *************************************************/
        //初始化决策者
        List<IAcceptorClient> acceptors = new ArrayList<>();
        for (i = 0; i < addressList.length; i++) {
            IAcceptorClient client = new AcceptorClient();
            client.address(addressList[i]);
            acceptors.add(client);
        }

        //优先投自己，找不到自己投第一个结点
        String firstValue = selfAddress;
        if (null == firstValue) {
            firstValue = addressList[0];
        }
        log.info("初始提议:master = {}", firstValue);
        proposer = new Proposer<>(acceptors, firstValue, 30000);
        String masterAddress = proposer.exe();
        log.info("投票最终结果：master = {}", masterAddress);

        if (null == selfAddress) {
            log.error("启动失败：结点列表配置nodelist(集群列表)中没有自身地址，无法注册到集群");
            return;
        }

        if (selfAddress.equals(masterAddress)) {
            //初始化master
            master.init(masterAddress, addressList);
            return;
        }

        //注册结点到集群
        slave.nodeRegister(masterAddress, selfAddress);
        return;
    }
}

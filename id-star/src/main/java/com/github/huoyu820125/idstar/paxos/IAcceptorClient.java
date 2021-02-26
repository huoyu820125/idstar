package com.github.huoyu820125.idstar.paxos;

/**
 * 决策者访问client定义,负责实现与决策者通信的具体过程
 * @author SunQian
 * @version 1.1
 */
public interface IAcceptorClient<T> {
    void address(String address);

    /**
     * 一阶段：发送拉票到acceptor(决策者)
     * @author: SunQian
     * @param serialNum 提案者承诺给与的报酬
     * @return 接受拉票时：返回推荐的值、拒绝时：返回null
     */
    ProposeData<T> propose(int serialNum);

    /**
     * 二阶段：发送提案到acceptor(决策者)
     * @author: SunQian
     * @param value 提案
     * @return true接受、false拒绝
     */
    Boolean accept(ProposeData<T> value);
}

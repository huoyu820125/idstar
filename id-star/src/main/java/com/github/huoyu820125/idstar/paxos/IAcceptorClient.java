package com.github.huoyu820125.idstar.paxos;

/**
 * @Title Acceptor客户端
 * @Athor SunQian
 * @CreateTime 2021/1/29 14:30
 * @Description: 负责实现与决策者通信的具体过程
 */
public interface IAcceptorClient<T> {
    void address(String address);

    //一阶段：发送拉票到acceptor,返回推荐的值
    ProposeData<T> propose(int serialNum);

    //二阶段：发送提议到acceptor,返回是否被接受
    Boolean accept(ProposeData<T> value);
}

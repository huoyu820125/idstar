/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.huoyu820125.idstar.paxos;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Title 决策者
 * @Athor SunQian
 * @CreateTime 2021/2/1 17:33
 * @Description: todo
 */
public class Acceptor {

    public Acceptor() {
        mMaxSerialNum = 0;
        mLastAcceptValue = new ProposeData();
        mLastAcceptValue.setSerialNum(0);
        mLastAcceptValue.setValue(0);
    }

    //同意/拒绝下阶段会接受提议
    //同意时，承诺不再同意编号小于mMaxSerialNum的提议，也不再接受编号小于mMaxSerialNum的提议
    public boolean propose(int serialNum, ProposeData lastAcceptValue) {
        mLock.lock();
        if (0 >= serialNum) {
            mLock.unlock();
            return false;
        }
        if (mMaxSerialNum > serialNum) {
            mLock.unlock();
            return false;
        }
        mMaxSerialNum = serialNum;
        lastAcceptValue.setSerialNum(mLastAcceptValue.serialNum());
        lastAcceptValue.setValue(mLastAcceptValue.value());
        mLock.unlock();

        return true;
    }

    //接受/拒绝提议
    //只接受编号>=mMaxSerialNum的提议，并记录
    public boolean accept(ProposeData value) {
        mLock.lock();
        if (0 >= value.serialNum()) {
            mLock.unlock();
            return false;
        }
        if (mMaxSerialNum > value.serialNum()) {
            mLock.unlock();
            return false;
        }
        mLastAcceptValue.setSerialNum(value.serialNum());
        mLastAcceptValue.setValue(value.value());
        mLock.unlock();
        return true;
    }
    private ProposeData mLastAcceptValue;//最后接受的提议
    private int mMaxSerialNum;//Propose提交的最大流水号
    private Lock mLock = new ReentrantLock();
}

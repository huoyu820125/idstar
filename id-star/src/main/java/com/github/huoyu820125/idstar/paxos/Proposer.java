/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.huoyu820125.idstar.paxos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提议者
 * @author SunQian
 * @version 1.1
 */
public class Proposer<T> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    //死锁次数
    private Integer paxosCount = 0;

    public Proposer(List<IAcceptorClient> acceptors, T value, Integer timeout) {
        this.acceptors = acceptors;
        this.mAcceptorCount = acceptors.size();
        this.mValue = new ProposeData();
        this.mValue.setSerialNum(0);
        this.mValue.setValue(value);
        this.mTimeout = timeout;
    }

    //开始Propose阶段
    private void startPropose() {
        mValue.setSerialNum(mValue.serialNum() + 1);
        mIsAgree = false;
        mMaxAcceptedSerialNum = 0;
        mOkCount = 0;
        mRefuseCount = 0;
        Date curTime = new Date();
        mStart = curTime.getTime();//这就是距离1970年1月1日0点0分0秒的毫秒数
        readyAcceptors.clear();
        if (paxosCount < 1) {
            log.info("拉票 start:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
        }
        else {
            log.info("拉票 restart:第{}次:报酬={}，{}人接受，{}人拒绝", paxosCount, mValue.serialNum(), mOkCount, mRefuseCount);
        }
    }

    /*
     * 阶段超时
     * millSecond：超时判定时间
     */
    private boolean isTimeOut(int millSecond)
    {
        Date curTime = new Date();
        int waitTime = (int)(curTime.getTime() - mStart);//这就是距离1970年1月1日0点0分0秒的毫秒数
        if ( waitTime > millSecond ) return true;

        return false;
    }

    //取得提议
    public ProposeData<T> getProposal() {
        return mValue;
    }

    //提议被投票，Proposed失败则重新开始Propose阶段
    private boolean onProposed(boolean ok, ProposeData<T> lastAcceptValue) {
        if (!ok) {
            mRefuseCount++;
            log.info("拉票 refused:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
            //已有半数拒绝，不需要等待其它acceptor投票了，重新开始Propose阶段
            if (mRefuseCount > mAcceptorCount / 2) {
                log.info("拉票 failed:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
                return false;
            }
            return true;
        }

        mOkCount++;
        log.info("拉票 accepted:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
        /*
		        没有必要检查分支：serialNum为null
		        因为serialNum>m_maxAcceptedSerialNum，与serialNum非0互为必要条件
         */
        //记录所有收到的提议中，编号最大的提议，当自己获得提议权时，提出
        if (lastAcceptValue.serialNum() > mMaxAcceptedSerialNum) {
            mMaxAcceptedSerialNum = lastAcceptValue.serialNum();
            if (null != lastAcceptValue.value()) {
                mValue.setValue(lastAcceptValue.value());
            }
        }
        return true;
    }

    //尝试开始Accept阶段,满足条件成功开始accept阶段返回ture，不满足开始条件返回false
    private boolean tryStartAccept() {
        if (mOkCount > mAcceptorCount / 2) {
            log.info("拉票 success:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
            mOkCount = 0;
            if (paxosCount < 1) {
                log.info("提议 start:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
            }
            else {
                log.info("提议 restart:第{}次:报酬={}，{}人接受，{}人拒绝", paxosCount, mValue.serialNum(), mOkCount, mRefuseCount);
            }
            return true;
        }

        return false;
    }

    //提议被接受，Accepted失败则重新开始Propose阶段
    private boolean onAccepted(boolean ok) {
        if (!ok) {
            mRefuseCount++;
            log.info("提议 refused:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
            //已有半数拒绝，不需要等待其它acceptor投票了，重新开始Propose阶段
            if (mRefuseCount > mAcceptorCount / 2) {
                log.info("提议 failed:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
                return false;
            }
            return true;
        }

        mOkCount++;
        log.info("提议 accepted:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
        if (mOkCount > mAcceptorCount / 2) {
            mIsAgree = true;
            log.info("提议 success:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
        }

        return true;
    }

    //提议被批准
    private boolean isAgree() {
        return mIsAgree;
    }

    /**
     * @title: 执行投票
     * @author: SunQian
     * @date: 2021/2/1 15:28
     * @descritpion: todo
     * @return 最终投票结果
    */
    public T exe() {
        paxosCount = -1;
        ProposeData lastValue = new ProposeData();
        while (true) {
            paxosCount++;
            if (paxosCount > 1) {
                //为了降低活锁，多等一会让别的proposer有机会完成自己的2阶段批准
                Long t = rand(5).longValue();
                log.info("{}秒后重新拉票", t);
                sleep(t * 1000);
            }
            startPropose();
            //循环一阶段
            for (IAcceptorClient acceptor : acceptors) {
                if (isTimeOut(mTimeout)) {
                    //超时重新开始Propose阶段
                    log.info("拉票 timeout:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
                    break;
                }
                /*
                 * 发送消息到第i个acceptor
                 * acceptor处理消息，mAcceptors[i].Propose()
                 * proposer处理回应mProposer.proposed(ok, lastValue)
                 */
                lastValue = acceptor.propose(mValue.serialNum());
                boolean ok = (null != lastValue);
                //处理Propose回应
                if (!this.onProposed(ok, lastValue))
                {
                    //重新开始Propose阶段
                    break;
                }
                if (ok) {
                    readyAcceptors.add(acceptor);//记录愿意投票的acceptor
                }

                if (mOkCount > mAcceptorCount / 2) {
                    //已达成半数，可以不向剩下的决策者拉票
                    if (rand(100) > 30)
                    {
                        //70%的几率立刻开始决策
                        break;
                    }
                }

                //向下一个acceptor拉票
            }

            //尝试开始Accept阶段，如果没有开始表示没有满足条件，要重新开始Propose阶段
            if (!this.tryStartAccept()) {
                continue;
            }
            //发送Accept消息到所有愿意投票的acceptor
            for (IAcceptorClient acceptor : readyAcceptors) {
                if (isTimeOut(mTimeout)) {
                    //超时重新开始阶段一
                    log.info("提议 timeout:报酬={}，{}人接受，{}人拒绝", mValue.serialNum(), mOkCount, mRefuseCount);
                    break;
                }

                //发送accept消息到acceptor
                boolean ok = acceptor.accept(mValue);
                //处理accept回应
                if (!this.onAccepted(ok)) {
                    //重新开始Propose阶段
                    break;
                }
                if (this.isAgree()) {//成功批准了提议
                    return mValue.value();
                }
            }
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
        }
    }

    private Integer rand(int max){
        int r = (int) (Math.random() * max);
        return r;
    }

    private int mTimeout;//拉票阶段和投票阶段的超时时间
    private int mAcceptorCount;//acceptor数量
    private ProposeData<T> mValue;//提议内容
    private boolean mIsAgree;//m_value被批准
    private int mMaxAcceptedSerialNum;//已被接受的提议中流水号最大的
    private long mStart;//阶段开始时间，阶段一，阶段二共用
    private int mOkCount;//投票数量，阶段一，阶段二共用
    private int mRefuseCount;//拒绝数量，阶段一，阶段二共用

    private List<IAcceptorClient> readyAcceptors = new ArrayList<>();//通过一阶段的acceptor结点
    private List<IAcceptorClient> acceptors;//所有acceptor结点
}
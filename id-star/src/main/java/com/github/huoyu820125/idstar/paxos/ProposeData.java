/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.huoyu820125.idstar.paxos;

/**
 * 提案
 * @author SunQian
 * @version 2.0
 */
public class ProposeData<T> {
    public void setSerialNum(int serialNum)
    {
        mSerialNum = serialNum;
    }
    public int serialNum()
    {
        return mSerialNum;
    }
    public void setValue(T value)
    {
        mValue = value;
    }
    public T value()
    {
        return mValue;
    }
    private int	mSerialNum;//流水号(承诺的报酬),1开始递增
    private T	mValue;//提案内容
}
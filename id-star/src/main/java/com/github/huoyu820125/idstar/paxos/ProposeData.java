/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.huoyu820125.idstar.paxos;

/**
 * @Title 提案
 * @Athor SunQian
 * @CreateTime 2021/2/1 15:49
 * @Description: todo
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
    private int	mSerialNum;//流水号,1开始递增，保证全局唯一
    private T	mValue;//提案内容
}
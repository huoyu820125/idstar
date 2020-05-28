package com.sq.idstar.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Title IdConfig
 * @Athor SunQian
 * @CreateTime 2020/5/28 14:10
 * @Description: todo
 */
@Component
public class IdConfig implements InitializingBean {
    @Value("${idStart.idStruct.snLen:16}")
    protected Integer snLen;
    @Value("${idStart.idStruct.raceNoLen:6}")
    protected Integer raceNoLen;
    @Value("${idStart.idStruct.regionNoLen:41}")
    protected Integer regionNoLen;


    /**
     * 最大区号
     */
    protected long maxRegionNo;
    /**
     * 最大种族
     */
    protected int maxRaceNo;
    /**
     * 最大id
     */
    protected int maxId;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (regionNoLen + raceNoLen + snLen > 63) {
            throw new RuntimeException("id取值:最大63bit");
        }

        maxRegionNo = (1L << regionNoLen) - 1;
        maxRaceNo = (1 << raceNoLen) - 1;
        maxId = (1 << snLen) - 1;
    }

    public Integer getSnLen() {
        return snLen;
    }

    public void setSnLen(Integer snLen) {
        this.snLen = snLen;
    }

    public Integer getRaceNoLen() {
        return raceNoLen;
    }

    public void setRaceNoLen(Integer raceNoLen) {
        this.raceNoLen = raceNoLen;
    }

    public Integer getRegionNoLen() {
        return regionNoLen;
    }

    public void setRegionNoLen(Integer regionNoLen) {
        this.regionNoLen = regionNoLen;
    }

    public long getMaxRegionNo() {
        return maxRegionNo;
    }

    public void setMaxRegionNo(long maxRegionNo) {
        maxRegionNo = maxRegionNo;
    }

    public int getMaxRaceNo() {
        return maxRaceNo;
    }

    public void setMaxRaceNo(int maxRaceNo) {
        maxRaceNo = maxRaceNo;
    }

    public int getMaxId() {
        return maxId;
    }

    public static void setMaxId(int maxId) {
        maxId = maxId;
    }
}

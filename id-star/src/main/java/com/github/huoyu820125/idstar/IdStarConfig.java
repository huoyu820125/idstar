package com.github.huoyu820125.idstar;

/**
 * id结构配置：id = 1位留空 + regionNoLen位区号 + raceNoLen位种族编号 + snLen位流水id
 * @author sq
 * @version 1.0
 */
public class IdStarConfig {
    protected Integer snLen;
    protected Integer raceNoLen;
    protected Integer regionNoLen;


    //最大区号
    protected long maxRegionNo;
    //最大种族
    protected int maxRaceNo;
    //最大id
    protected int maxId;

    public IdStarConfig() {
        snLen = 16;
        raceNoLen = 6;
        regionNoLen = 41;
        init();
    }

    public IdStarConfig(int snLen, int raceNoLen, int regionNoLen) {
        this.snLen = snLen;
        this.raceNoLen = raceNoLen;
        this.regionNoLen = regionNoLen;
        init();
    }

    private void init() {
        if (regionNoLen < 10) {
            throw new RuntimeException("地区编号太短:至少10bit");
        }
        if (regionNoLen + raceNoLen + snLen > 63) {
            throw new RuntimeException("id长度最大63bit");
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

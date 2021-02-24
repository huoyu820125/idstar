package com.github.huoyu820125.idstar;

import com.github.huoyu820125.idstar.core.IdStarConfig;
import com.github.huoyu820125.idstar.error.RClassify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * id星球
 *      id = 1位留空 + regionNoLen位区号 + raceNoLen位种族编号 + snLen位流水号
 *      snLen位id资源用完，从redis获取最新可用地区编号
 * @author sq
 * @version 1.0
 */
public class IdStar {
    private static Logger log = LoggerFactory.getLogger(IdStar.class);

    /**
     * 算法配置
    */
    IdStarConfig idStarConfig;

    /**
     * 所有种族当前区号
     * regionNo，占高位
     */
    private static long[] regionNos;

    /**
     * 所有种族上次使用过的流水号
     * 初始化为最大值，触使服务在第一次响应id请求时，更新区号
     */
    private static AtomicInteger lastSns[];

    /**
     * id地区提供者
     * 可通过配置idStart.regionProvider.instance.name更换具体的实现，bean对象名字
     * 默认从redis获取
     */
    IRegionProvider regionProvider;

    protected IdStar(IRegionProvider regionProvider, IdStarConfig idStarConfig) {
        this.regionProvider = regionProvider;
        this.idStarConfig = idStarConfig;

        //初始化为最大值，触使服务在第一次响应id请求时，更新区号
        lastSns = new AtomicInteger[idStarConfig.getMaxRaceNo()];
        int i = 0;
        for (i = 0; i < idStarConfig.getMaxRaceNo(); i++) {
            lastSns[i] = new AtomicInteger(idStarConfig.getMaxId());
        }

        regionNos = new long[idStarConfig.getMaxRaceNo()];
    }

    /**
     * 生成唯一id
     * 使用默认id种族(种族，可以用于区分业务类型，表等)
     * @author: SunQian
     * @return todo
    */
    public Long next(){
        return next(0);
    }

    /**
     * 生成唯一id
     * @author: SunQian
     * @param raceNo id种族从0开始，不能超过idStarConfig.getMaxRaceNo()，
     *               可以用于区分业务类型，表等
     * @return todo
    */
    public Long next(Integer raceNo){
        if (raceNo > idStarConfig.getMaxRaceNo()) {
            throw RClassify.param.exception("种族编号只能是：0~" + String.valueOf(idStarConfig.getMaxRaceNo()));
        }

        //拿下一个流水号
        int curSN = lastSns[raceNo].addAndGet(1);
        if (curSN > idStarConfig.getMaxId()) {
            //当前地区流水号已用完，更新区号，流水号从0开始
            synchronized (this) {
                // 可能其它线程已经更新了区号和上次流水号
                // 所以再拿1次流水号，确保只有1个线程进入更新区号的逻辑
                curSN = lastSns[raceNo].addAndGet(1);
                if (curSN > idStarConfig.getMaxId()) {
                    //更新区号
                    regionNos[raceNo] = regionProvider.noManRegionNo(raceNo);
                    if (idStarConfig.getMaxRegionNo() <= regionNos[raceNo]) {
                        throw RClassify.refused.exception("no resources: arrived last region");
                    }
                    regionNos[raceNo] = (regionNos[raceNo] << (idStarConfig.getRaceNoLen() + idStarConfig.getSnLen()))
                            + (raceNo << idStarConfig.getSnLen());

                    //当前流水号=0
                    curSN = 0;
                    //保存上次使用的流水号
                    lastSns[raceNo].set(0);
                }
            }
        }

        return regionNos[raceNo] + curSN;
    }
}

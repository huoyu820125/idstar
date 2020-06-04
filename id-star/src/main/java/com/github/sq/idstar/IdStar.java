package com.github.sq.idstar;

import com.github.sq.idstar.impl.IdStarConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sq
 * @version 1.0
 * @className IdStar
 * @description id星球
 *      id = 1位留空 + regionNoLen位区号 + raceNoLen位种族编号 + snLen位流水id
 *      snLen位id资源用完，从redis获取最新可用地区编号
 * @date 2019/4/24 上午11:36
 */
//@Service
public class IdStar implements InitializingBean {
    @Autowired
    IdStarConfig idStarConfig;

    /**
     * 所有种族当前区号
     * regionNo，占高位
     */
    private static long[] regionNos;

    /**
     * 所有种族上次使用过的id
     * snLenbit，占低位
     * 最大值maxId
     * 初始化为最大值，触使服务在第一次响应id请求时，更新区号
     */
    private static AtomicInteger lastIds[];

    /**
     * id地区提供者
     * 可通过配置idStart.regionProvider.instance.name更换具体的实现，bean对象名字
     * 默认从redis获取
     */
    IRegionProvider regionProvider;


    @Value("${idStart.regionProvider.instance.name:defaultRegionProvider}")
    String regionProviderClass;

    @Autowired
    ApplicationObjectSupport applicationObjectSupport;

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = applicationObjectSupport.getApplicationContext();
        regionProvider = (IRegionProvider)context.getBean(regionProviderClass);
        //初始化为最大值，触使服务在第一次响应id请求时，更新区号
        lastIds = new AtomicInteger[idStarConfig.getMaxRaceNo()];
        int i = 0;
        for (i = 0; i < idStarConfig.getMaxRaceNo(); i++) {
            lastIds[i] = new AtomicInteger(idStarConfig.getMaxId());
        }
        regionNos = new long[idStarConfig.getMaxRaceNo()];
    }

    /**
     * 下一个id
     *
     * @param
     * @return java.lang.Long
     * @author sq
     * @date 2019/4/24 下午12:06
     */
    public Long nextId(){
        return nextId(0);
    }

    /**
     * 下一个id
     *
     * @param
     * @return java.lang.Long
     * @author sq
     * @date 2019/4/24 下午12:06
     */
    public Long nextId(Integer raceNo){
        if (raceNo > idStarConfig.getMaxRaceNo()) {
            throw new RuntimeException("种族编号只能是：0~" + String.valueOf(idStarConfig.getMaxRaceNo()));
        }

        int curId = lastIds[raceNo].addAndGet(1);
        while (idStarConfig.getMaxId() < curId) {
            if (moveToNMR(curId, raceNo)) {
                curId = 0;
                break;
            }
            curId = lastIds[raceNo].addAndGet(1);
        }

        return regionNos[raceNo] + curId;
    }

    /**
     * 进入无人区
     *
     * @param
     * @return boolean 多线程并发时，只有一个线程的调用会进入无人区，其它线程的全部返回false
     * @author sq
     * @date 2019/4/24 下午3:52
     */
    private boolean moveToNMR(int curId, int raceNo) {
        synchronized (IdStar.class) {
            if (idStarConfig.getMaxId() >= curId) {
                return false;
            }
            //更新区号
            regionNos[raceNo] = regionProvider.noManRegionNo(raceNo);
            if (idStarConfig.getMaxRegionNo() <= regionNos[raceNo]) {
                throw new RuntimeException("no resources: arrived last region");
            }
            regionNos[raceNo] = (regionNos[raceNo] << (idStarConfig.getRaceNoLen() + idStarConfig.getSnLen()))
                    + (raceNo << idStarConfig.getSnLen());

            //重置id
            lastIds[raceNo].set(0);
        }

        return true;
    }
}

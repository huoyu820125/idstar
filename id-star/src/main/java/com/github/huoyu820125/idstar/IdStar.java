package com.github.huoyu820125.idstar;

import com.github.huoyu820125.idstar.impl.IdStarConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * id星球
 *      id = 1位留空 + regionNoLen位区号 + raceNoLen位种族编号 + snLen位流水id
 *      snLen位id资源用完，从redis获取最新可用地区编号
 * @author sq
 * @version 1.0
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
     * 生成唯一id
     * 使用默认id种族(种族，可以用于区分业务类型，表等)
     * @author: SunQian
     * @return todo
    */
    public Long nextId(){
        return nextId(0);
    }

    /**
     * 生成唯一id
     * @author: SunQian
     * @param raceNo id种族，可以用于区分业务类型，表等
     * @return todo
    */
    public Long nextId(Integer raceNo){
        if (raceNo > idStarConfig.getMaxRaceNo()) {
            throw new RuntimeException("种族编号只能是：0~" + String.valueOf(idStarConfig.getMaxRaceNo()));
        }

        int curId = lastIds[raceNo].addAndGet(1);
        while (idStarConfig.getMaxId() < curId) {
            if (moveToNMR(raceNo)) {
                curId = 0;
                break;
            }
            curId = lastIds[raceNo].addAndGet(1);
        }

        return regionNos[raceNo] + curId;
    }

    /**
     * 进入无人区
     * @author: SunQian
     * @param raceNo 进入哪个种族的无人区
     * @return boolean 多线程并发时，只有一个线程的调用会进入无人区，其它线程的全部返回false
    */
    private boolean moveToNMR(int raceNo) {
        synchronized (IdStar.class) {
            //确保只有1个线程进入
            int curId = lastIds[raceNo].get();
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

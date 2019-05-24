package com.sq.idstar.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

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
@Service
public class IdStar implements InitializingBean {
    @Value("${idStart.idStruct.snLen:16}")
    Integer snLen;
    @Value("${idStart.idStruct.raceNoLen:6}")
    Integer raceNoLen;
    @Value("${idStart.idStruct.regionNoLen:41}")
    Integer regionNoLen;


    /**
     * 区号
     * regionNo，占高位
     */
    private static long regionNo;

    /**
     * 最大区号
     */
    private static long maxRegionNo;
    /**
     * 最大种族
     */
    private static long maxRaceNo;
    /**
     * 最大id
     */
    private static int maxId;

    /**
     * 上次使用过的id
     * snLenbit，占低位
     * 最大值maxId
     * 初始化为最大值，触使服务在第一次响应id请求时，更新页码
     */
    private static AtomicInteger lastId = new AtomicInteger(maxId);

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
        if (regionNoLen <= 0 || regionNoLen > 41) {
            throw new RuntimeException("地区编号取值:1~41bit");
        }
        if (snLen <= 0 || snLen > 27) {
            throw new RuntimeException("流水id取值:1~27bit");
        }
        if (raceNoLen < 0 || raceNoLen > 8) {
            throw new RuntimeException("种族编号取值:0~8bit");
        }

        maxRegionNo = (1L << regionNoLen) - 1;
        maxRaceNo = (1L << raceNoLen) - 1;
        maxId = (1 << snLen) - 1;
        //初始化为最大值，触使服务在第一次响应id请求时，更新页码
        lastId.set(maxId);
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
        if (raceNo > maxRaceNo) {
            throw new RuntimeException("种族编号只能是：0~" + String.valueOf(maxRaceNo));
        }

        int curId = lastId.addAndGet(1);
        while (maxId < curId) {
            if (moveToNMR(curId, raceNo)) {
                curId = 0;
                break;
            }
            curId = lastId.addAndGet(1);
        }

        return regionNo + curId;
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
            if (maxId >= curId) {
                return false;
            }
            //更新页码
            regionNo = regionProvider.noManRegionNo(raceNo);
            if (maxRegionNo <= regionNo) {
                throw new RuntimeException("no resources: arrived last region");
            }
            regionNo = (regionNo << (raceNoLen + snLen))
                    + (raceNo << snLen);

            //重置id
            lastId.set(0);
        }

        return true;
    }
}

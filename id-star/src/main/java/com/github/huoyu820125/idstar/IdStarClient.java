package com.github.huoyu820125.idstar;

import com.github.huoyu820125.idstar.core.DefaultRegionProvider;
import com.github.huoyu820125.idstar.core.IdStarConfig;
import com.github.huoyu820125.idstar.error.RClassify;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * idStar客户端
 * @author SunQian
 * @version 2.0
 */
public class IdStarClient {
    private static Logger log = LoggerFactory.getLogger(IdStarClient.class);

    private static IdStarConfig idStarConfig;
    private static IRegionProvider regionProvider;
    private static IdStar idStar;

    /**
     * 装配idStar客户端单例对象
     * @author: SunQian
     * @param regionProvider 区域生产者(必填)
     * @param idStarConfig  星球配置(非必填)
     * @return todo
    */
    public static void assemble(IRegionProvider regionProvider, IdStarConfig idStarConfig) {
        if (null == regionProvider) {
            throw RClassify.param.exception("段号提供者是必须的");
        }
        if (null == idStarConfig) {
            idStarConfig = new IdStarConfig();
        }

        IdStarClient.idStarConfig = idStarConfig;
        IdStarClient.regionProvider = regionProvider;
    }

    /**
     * 装配idStar客户端单例对象：使用idStar服务做区号提供者
     * @author: SunQian
     * @param anyNodeAddress    idStar服务任意结点地址(必填)
     * @param idStarConfig      星球配置(非必填)
     * @return todo
    */
    public static void assemble(String anyNodeAddress, IdStarConfig idStarConfig) {
        if (StringUtils.isEmpty(anyNodeAddress)) {
            throw RClassify.param.exception("idStar服务任意结点地址是必须的");
        }
        if (null == idStarConfig) {
            idStarConfig = new IdStarConfig();
        }

        IdStarClient.idStarConfig = idStarConfig;
        IdStarClient.regionProvider = new DefaultRegionProvider(anyNodeAddress);
    }

    /**
     * 单例对象
     * @author: SunQian
     * @return todo
    */
    private static IdStar instance() {
        if (null != idStar) {
            return idStar;
        }

        if (null == idStarConfig) {
            log.error("idStar配置未装配，请在程序启动时候调用1次static void assemble(IRegionProvider regionProvider, IdStarConfig idStarConfig)");
            throw RClassify.refused.exception("idStar配置未装配，请在程序启动时候调用1次static void assemble(IRegionProvider regionProvider, IdStarConfig idStarConfig)");
        }
        if (null == regionProvider) {
            log.error("段号提供者未装配，请在程序启动时候调用1次static void assemble(IdStarConfig idStarConfig, IRegionProvider regionProvider)");
            throw RClassify.refused.exception("段号提供者未装配，请在程序启动时候调用1次static void assemble(IdStarConfig idStarConfig, IRegionProvider regionProvider)");
        }

        synchronized (IdStar.class) {
            if (null == idStar) {
                idStar = new IdStar(regionProvider, idStarConfig);
            }
        }

        return idStar;
    }

    /**
     * 产生一个在某个种族内唯一的id
     * @author: SunQian
     * @param raceNo id种族从0开始，不能超过idStarConfig.getMaxRaceNo()，
     *               可以用于区分业务类型，表等
     * @return todo
    */
    public static Long next(Integer raceNo) {
        return instance().next(raceNo);
    }
}

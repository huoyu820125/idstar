package com.sq.idstar;

import com.sq.idstar.impl.DefaultRegionProvider;
import com.sq.idstar.impl.IdStarConfig;
import com.sq.idstar.impl.nbrestful.impl.RouteRestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @Title IdStarBeans
 * @Athor SunQian
 * @CreateTime 2020/5/28 18:52
 * @Description: 从IdStarBeans派生一个类，添加注解@Configuration，不用添加任何代码
 */
public class IdStarBeans {
    @Bean
    @ConditionalOnProperty(prefix = "idStar", name = "open", havingValue = "true", matchIfMissing = true)
    public IdStarConfig IdConfig() {
        return new IdStarConfig();
    }

//    @Bean(name = "defaultRegionProvider")//bean名为defaultRegionProvider
//    @Bean(value = "defaultRegionProvider")//bean名为defaultRegionProvider
//    @Bean("defaultRegionProvider")//bean名为defaultRegionProvider
    @Bean//bean名默认与方法名一致，区分大小写
    @ConditionalOnProperty(prefix = "idStar", name = "open", havingValue = "true", matchIfMissing = true)
    public DefaultRegionProvider defaultRegionProvider() {
        return new DefaultRegionProvider();
    }

    @Bean()
    @ConditionalOnProperty(prefix = "idStar", name = "open", havingValue = "true", matchIfMissing = true)
    public IdStar IdStar() {
        return new IdStar();
    }

    @Bean("idStarRestTemplate")
    @ConditionalOnProperty(prefix = "idStar", name = "open", havingValue = "true", matchIfMissing = true)
    public RouteRestTemplate idStarRestTemplate() {
        return new RouteRestTemplate();
    }

}

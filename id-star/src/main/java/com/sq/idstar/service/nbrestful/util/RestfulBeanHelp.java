package com.sq.idstar.service.nbrestful.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Athor SunQian
 * @CreateTime 2019/6/26 19:24
 * @Description: TODO
 */
@Component
public class RestfulBeanHelp implements InitializingBean {
    @Autowired
    RouteRestTemplate nbRestTemplate;

    public static RouteRestTemplate s_nbRestTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        s_nbRestTemplate = nbRestTemplate;
    }

    public static RouteRestTemplate getRestTemplateInstance() {
        return s_nbRestTemplate;
    }
}

package com.sq.idsvr.servie;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

/**
 * @author sq
 * @version 1.0
 * @className IdService
 * @description id服务
 *      id = 1位留空 + 36位页码 + 27位id
 *      27位id资源用完，从page-svr获取最新可用页的页码
 * @date 2019/4/24 上午11:36
 */
@Service
public class IdService implements InitializingBean {

    /**
     * 页码
     * 36bit，占高位
     * 初始值从0x0000000008000000L开始
     */
    private static long pageNo = 0x0000000008000000L;

    /**
     * 最大id
     */
    private static int maxId = 0x7ffffff;

    /**
     * id
     * 27bit，占低位
     * 最大值maxId
     * 初始化为最大值，触使服务在第一次响应id请求时，更新页码
     */
    private static int curId = maxId;

    /**
     * id分页提供者
     * 可通过配置page.provider.instance.name更换具体的实现，bean对象名字
     * 默认从id-page-provider服务获取
     */
    IPageProvider pageProvider;

    @Value("${page.provider.instance.name:defaultPageProvider}")
    String pageProviderClass;

    @Autowired
    ApplicationObjectSupport applicationObjectSupport;

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = applicationObjectSupport.getApplicationContext();
        pageProvider = (IPageProvider)context.getBean(pageProviderClass);
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
        curId++;
        while (maxId < curId) {
            if (nextPage()) {
                break;
            }
            curId++;
        }

        return pageNo + curId;
    }

    /**
     * 进入下一页
     *
     * @param
     * @return boolean 多线程并发时，只有一个线程的调用会进入下一页，其它线程的全部返回false
     * @author sq
     * @date 2019/4/24 下午3:52
     */
    private boolean nextPage() {
        synchronized (IdService.class) {
            if (maxId >= curId) {
                return false;
            }
            //更新页码
            pageNo = pageProvider.idlePageNo();
            pageNo = pageNo << 27;
            //重置id
            curId = 0;
        }

        return true;
    }
}

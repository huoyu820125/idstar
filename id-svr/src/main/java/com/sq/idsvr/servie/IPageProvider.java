package com.sq.idsvr.servie;

/**
 * @author sq
 * @version 1.0
 * @className IPageProvider
 * @description id分页提供者
 * @date 2019/4/24 下午3:58
 */
public interface IPageProvider {
    /**
     * 取一个空闲页的页码
     *
     * @param
     * @return java.lang.Long 页码
     * @author sq
     * @date 2019/4/24 下午3:58
     */
    Long idlePageNo(Integer version);
}

package com.lgh.pull2.service;

/**
 * Created by Administrator on 2016/6/10.
 */
public interface CommonConfigService {
    /**
     * 静态资源域名地址
     *
     * @return
     */
    String getResourcesUri();

    /**
     * 上传资源的服务地址
     *
     * @return
     */
    String getResourcesHome();

}

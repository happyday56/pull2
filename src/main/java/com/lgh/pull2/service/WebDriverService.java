/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.lgh.pull2.service;

import org.openqa.selenium.WebDriver;

/**
 * 自动获取driver的服务
 */
public interface WebDriverService {


    /**
     * @return 生成一个新的Driver, 客户端代码必须负责销毁
     */
    WebDriver webDriverGenerator();
}
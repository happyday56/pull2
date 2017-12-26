/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 *
 */

package com.lgh.pull.service.impl;

import com.lgh.pull.service.WebDriverService;
import com.lgh.pull.utils.DriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by lhx on 2017/2/22.
 */
@Service
public class WebDriverServiceImpl implements WebDriverService {

    static {
        try {
            DriverSetup.setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Environment environment;

    @Autowired
    public WebDriverServiceImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public WebDriver webDriverGenerator() {
        // 如果环境设定只使用谷歌 那也是可以的
        if (environment.getProperty("huotao_chrome", Boolean.class, false)) {
            return new ChromeDriver();
        }
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
        return new FirefoxDriver(profile);
    }
}
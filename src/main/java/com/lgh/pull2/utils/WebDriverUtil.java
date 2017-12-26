/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 *
 */

package com.lgh.pull2.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Created by xyr on 2017/4/17.
 */
public class WebDriverUtil {
    public static void waitFor(WebDriver driver, java.util.function.Predicate<WebDriver> predicate, int seconds) {
        new WebDriverWait(driver, seconds)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(@Nullable WebDriver driver) {
                        if (driver == null)
                            return false;
                        return predicate.test(driver);
                    }
                });
//        new WebDriverWait(driver, seconds)
//                .until(new Predicate<WebDriver>() {
//                    @Override
//                    public boolean apply(@Nullable WebDriver driver) {
//                        if (driver == null)
//                            return false;
//                        return predicate.test(driver);
//                    }
//                });
    }

    public static void close(WebDriver driver) {
        try{
            driver.close();
        }catch (Throwable ignored){
        }
        try {
            driver.quit();
        } catch (Throwable ignored) {
        }
    }
}

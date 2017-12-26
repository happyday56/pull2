package com.lgh.pull2.service.impl;

import com.lgh.pull.service.UtilsService;
import com.lgh.pull.service.WebDriverService;
import com.lgh.pull.service.WebService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 网站服务service
 */
@Service
public class WebServiceImpl implements WebService {

    @Autowired
    private WebDriverService webDriverService;

    @Autowired
    private UtilsService utilsService;

    private static Log log = LogFactory.getLog(WebServiceImpl.class);

    /**
     * 单个视频保存为html文件
     *
     * @param id
     */
    public void generateMovieHtml(Integer id) {
        WebDriver driver = null;
        try {
            driver = webDriverService.webDriverGenerator();
            utilsService.getWebDriver(driver);
            editOneOper(id, driver);
            driver.quit();
        } catch (Exception ex) {
            if (driver != null) driver.quit();
        }
    }

    /**
     * 批量视频保存为html
     *
     * @param listUpdated
     */
    public void batchGenerateMovieHtml(List<Integer> listUpdated) {
        if (listUpdated.size() > 0) {
            WebDriver driver = null;
            try {
                driver = webDriverService.webDriverGenerator();
                utilsService.getWebDriver(driver);
                for (Integer id : listUpdated) {
                    editOneOper(id, driver);
                }
                driver.quit();
            } catch (Exception ex) {
                if (driver != null) driver.quit();
            }
        }
    }

    public void editOneOper(Integer id, WebDriver driver) {
        //3.添加操作
        String addUrl = "http://www.meijuhui.net/top100/article_edit.php?aid=" + id;
        driver.get(addUrl);
        driver.manage().window().maximize();

        //切换到源码 内容过多无法设置数据 否则火狐下内容没法保存
//        webDriver.findElement(By.id("cke_8_label")).click();

        //切回主文档
//        webDriver.switchTo().defaultContent();

        driver.findElement(By.name("imageField")).click();
        log.info(id + " html do");
    }
}

package com.lgh.pull2.service.impl;

import com.lgh.pull.entity.*;
import com.lgh.pull.exception.UpdatedFailedException;
import com.lgh.pull.model.*;
import com.lgh.pull.repository.*;
import com.lgh.pull.service.*;
import com.lgh.pull.utils.FileUtil;
import com.lgh.pull.utils.LocalDateTimeUtils;
import com.lgh.pull.utils.SitemapHelper;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hot on 2017/8/28.
 */
@Service
public class ArchivesServiceImpl implements ArchivesService {

    private static Log log = LogFactory.getLog(ArchivesServiceImpl.class);
    @Autowired
    private PullService pullService;

    @Autowired
    private PushService pushService;

    @Autowired
    private ArchivesRepository archivesRepository;

    @Autowired
    private AddOnArticleRepository addOnArticleRepository;

    @Autowired
    private ArctinyRepository arctinyRepository;

    @Autowired
    private WebDriverService webDriverService;

    @Autowired
    private UtilsService utilsService;

    @Autowired
    private ZmzService zmzService;

    @Autowired
    private TianTianService tianTianService;

    @Autowired
    private DouBanService douBanService;

    @Autowired
    private Environment env;

    @Autowired
    private WebService webService;

    @Autowired
    private SysConfigRepository sysConfigRepository;


    @Autowired
    private DownloadArticleRepository downloadArticleRepository;

    /**
     * 从字幕组抓取数据 （新增）
     *
     * @param zmzId
     * @param ttId
     * @param typeId
     * @param cookies
     */
    @Override
    public void addNewFromZMZ(Integer zmzId, Integer ttId, Integer typeId
            , String cookies, Integer dbId, Integer season) throws UpdatedFailedException {
        boolean updated = false;

        String theSeason = season > 0 ? "第" + season.toString() + "季" : "";
//电影从登录后拉去
        if (typeId == 44) cookies = env.getProperty("cookies");
        SourceArticleFive sourceArticleFour = zmzService.getZMZData(zmzId, cookies, season);

        Integer id = archivesRepository.findMaxId() + 1;
        if (sourceArticleFour != null) {
            try {
                Archives archives = new Archives();
                archives.setId(id);
                archives.setTypeid(typeId);
                archives.setTitle(sourceArticleFour.getTitle().replace("&#39;", "'"));
                archives.setKeywords(sourceArticleFour.getKeywords());
                archives.setUpdateTime(new Date().getTime() / 1000);
                archives.setSenddate(new Date().getTime() / 1000);
                archives.setVoteId(0);

                archives.setLitPic("");

                archives = archivesRepository.save(archives);

                AddOnArticle addOnArticle = new AddOnArticle();
                addOnArticle.setAid(archives.getId());
                addOnArticle.setTypeid(typeId);
                addOnArticle.setPingfen(sourceArticleFour.getPingfen());
                addOnArticle.setFeedback(0);

                addOnArticle.setEnglishName(sourceArticleFour.getEnglishName().replace("&#39;", "'"));
                addOnArticle.setAreaName(sourceArticleFour.getAreaName());
                addOnArticle.setLanguage(sourceArticleFour.getLanguage());
                addOnArticle.setBeginTime(sourceArticleFour.getBeginTime());
                addOnArticle.setCorp(sourceArticleFour.getCorp());
                addOnArticle.setType(sourceArticleFour.getType());
                addOnArticle.setImdb(sourceArticleFour.getImdb());
                addOnArticle.setAlias(sourceArticleFour.getAlias());
                addOnArticle.setAuthor(sourceArticleFour.getAuthor());
                addOnArticle.setDirector(sourceArticleFour.getDirector());
                addOnArticle.setZhuYan(sourceArticleFour.getZhuYan());
                addOnArticle.setMovieName(sourceArticleFour.getTitle().replace(sourceArticleFour.getEnglishName(), "").replace("迅雷下载", "").replace("/全集", ""));
                addOnArticle.setSummary(sourceArticleFour.getSummary().replace("&#39;", "'"));
                addOnArticle.setPictureUrl("<img alt=\"" + addOnArticle.getMovieName() + "\" src=\"" + sourceArticleFour.getPictureUrl() + "\" />");
                addOnArticle.setFromZMZId(zmzId);
                if (ttId > 0) {
                    //从天天抓
                    addOnArticle.setFromTTId(ttId);
                    String downUrls = getDownUrlsFromTT(ttId);
                    if (!StringUtils.isEmpty(downUrls)) addOnArticle.setDownUrls(downUrls);

                } else {
                    addOnArticle.setDownUrls(sourceArticleFour.getDownUrls());
                }
                addOnArticle.setRecommend("");
                addOnArticle.setBody("");//todo
                addOnArticle.setStaus(addOnArticle.getDownUrls().indexOf("</a>") >= 0 ? "连载中" : "未开播");
                if (addOnArticle.getImdb() != null)
                    addOnArticle.setImdb(addOnArticle.getImdb().replace("http://www.imdb.com/title/", ""));
                addOnArticle.setCurCollection(pushService.getCurrentCollection(addOnArticle.getDownUrls()));
                addOnArticle.setSeason(utilsService.getSeason(addOnArticle.getMovieName()));


                initArticleFromDouban(dbId, season, addOnArticle);

                addOnArticleRepository.save(addOnArticle);

//                String keyWords = addOnArticle.getType().replace("/", ",");
//                keyWords += "," + addOnArticle.getCorp();
//                if (!StringUtils.isEmpty(addOnArticle.getBeginTime()) && addOnArticle.getBeginTime().length() >= 4) {
//                    keyWords += "," + addOnArticle.getBeginTime().substring(0, 4);
//                }
//                archives.setKeywords(keyWords);
//                archivesRepository.save(archives);

                Arctiny arctiny = new Arctiny();
                arctiny.setId(archives.getId());
                arctiny.setTypeId(typeId);
                arctiny.setTypeid2("0");
                arctiny.setArcrank(-2);
                arctiny.setChannel(1);
                arctiny.setSendDate(1497521190);
                arctiny.setSortRank(1497521178);
                arctiny.setMid(1);
                arctinyRepository.save(arctiny);

                updated = true;
            } catch (Exception ex) {
                log.error("insert data error");
            }

        } else {
            log.error("zmz pull error");
        }

        if (!updated) {
            throw new UpdatedFailedException();
        }

    }

    /**
     * @param dbId
     * @param season       如 第一季 第二季
     * @param addOnArticle
     */
    private void initArticleFromDouban(Integer dbId, Integer season, AddOnArticle addOnArticle) {
        if (dbId > 0) {
            DouBanModel douBanModel = douBanService.get(dbId);
            if (douBanModel != null) {
                if (douBanModel.getSeason() > 0) {
                    addOnArticle.setSeason(douBanModel.getSeason());
                }
                if (douBanModel.getMaxCollection() > 0) {
                    addOnArticle.setMaxCollection(douBanModel.getMaxCollection());
                }
                //第一季用字幕组的
                if (StringUtils.isEmpty(addOnArticle.getBeginTime()) || season != 1) {
                    addOnArticle.setBeginTime(douBanModel.getBeginTime());
                }
                if (!StringUtils.isEmpty(douBanModel.getImdb())) {
                    addOnArticle.setImdb(douBanModel.getImdb());
                }
                if (!StringUtils.isEmpty(douBanModel.getPictureUrl())) {
                    addOnArticle.setPictureUrl("<img alt=\"" + addOnArticle.getMovieName() + "\" src=\"" + douBanModel.getPictureUrl() + "\" />");
                }
//                if (!StringUtils.isEmpty(douBanModel.getSummary()))
//                    addOnArticle.setSummary(douBanModel.getSummary());
            } else {
                log.error("initArticleFromDouban error");
            }
            addOnArticle.setFromDBId(dbId);
        }
    }

    private String getDownUrlsFromTT(Integer ttId) {
        String result = "";
//        WebDriver driver = webDriverService.webDriverGenerator();
        try {

//            String loginUrl = "http://www.msj1.com/archives/" + ttId + ".html";
//            driver.get(loginUrl);
//            String html = driver.getPageSource().toString();

            URL url = new URL("http://www.msj1.com/archives/" + ttId + ".html");
            Source source = new Source(url);
            Element element = source.getElementById("content");
            if (element != null) {
                String content = element.getContent().toString();
                content = content.substring(content.indexOf("<h2 id=\"download\""), content.indexOf("外挂字幕下载"));
                result = utilsService.clearFormat(content);

//                if (content.indexOf("<h2>") > 0 && content.indexOf("<p>") >= 0) {

//                    if (content.indexOf("在线观看") >= 0) {
//                        return content.substring(content.indexOf("<h2 id=\"download\">"), content.indexOf("外挂字幕下载"));
//                    } else {
//                        return content.substring(content.indexOf("<h2"), content.indexOf("外挂字幕下载"));
//                    }
//                }
            }
//            driver.quit();
        } catch (Exception ex) {
            log.error(ttId + " getDownUrlsFromTT error");
//            if (driver != null) driver.quit();
        }
        return result;
    }

    /**
     * 只更新downurls
     *
     * @param type 1 tt 0 zmz
     * @param id   要修改的文章id
     */
    @Override
    @Transactional
    public void updateDownUrls(Integer type, Integer id) throws UpdatedFailedException {
        boolean updated = false;
        AddOnArticle addOnArticle = addOnArticleRepository.findOne(id);
        if (addOnArticle != null) {
            if (type == 1 && addOnArticle.getFromTTId() != null && addOnArticle.getFromTTId() > 0) {
                String downUrls = getDownUrlsFromTT(addOnArticle.getFromTTId());
                if (!StringUtils.isEmpty(downUrls)) {
                    boolean allowUpdated = false;
                    allowUpdated = isAllowUpdated(id, downUrls, allowUpdated);
                    if (allowUpdated) {
                        SaveDownUrls(id, downUrls);
                        updated = true;
                    }
                }
            } else if (type == 0 && addOnArticle.getFromZMZId() != null && addOnArticle.getFromZMZId() > 0) {
                DownloadUrlModel downloadUrlModel = zmzService.getOneMovieDownloadUrlSmart(addOnArticle.getFromZMZId(), addOnArticle.getSeason());
                if (downloadUrlModel != null) {
                    boolean allowUpdated = false;
                    allowUpdated = isAllowUpdated(id, downloadUrlModel.getSourceHtml(), allowUpdated);
                    if (allowUpdated) {
                        SaveDownUrls(id, downloadUrlModel.getViewHtml());
                        updated = true;
                    }

                }
            }
        }
        if (updated) {
            webService.generateMovieHtml(id);
        } else {
            throw new UpdatedFailedException();
        }
    }


    /**
     * 保存下载地址，并生成文件
     *
     * @param id
     * @param addOnArticle
     * @param downUrls
     */
//    private void SaveDownUrls(Integer id, AddOnArticle addOnArticle, String downUrls) {
//        addOnArticle.setDownUrls(downUrls);
//        addOnArticle.setCurCollection(pushService.getCurrentCollection(addOnArticle.getDownUrls()));
//        addOnArticleRepository.save(addOnArticle);
//        archivesRepository.updateUpdateTime(id, System.currentTimeMillis() / 1000);
//
//        webService.generateMovieHtml(id);
//    }

    /**
     * 只更新下载地址 当前集 和 更新时间
     *
     * @param id
     * @param downUrls
     */
    private void SaveDownUrls(Integer id, String downUrls) {
        AddOnArticle addOnArticle = addOnArticleRepository.findOne(id);
        addOnArticle.setDownUrls(downUrls);
        addOnArticle.setCurCollection(pushService.getCurrentCollection(downUrls));
        addOnArticleRepository.save(addOnArticle);

        archivesRepository.updateUpdateTime(id, System.currentTimeMillis() / 1000);

        log.info(id + " SaveDownUrls");
    }

    /**
     * 更新相关(从字幕组)
     *
     * @param toId
     */
    @Override
    public void updateAbouts(Integer toId) throws UpdatedFailedException, IOException {
        boolean updated = false;
        AddOnArticle addOnArticle = addOnArticleRepository.findOne(toId);
        if (addOnArticle != null && addOnArticle.getFromZMZId() != null && addOnArticle.getFromZMZId() > 0) {
//            WebDriver driver = webDriverService.webDriverGenerator();
//            String mainInfoUrl = "http://www.zimuzu.tv/gresource/" + addOnArticle.getFromZMZId();
            SourceArticleFive sourceArticleFour = new SourceArticleFive();
            zmzService.getOneMovieMainInfo(addOnArticle.getFromZMZId(), sourceArticleFour);

            addOnArticle.setEnglishName(sourceArticleFour.getEnglishName().replace("&#39;", "'"));
            addOnArticle.setAreaName(sourceArticleFour.getAreaName());
            addOnArticle.setLanguage(sourceArticleFour.getLanguage());
            addOnArticle.setBeginTime(sourceArticleFour.getBeginTime());
            addOnArticle.setCorp(sourceArticleFour.getCorp());
            addOnArticle.setType(sourceArticleFour.getType());
            if (!StringUtils.isEmpty(sourceArticleFour.getImdb())) addOnArticle.setImdb(sourceArticleFour.getImdb());
            addOnArticle.setAlias(sourceArticleFour.getAlias());
            addOnArticle.setAuthor(sourceArticleFour.getAuthor());
            addOnArticle.setDirector(sourceArticleFour.getDirector());
            addOnArticle.setZhuYan(sourceArticleFour.getZhuYan());
            addOnArticleRepository.save(addOnArticle);
            updated = true;
//            driver.quit();
        }
        if (!updated) {
            throw new UpdatedFailedException();
        }
    }


//    @Transactional
//    public void doUrlsFromTTBatch() {
//
//        log.info("doUrlsFromTTBatch start");
//        List list = addOnArticleRepository.findTTID();
//        if (list != null) {
//            log.info("doing " + list.size());
//            for (Object object : list) {
//                Object[] objects = (Object[]) object;
//                Integer id = Integer.parseInt(objects[0].toString());
//                Integer ttId = Integer.parseInt(objects[1].toString());
//                String downUrls = getDownUrlsFromTT(ttId);
//                if (!StringUtils.isEmpty(downUrls)) {
//                    if (downUrls.indexOf("kmeiju.net") >= 0) {
//                        log.error("exist kmeiju.net address ");
//                    } else {
//
//                        AddOnArticle addOnArticle = addOnArticleRepository.findOne(id);
//                        if (!downUrls.equals(addOnArticle.getDownUrls())) {
//                            SaveDownUrls(id, addOnArticle, downUrls);
//
//
//                            log.info(id + " do");
//                        }
//                    }
//                } else {
//                    log.info(id + " " + ttId + " getDownUrlsFromTT is null");
//                }
//            }
//        }
//        log.info("doUrlsFromTTBatch finished");
//    }


    @Override
    @Transactional
    public void doUrlsFromTTBatchSmart() {
        log.info("doUrlsFromTTBatchSmart start");
        List list = addOnArticleRepository.findTTID();
        List<Integer> listUpdated = new ArrayList<>();
        if (list != null) {
            log.info("doing " + list.size());
            for (Object object : list) {
                Object[] objects = (Object[]) object;
                Integer id = Integer.parseInt(objects[0].toString());
                Integer ttId = Integer.parseInt(objects[1].toString());
                String downUrls = getDownUrlsFromTT(ttId);
                if (StringUtils.isEmpty(downUrls)) {
                    log.error(id + " " + ttId + " downurls null");
                    continue;
                }
                if (downUrls.indexOf("kmeiju.net") >= 0) {
                    log.error(id + " " + ttId + " have kmeiju.net address ");
                    continue;
                }

                boolean allowUpdated = false;
                allowUpdated = isAllowUpdated(id, downUrls, allowUpdated);

                if (allowUpdated) {
                    SaveDownUrls(id, downUrls);
                    listUpdated.add(id);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        log.info("save to database finished start do html");
        webService.batchGenerateMovieHtml(listUpdated);
        log.info("doUrlsFromTTBatchSmart finished");
    }

    private boolean isAllowUpdated(Integer id, String downUrls, boolean allowUpdated) {
        //比较downurls
        DownloadArticle downloadArticle = downloadArticleRepository.findOne(id);
        if (downloadArticle == null) {
            downloadArticle = new DownloadArticle();
            downloadArticle.setId(id);
            downloadArticle.setDownUrls(downUrls);
            downloadArticle.setTime(new Date());
            downloadArticleRepository.save(downloadArticle);
            allowUpdated = true;
        } else if (!downloadArticle.getDownUrls().endsWith(downUrls)) {
            downloadArticle.setDownUrls(downUrls);
            downloadArticle.setTime(new Date());
            downloadArticleRepository.save(downloadArticle);
            allowUpdated = true;
        }
        return allowUpdated;
    }

    /**
     * 自动处理urls tt和zmz
     */
    @Override
    @Scheduled(cron = "0 5 8 * * ?")
    @Scheduled(cron = "0 5 10 * * ?")
    @Scheduled(cron = "0 38 11 * * ?")
    @Scheduled(cron = "0 5 13 * * ?")
    @Scheduled(cron = "0 5 14 * * ?")
    @Scheduled(cron = "0 5 15 * * ?")
    @Scheduled(cron = "0 5 16 * * ?")
    @Scheduled(cron = "0 5 17 * * ?")
    @Scheduled(cron = "0 5 19 * * ?")
    @Scheduled(cron = "0 5 21 * * ?")
    @Scheduled(cron = "0 5 23 * * ?")
    @Transactional
    public void autoDoUrls() {

//        doUrlsFromTTBatchSmart();
//
//        autoDoArticle();

        doUrlsFromZMZBatchSmart();

        autoDoArticle();

        autoDoSEAndBack();

//        String cookies = env.getProperty("cookies");
//        doUrlsFromZMZBatch(cookies);

    }

    /**
     * 生成系统配置
     */
    @Override
    public void autoDoSysConfig() {
        try {
            Thread.sleep(1000 * 30);
        } catch (InterruptedException e) {
        }

        log.info("autoDoSysConfig start");

        WebDriver webDriver = null;
        try {
            webDriver = webDriverService.webDriverGenerator();
            utilsService.getWebDriver(webDriver);
            WebElement webElement;

            //2.自动更新sys_info
            webDriver.get("http://www.meijuhui.net/top100/sys_info.php");
//            webElement=webDriver.findElement(By.name("edit___tongji"));
//            webElement.sendKeys("t");

            webElement = webDriver.findElement(By.name("imageField"));
            webElement.click();


            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
            }

            webDriver.quit();
        } catch (Exception ex) {
            if (webDriver != null) {
                webDriver.quit();
            }
        }

        log.info("autoDoSysConfig end");
    }

    /**
     * 自动更新所有文章(由每片修改处理)  自动更新sitemap 自动更新首页
     */
    @Override
    public void autoDoArticle() {

        log.info("start do autoDoAricle");
        WebDriver webDriver = null;

        try {
            webDriver = webDriverService.webDriverGenerator();
            utilsService.getWebDriver(webDriver);

            WebElement webElement = null;
            //1.自动更新所有文章
//        webDriver.get("http://www.meijuhui.net/top100/makehtml_archives.php");
//        webElement = webDriver.findElement(By.name("b112"));
//        webElement.click();
//
//        try {
//            Thread.sleep(1000 * 60 * 50);
//        } catch (InterruptedException e) {
//        }


            //3.自动更新首页
            webDriver.get("http://www.meijuhui.net/top100/freelist_edit.php?aid=12");
            webDriver.findElement(By.id("nodefault")).click();
            webDriver.findElement(By.id("Submit2")).click();
            webDriver.get("http://www.meijuhui.net/top100/makehtml_freelist.php?aid=12");
            webDriver.findElement(By.name("b112")).click();

            try {
                Thread.sleep(1000 * 60 * 3);
            } catch (InterruptedException e) {
            }

            //2.自动更新sitemap
            webDriver.get("http://www.meijuhui.net/top100/makehtml_map_guide.php");
            webElement = webDriver.findElement(By.name("b112"));
            webElement.click();

            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
            }

            autoCreateIndexSiteMapXml();

            webDriver.quit();
        } catch (Exception ex) {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
        log.info("end do autoDoAricle");
    }

    @Override
    public void autoCreateTagSiteMapXml() {
        log.info("start autoCreateTagSiteMapXml");

        String path = "D:\\wwwroot\\liuxueba\\wwwroot\\tag";//D:\publish\xingguang
        File file = new File(path);
        List<File> files = FileUtil.getDirectory(file);
        StringBuilder items = new StringBuilder();
        for (File f : files) {
            String url = "http://www.meijuhui.net/tag" + f.getAbsolutePath().replace(path, "").replace("\\", "/");
            items.append(SitemapHelper.generateItemXml(url, "", "weekly", "0.8"));
        }
        String tagXml = SitemapHelper.generateXml(items.toString());

        try {
            String tagXmlPathName = "D:\\wwwroot\\liuxueba\\wwwroot\\tag.xml";
            FileUtil.write(tagXmlPathName, tagXml);
        } catch (IOException e) {
            log.error(e);
        }
        log.info("end autoCreateTagSiteMapXml");
    }

    @Override
    public void autoCreateIndexSiteMapXml() {
        log.info("start autoCreateIndexSiteMapXml");
        List<String> urls = new ArrayList<>();
        urls.add("http://www.meijuhui.net/article.xml");
        urls.add("http://www.meijuhui.net/tag.xml");
        String xml = SitemapHelper.generateIndexXml(urls, LocalDateTimeUtils.convert(LocalDateTime.now()));
        try {
            String xmlPathName = "D:\\wwwroot\\liuxueba\\wwwroot\\sitemap.xml";
            FileUtil.write(xmlPathName, xml);
        } catch (IOException e) {
            log.error(e);
        }
        log.info("end autoCreateIndexSiteMapXml");
    }

    /**
     * 创建每日schedule
     */
    @Override
    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional
    public void autoCreateDaySchedule() {
        Integer year = LocalDateTime.now().getYear();
        Integer month = LocalDateTime.now().getMonthValue();
        createDaySchedule(year, month);

        autoDoSysConfig();

        autoDoArticle();
    }

    /**
     * 自动处理集和回归列表
     */
    @Override
    @Scheduled(cron = "0 15 1 * * ?")
    @Transactional
    public void autoDoSEAndBack() {

        //处理未开播 改为开播
        log.info("start do updateStatusByNoBegin");
        addOnArticleRepository.updateStatusByNoBegin();

//        log.info("start do doThisSE");
//        doThisSE();

        createBackList();

        autoCreateTagSiteMapXml();

        autoCreateIndexSiteMapXml();
    }

    /**
     * 自动更新评分 自动更新tags 每个周三
     */
    @Override
    @Scheduled(cron = "0 30 2 ? * WED")
    public void autoDoScoreTags() {
        log.info("start do autoDoScoreTags");
        WebDriver webDriver = webDriverService.webDriverGenerator();
        utilsService.getWebDriver(webDriver);

        //1.自动更新评分
        webDriver.get("http://www.meijuhui.net/top100/freelist_edit.php?aid=13");
        webDriver.findElement(By.id("nodefault")).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        webDriver.findElement(By.id("Submit2")).click();
        webDriver.get("http://www.meijuhui.net/top100/makehtml_freelist.php?aid=13");
        webDriver.findElement(By.name("b112")).click();


        try {
            Thread.sleep(1000 * 60 * 50);
        } catch (InterruptedException e) {
        }


        //2.自动更新tags
        webDriver.get("http://www.meijuhui.net/top100/tags_main.php");

        log.info("do autoDoScore finished");

        webDriver.findElement(By.name("startaid")).sendKeys("1");
        webDriver.findElement(By.name("endaid")).sendKeys("50000");
        webDriver.findElement(By.name("submit")).click();

        try {
            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
        }

        webDriver.get("http://www.meijuhui.net/top100/makehtml_tag.php");
        List<WebElement> webElementList = webDriver.findElements(By.name("all"));
        webElementList.get(0).click();

        webDriver.findElement(By.name("nic_b112")).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        webDriver.findElement(By.name("b112")).click();

        try {
            Thread.sleep(1000 * 60 * 20);
        } catch (InterruptedException e) {
        }
        webDriver.quit();
        log.info("end do autoDoScoreTags");
    }


    /**
     * @param startZmzId
     * @param endZmzId
     * @param cookies
     */
    @Override
    public void addNewFromStartZMZ(Integer startZmzId, Integer endZmzId, String cookies) {
        log.info("addNewFromStartZMZ start " + startZmzId + " to " + endZmzId);
        //自动取配置中的cookies
        if (StringUtils.isEmpty(cookies)) {
            cookies = env.getProperty("cookies");
        }
        Integer zmzId;
        for (zmzId = startZmzId; zmzId <= endZmzId; zmzId++) {
            //判断是否存在
            List<Integer> lists = addOnArticleRepository.findByZmzId(zmzId);
            if (lists != null && lists.size() >= 1) {
                log.info("exist " + zmzId);
                continue;
            }

            //开始抓取
            SourceArticleFive sourceArticleFour = zmzService.getZMZData(zmzId, cookies, 0);
            if (sourceArticleFour != null && !StringUtils.isEmpty(sourceArticleFour.getMainType())) {
                saveDataFromPullZmz(zmzId, sourceArticleFour);

//                String mainType = sourceArticleFour.getMainType();
//                if (mainType.equals("电影")) {
//
//                } else if (!StringUtils.isEmpty(sourceArticleFour.getEnglishName())) {
//                    lists = addOnArticleRepository.findByEnglishName(sourceArticleFour.getEnglishName());
//                    if (lists != null && lists.size() >= 1) {
//                        log.info("exist " + zmzId + " " + sourceArticleFour.getEnglishName());
//                        continue;
//                    }
//                }


            } else {
                log.error(zmzId + " pull error or mainType null");
            }
        }
        ///driver.quit();
        log.info("addNewFromStartZMZ end ");
    }

    private void saveDataFromPullZmz(Integer zmzId, SourceArticleFive sourceArticleFour) {
        String mainType = sourceArticleFour.getMainType();
        Integer typeId = 0;
        //主类型 如 纪录片 电影 电视剧 剧 动画 （公开课）
        if (mainType.equals("纪录片")) {
            typeId = 37;
        } else if (mainType.equals("电影")) {
            typeId = 44;
        } else if (mainType.equals("动画")) {
            typeId = 39;
        } else if (mainType.indexOf("剧") >= 0) {
            String type = sourceArticleFour.getType();
            if (!StringUtils.isEmpty(type)) {
                if (type.indexOf("魔幻") >= 0 || type.indexOf("科幻") >= 0) {
                    typeId = 31;
                } else if (type.indexOf("罪案") >= 0 || type.indexOf("谍战") >= 0) {
                    typeId = 32;
                } else if (type.indexOf("灵异") >= 0 || type.indexOf("惊悚") >= 0 || type.indexOf("恐怖") >= 0) {
                    typeId = 33;
                } else if (type.indexOf("动作") >= 0 || type.indexOf("战争") >= 0) {
                    typeId = 30;
                } else if (type.indexOf("喜剧") >= 0) {
                    typeId = 34;
                } else if (type.indexOf("律政") >= 0 || type.indexOf("医务") >= 0) {
                    typeId = 35;
                } else if (type.indexOf("剧情") >= 0 || type.indexOf("历史") >= 0 || type.indexOf("古装") >= 0 || type.indexOf("史诗") >= 0) {
                    typeId = 42;
                } else if (type.indexOf("都市情感") >= 0 || type.indexOf("生活") >= 0 || type.indexOf("爱情") >= 0) {
                    typeId = 43;
                } else if (type.indexOf("真人秀") >= 0) {
                    typeId = 36;
                } else if (type.indexOf("动画") >= 0) {
                    typeId = 39;
                }
            }
        }


        if (typeId > 0) {
            Integer id = archivesRepository.findMaxId() + 1;
            Archives archives = new Archives();
            archives.setId(id);
            archives.setTypeid(typeId);
            archives.setTitle(sourceArticleFour.getTitle().replace("&#39;", "'"));
            archives.setKeywords(sourceArticleFour.getKeywords());
            archives.setUpdateTime(System.currentTimeMillis() / 1000);
            archives.setSenddate(System.currentTimeMillis() / 1000);
            archives.setVoteId(0);

            if (typeId == 44) {
                archives.setTitle(sourceArticleFour.getTitle().replace("&#39;", "'").replace("/全集", ""));
            }
            archives.setLitPic("");

            archives = archivesRepository.save(archives);

            AddOnArticle addOnArticle = new AddOnArticle();
            addOnArticle.setAid(archives.getId());
            addOnArticle.setTypeid(typeId);
            addOnArticle.setPingfen(sourceArticleFour.getPingfen());
            addOnArticle.setFeedback(0);

            addOnArticle.setEnglishName(sourceArticleFour.getEnglishName().replace("&#39;", "'"));
            addOnArticle.setAreaName(sourceArticleFour.getAreaName());
            addOnArticle.setLanguage(sourceArticleFour.getLanguage());
            addOnArticle.setBeginTime(sourceArticleFour.getBeginTime());
            addOnArticle.setCorp(sourceArticleFour.getCorp());
            addOnArticle.setType(sourceArticleFour.getType());
            addOnArticle.setImdb(sourceArticleFour.getImdb());
            addOnArticle.setAlias(sourceArticleFour.getAlias());
            addOnArticle.setAuthor(sourceArticleFour.getAuthor());
            addOnArticle.setDirector(sourceArticleFour.getDirector());
            addOnArticle.setZhuYan(sourceArticleFour.getZhuYan());
            addOnArticle.setMovieName(sourceArticleFour.getTitle().replace(sourceArticleFour.getEnglishName(), "").replace("迅雷下载", "").replace("/全集", ""));
            addOnArticle.setSummary(sourceArticleFour.getSummary().replace("&#39;", "'"));
            addOnArticle.setPictureUrl("<img alt=\"" + addOnArticle.getMovieName() + "\" src=\"" + sourceArticleFour.getPictureUrl() + "\" />");
            addOnArticle.setFromZMZId(zmzId);
            addOnArticle.setDownUrls(sourceArticleFour.getDownUrls());
            addOnArticle.setCurCollection(pushService.getCurrentCollection(addOnArticle.getDownUrls()));
            addOnArticle.setSeason(utilsService.getSeason(addOnArticle.getMovieName()));
            if (typeId == 44) {
                if (StringUtils.isEmpty(addOnArticle.getDownUrls())) {
                    addOnArticle.setStaus("未开播");
                } else {
                    addOnArticle.setStaus("本季完结");
                }
            } else if (StringUtils.isEmpty(addOnArticle.getDownUrls())) {
                addOnArticle.setStaus("未开播");
            } else {
                addOnArticle.setStaus("连载中");
            }

            if (addOnArticle.getImdb() != null) {
                addOnArticle.setImdb(addOnArticle.getImdb().replace("http://www.imdb.com/title/", ""));
            }

            addOnArticle.setRecommend("");
            addOnArticle.setBody("");
            addOnArticleRepository.save(addOnArticle);

            Arctiny arctiny = new Arctiny();
            arctiny.setId(archives.getId());
            arctiny.setTypeId(typeId);
            arctiny.setTypeid2("0");
            arctiny.setArcrank(-2);
            arctiny.setChannel(1);
            arctiny.setSendDate(1497521190);
            arctiny.setSortRank(1497521178);
            arctiny.setMid(1);
            arctinyRepository.save(arctiny);

            log.info(zmzId + " do");
        } else {
            log.error(zmzId + " " + mainType + " " + sourceArticleFour.getType());
        }
    }

//    /**
//     * 批量修改字幕组下载地址
//     *
//     * @param cookies
//     */
//    @Override
//    @Transactional
//    public void doUrlsFromZMZBatch(String cookies) {
//        log.info("doUrlsFromZMZBatch start");
//        List list = addOnArticleRepository.findZMZTTID();
//        if (list != null) {
//            log.info("doing " + list.size());
//            for (Object object : list) {
//                Object[] objects = (Object[]) object;
//                Integer id = Integer.parseInt(objects[0].toString());
//                Integer zmzId = Integer.parseInt(objects[1].toString());
//                String movieName = objects[2].toString();
//
//                WebDriver driver = null;
//
//                try {
//                    driver = utilsService.getZMZWebDriver(cookies);
//                    String theSeason = utilsService.getZmzSeasonName(movieName);
//                    String downUrls = zmzService.getOneMovieDownloadUrl(driver, zmzId, theSeason);
//
//                    if (!StringUtils.isEmpty(downUrls)) {
//                        AddOnArticle addOnArticle = addOnArticleRepository.findOne(id);
//                        if (!downUrls.equals(addOnArticle.getDownUrls())) {
//                            SaveDownUrls(id, addOnArticle, downUrls);
//                            log.info(id + " do");
//                        }
//                    }
//
//                    driver.quit();
//
//                } catch (Exception ex) {
//                    if (driver != null) driver.quit();
//                }
//
//
//                doZmzOverStatus(id, zmzId, movieName);
//            }
//        }
//        log.info("doUrlsFromZMZBatch finished");
//    }

    @Transactional
    public void doUrlsFromZMZBatchSmart() {
        log.info("doUrlsFromZMZBatchSmart start");
        List list = addOnArticleRepository.findZMZTTID();
        List<Integer> listUpdated = new ArrayList<>();
        if (list != null) {
            log.info("doing " + list.size());
            for (Object object : list) {
                Object[] objects = (Object[]) object;
                Integer id = Integer.parseInt(objects[0].toString());
                Integer zmzId = Integer.parseInt(objects[1].toString());
                String movieName = objects[2].toString();
                Integer season = Integer.parseInt(objects[3].toString());

//                String theSeason = utilsService.getZmzSeasonName(movieName);
                DownloadUrlModel downloadUrlModel = null;
                try {
                    downloadUrlModel = zmzService.getOneMovieDownloadUrlSmart(zmzId, season);
                } catch (Exception e) {
                }

                if (downloadUrlModel == null || StringUtils.isEmpty(downloadUrlModel.getViewHtml())) {
                    log.error(id + " " + zmzId + " downurls null");
                    continue;
                }

                boolean allowUpdated = false;
                allowUpdated = isAllowUpdated(id, downloadUrlModel.getSourceHtml(), allowUpdated);

                if (allowUpdated) {
                    SaveDownUrls(id, downloadUrlModel.getViewHtml());
                    listUpdated.add(id);

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                    }
                }


                doZmzOverStatus(id, zmzId, movieName);
            }
        }

        log.info("save to database finished start do html");
        webService.batchGenerateMovieHtml(listUpdated);
        log.info("doUrlsFromZMZBatchSmart finished");
    }


    private void doZmzOverStatus(Integer id, Integer zmzId, String movieName) {
        //处理完结
        //查找本季状态
        try {
            boolean isOver = false;
            PlayStatusModel model = zmzService.getStatus(zmzId);
            if (model != null) {
                String over = model.getPlay_status();
                if (!StringUtils.isEmpty(over)) {
                    if (over.equals("本剧完结")) {
                        isOver = true;
                    } else if (over.indexOf("完结") >= 0) {
                        isOver = true;
                        over = "本季完结";
                    }
                }
                if (isOver) {
                    addOnArticleRepository.updateStatus(id, over);
                    log.info(id + " " + movieName + " " + over);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void batchEditForPicture(Integer toId, Integer endId) {
        log.info("start batchEditForPicture");
        List<Integer> ids = new ArrayList<>();
        for (Integer i = toId; i <= endId; i++) {
            ids.add(i);
        }
        webService.batchGenerateMovieHtml(ids);
        log.info("end batchEditForPicture");
    }


//    private void editOneOperForFireFox(Integer id, WebDriver driver) {
//        //3.添加操作
//        String addUrl = "http://www.meijuhui.net/top100/article_edit.php?aid=" + id;
//        driver.get(addUrl);
//        driver.manage().window().maximize();
//
//        //切换到源码 内容过多无法设置数据 否则火狐下内容没法保存
//        driver.findElement(By.id("cke_8_label")).click();
//
//        //切回主文档
////        webDriver.switchTo().defaultContent();
//
//        driver.findElement(By.name("imageField")).click();
//
//    }

    @Transactional
    public void updateAllImdbAndMaxCollectionFromDB() {
        log.info("start updateAllImdbAndMaxCollectionFromDB");
//        List list = addOnArticleRepository.findByImdbTTID();
//        if (list != null) {
//            log.info("doing " + list.size());
//            for (Object object : list) {
//                Object[] objects = (Object[]) object;
//                Integer id = Integer.parseInt(objects[0].toString());
//                Integer ttId = Integer.parseInt(objects[1].toString());
//
//                TianTianModel model = tianTianService.getImdbFromTT(ttId);
//                if (model != null && !StringUtils.isEmpty(model.getImdb())) {
//                    addOnArticleRepository.updateImdb(id, model.getImdb());
//                }
//
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//
//                }
//            }
//        }
//
//        list = addOnArticleRepository.findByTTID();
//        if (list != null) {
//            log.info("doing " + list.size());
//            for (Object object : list) {
//                Object[] objects = (Object[]) object;
//                Integer id = Integer.parseInt(objects[0].toString());
//                Integer ttId = Integer.parseInt(objects[1].toString());
//
//                try {
//                    TianTianModel model = tianTianService.getImdbFromTT(ttId);
//                    if (model != null && !StringUtils.isEmpty(model.getMaxCollection()) && Integer.parseInt(model.getMaxCollection()) > 0) {
//                        addOnArticleRepository.updateMaxCollection(id, Integer.parseInt(model.getMaxCollection()));
//                    }
//                } catch (Exception ex) {
//                    log.error(id + " " + ttId);
//                }
//            }
//        }
        log.info("end updateAllImdbAndMaxCollectionFromDB");

    }


    /**
     * 生成美剧新剧回归时间表
     */
    @Transactional
    @Override
    public void createBackList() {
        log.info("start do createBackList");

        StringBuilder html = new StringBuilder();
        List<Integer> types = new ArrayList<>();
        types.add(30);
        types.add(31);
        types.add(32);
        types.add(33);
        types.add(34);
        types.add(35);
//        types.add(36);
//        types.add(37);
        types.add(39);
        types.add(42);
        types.add(43);
        List list = addOnArticleRepository.findByThisYear(types);
        String nav = "<p id='month'>";
        String lastMonth = "0";
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            String beginTime = objects[1].toString();
            String movieName = objects[2].toString();
            String englishName = objects[3].toString();
            String staus = objects[4].toString();
            Integer curCollection = Integer.parseInt(objects[5].toString());

            String thisDay = beginTime.split(" ")[0];

            String month = "";
//            String day = "";
            if (thisDay.split("-").length == 3) {
                month = thisDay.split("-")[1];
//                day = thisDay.split("-")[2];
            }

            if (!month.equals(lastMonth)) {
                //month = month.startsWith("0") ? month.substring(1) : month;
                html.append("<h2 id='" + month + "'>" + month + "月美剧播出时间表</h2>");
                nav = nav + "<a href=\"#" + month + "\">" + month + "月</a>";
            }

            beginTime = beginTime.length() >= 10 ? beginTime.substring(0, 10) : beginTime;
            String weekName = LocalDateTimeUtils.getWeekName(beginTime);

            html.append("<p>" + beginTime + " " + weekName + "《<a href=\"/archives/" + id + ".html\" target=\"_blank\">" + movieName + "</a>》" + englishName + " " + staus + ((staus.equals("连载中") ? " 已更新第" + curCollection + "集" : ""))
                    + "</p>");


            lastMonth = month;
        }
        nav = nav + "</p><div style=\"clear:both\"></div>";


        addOnArticleRepository.updateBody(1911, nav + html.toString());
        webService.generateMovieHtml(1911);

        log.info("end do createBackList");
    }


    /**
     * 处理季和当前集
     */
    @Override
    @Transactional
    public void doThisSE() {
        List<Integer> types = new ArrayList<>();
        types.add(30);
        types.add(31);
        types.add(32);
        types.add(33);
        types.add(34);
        types.add(35);
        types.add(39);
        types.add(42);
        types.add(43);
        List list = addOnArticleRepository.findAllByTypes(types);
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            String downUrls = objects[1].toString();
            String movieName = objects[2].toString();
            int currentCollection = pushService.getCurrentCollection(downUrls);
            int season = utilsService.getSeason(movieName);

            AddOnArticle addOnArticle = addOnArticleRepository.findOne(id);
            addOnArticle.setCurCollection(currentCollection);
            addOnArticle.setSeason(season);
            addOnArticleRepository.save(addOnArticle);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * 查询死链
     */
    @Override
    public void checkDeadLink() {
        log.info("start do checkDeadLink");

        List list = addOnArticleRepository.findAllSummary();
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            String summary = objects[1].toString();
            String movieName = objects[2].toString();
            List<String> urls = utilsService.getHrefList(summary);
            urls.forEach(url -> {
                try {
                    if (url.startsWith("/archives/")) {
                        int curId = Integer.parseInt(url.replace("/archives/", "").replace(".html", ""));
                        if (!archivesRepository.exists(curId)) {
                            log.info(id + " " + movieName + " " + url + " no exist");
                        }

                    }
                } catch (Exception e) {
                    log.error(id + " " + movieName + " " + url + " error");
                }
            });

        }

        log.info("end do checkDeadLink");
    }

    /**
     * 处理简介里面没有连接的
     */
    @Override
    @Transactional
    public void doSummaryNoLink() {
        log.info("start do doSummaryNoLink");
        List list = addOnArticleRepository.findAllSummary();
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            String summary = objects[1].toString();
            String moviName = objects[2].toString();
            Pattern pattern = Pattern.compile("《(.*?)》", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(summary);
            int i = 0;
            String summary1 = summary;
            while (matcher.find()) {
                String curTitle = matcher.group(1);
                if (curTitle.indexOf("</a>") < 0 && !moviName.contains(curTitle)) {
                    Integer findId = 0;
                    List<Integer> finds = addOnArticleRepository.findByMovieName(curTitle);
                    if (finds != null && finds.size() > 0) {
                        findId = finds.get(0);
                    } else {
                        finds = addOnArticleRepository.findByLikeMovieName(curTitle + "%");
                        if (finds != null && finds.size() > 0) {
                            findId = finds.get(0);
                        }
                    }

                    if (findId <= 0 && curTitle.indexOf("第") >= 0) {
                        curTitle = curTitle.substring(0, curTitle.indexOf("第"));
                        finds = addOnArticleRepository.findByLikeMovieName(curTitle + "%");
                        if (finds != null && finds.size() > 0) {
                            findId = finds.get(0);
                        }
                    }

                    if (findId > 0) {
                        summary1 = summary1.replaceAll(matcher.group(), "《<a href=\"/archives/" + findId + ".html\">" + matcher.group(1) + "</a>》");
                    }

                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {

                    }
                }
                i++;
            }

            if (!summary.equals(summary1)) {
                addOnArticleRepository.updateSummary(id, summary1);
                log.info(id + " " + moviName + " do");
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {

            }
        }
        log.info("end do doSummaryNoLink");
    }


    /**
     * 处理关键字中的年份
     */
    @Transactional
    public void doKeywordsYear() {
        log.info("start doKeywordsYear");
        List<Integer> years = new ArrayList<>();
        int i = 1940;
        while (i <= 2017) {
            years.add(i);
            i++;
        }


        List list = addOnArticleRepository.findAllBeginTime();
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            String beginTime = objects[1].toString();
            try {
                if (!StringUtils.isEmpty(beginTime) && beginTime.length() >= 4 && years.contains(Integer.parseInt(beginTime.substring(0, 4)))) {
                    Integer theYear = Integer.parseInt(beginTime.substring(0, 4));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {

                    }

                    Archives archives = archivesRepository.findOne(id);
                    if (archives.getKeywords().indexOf(theYear.toString()) < 0) {
                        archives.setKeywords(StringUtils.isEmpty(archives.getKeywords()) ? theYear.toString() : archives.getKeywords() + "," + theYear);
                        archivesRepository.save(archives);
                        log.info(id + " do");
                    }
                } else {
                    log.error(id + " " + beginTime);
                }
            } catch (Exception ex) {
                log.error(id + " " + beginTime);
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {

            }
        }


        log.info("end doKeywordsYear");
    }

    /**
     * 处理推荐 多季的问题
     */
    @Transactional
    public void doRecommend() {
        log.info("start doRecommend");
        List list = addOnArticleRepository.findAllRecommend();
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            String recommend = objects[1].toString();
            String movieName = objects[2].toString();
            String englishName = objects[3].toString();

            List<String> hrefs = utilsService.getHrefList(recommend);
            String theName = movieName.indexOf("第") >= 0 ? movieName.substring(0, movieName.indexOf("第")) : movieName;
//            if (recommend.indexOf(theName) < 0) {
//                //不存在的情况，查找
//
//            }

            String t = getRecommendStr(theName, englishName, id, hrefs);
            if (!StringUtils.isEmpty(t)) {
                addOnArticleRepository.updateRecommend(id, recommend + t);
                log.info(id + " " + movieName + " do");
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {

            }
        }
        log.info("end doRecommend");
    }

    private String getRecommendStr(String theName, String englishName, int excludeId, List<String> excluedeHrefs) {
        String result = "";
        List movies = addOnArticleRepository.findAllLikeNameAndEnglishName(excludeId, theName + "%", englishName);
        if (movies != null && movies.size() > 0) {
            for (Object obj : movies) {
                Object[] objects = (Object[]) obj;
                Integer id = Integer.parseInt(objects[0].toString());
                String movieName = objects[1].toString();

                if (!excluedeHrefs.contains("/archives/" + id + ".html")) {
                    result += "《<a href=\"/archives/" + id + ".html\">" + movieName + "</a>》";
                }
            }
        }
        return result;
    }

    /**
     * 查找终结的
     */
//    public void findOver() {
//        List list = addOnArticleRepository.findSerializing();
//
//        for (Object obj : list) {
//            Object[] objects = (Object[]) obj;
//            Integer id = Integer.parseInt(objects[0].toString());
//            String fromZMZId = objects[1].toString();
//            String movieName = objects[2].toString();
//
//
//        }
//    }

    /**
     * 创建月美剧回归
     */
    @Override
    public void
    createLastMonthBack(Integer year, Integer month, Integer aid) {
        log.info("start createLastMonthBack");
        StringBuilder html = new StringBuilder();
        List<Integer> types = new ArrayList<>();
        types.add(30);
        types.add(31);
        types.add(32);
        types.add(33);
        types.add(34);
        types.add(35);
        types.add(39);
        types.add(42);
        types.add(43);
        int n = 0;
        String myPic = "";
        List list = addOnArticleRepository.findByThisMonth(types, year + "-" + (String.valueOf(month).length() == 1 ? "0" + month : month) + "%");
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            String beginTime = objects[1].toString();
            String movieName = objects[2].toString();
            String englishName = objects[3].toString();
            String summary = objects[4].toString();

            String imgUrl = archivesRepository.getLitPic(id);

            html.append("<h1><a href=\"/archives/" + id + ".html\" target=\"_blank\">" + movieName + englishName + "</a></h1>");
            html.append("<p>" + summary + "</p>");
            html.append("<p><img src=\"" + imgUrl.replace("-lp.jpg", ".jpg") + "\" /></p>");
            if (n == 0) myPic = imgUrl;
            n++;
        }

        if (aid > 0) {
            AddOnArticle addOnArticle = addOnArticleRepository.findOne(aid);
            addOnArticle.setBody(html.toString());
            addOnArticleRepository.save(addOnArticle);

            webService.generateMovieHtml(aid);

        } else {
            String title = year + "年" + month + "月美剧回顾";
            Integer id = archivesRepository.findMaxId() + 1;
            Integer typeId = 45;
            Archives archives = new Archives();
            archives.setId(id);
            archives.setTypeid(typeId);
            archives.setTitle(title);
            archives.setKeywords(year.toString());
            archives.setUpdateTime(System.currentTimeMillis() / 1000);
            archives.setSenddate(System.currentTimeMillis() / 1000);
            archives.setVoteId(0);

            archives.setLitPic(myPic);

            archives = archivesRepository.save(archives);

            AddOnArticle addOnArticle = new AddOnArticle();
            addOnArticle.setAid(archives.getId());
            addOnArticle.setTypeid(typeId);
            addOnArticle.setPingfen(0F);
            addOnArticle.setFeedback(0);

            addOnArticle.setEnglishName("其他");
            addOnArticle.setAreaName("其他");
            addOnArticle.setLanguage("");
            addOnArticle.setBeginTime("");
            addOnArticle.setCorp("");
            addOnArticle.setType("");
            addOnArticle.setImdb("");
            addOnArticle.setAlias("");
            addOnArticle.setAuthor("");
            addOnArticle.setDirector("");
            addOnArticle.setZhuYan("");
            addOnArticle.setMovieName("");
            addOnArticle.setSummary("");
            addOnArticle.setPictureUrl("");
            addOnArticle.setFromZMZId(0);
            addOnArticle.setDownUrls("");
            addOnArticle.setRecommend("");
            addOnArticle.setBody(html.toString());
            addOnArticle.setStaus("本季完结");
            addOnArticleRepository.save(addOnArticle);

            Arctiny arctiny = new Arctiny();
            arctiny.setId(archives.getId());
            arctiny.setTypeId(typeId);
            arctiny.setTypeid2("0");
            arctiny.setArcrank(-2);
            arctiny.setChannel(1);
            arctiny.setSendDate(1497521190);
            arctiny.setSortRank(1497521178);
            arctiny.setMid(1);
            arctinyRepository.save(arctiny);

            webService.generateMovieHtml(id);
        }

//        log.info(html);
        log.info("end createLastMonthBack");
    }

    @Override
    public void updateAboutFromDB(Integer id) {
        AddOnArticle addOnArticle = addOnArticleRepository.findOne(id);
        if (addOnArticle != null) {
//            String movieName = addOnArticle.getMovieName();
            initArticleFromDouban(addOnArticle.getFromDBId(), addOnArticle.getSeason(), addOnArticle);
            addOnArticleRepository.save(addOnArticle);
            log.info(id + " do");
        }
    }


//    public String findNewMovie(Integer year, Integer month) {
//        StringBuilder result = new StringBuilder();
//        log.info("findNewMovie start " + year + " " + month);
//        String u = "http://www.zimuzu.tv/tv/schedule/index/year/" + year + "/month/" + month;
//        try {
//            URL url = new URL(u);
//            Source source = new Source(url);
//            Element element = source.getFirstElementByClass("play-schedule");
//            List<String> hrefs = utilsService.getHrefList(element.getContent().toString());
//            List<String> list = new ArrayList<>();
//            for (String item : hrefs) {
//                if (item.indexOf("/resource/") == 0 && !list.contains(item)) {
//                    list.add(item);
//                }
//            }
//
//            for (String item : list) {
//                Integer zmzId = Integer.parseInt(item.replace("/resource/", ""));
//                List<Integer> ids = addOnArticleRepository.findByZmzId(zmzId);
//                if (ids == null || ids.size() == 0) {
//                    result.append(zmzId + ",");
//                    log.info(zmzId + " need do");
//                }
//            }
//        } catch (IOException e) {
//        }
//        log.info("findNewMovie end ");
//        return result.toString();
//    }

    /**
     * 创建每月漏的新剧
     *
     * @param year
     * @param month
     */
    @Override
    public void createMovieFromZmzSchedule(Integer year, Integer month) {
        log.info("createMovieFromZmzSchedule start " + year + " " + month);
        String cookies = env.getProperty("cookies");

        Map<String, List<LinkModel>> map = getZmzMovieList(year, month);
        Iterator<Map.Entry<String, List<LinkModel>>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<LinkModel>> entry = entries.next();
            List<LinkModel> linkModels = entry.getValue();
            String theDay = entry.getKey();
            for (LinkModel linkModel : linkModels) {
                Integer zmzId = Integer.parseInt(linkModel.getUrl().replace("/resource/", ""));

                boolean created = false;
                Integer season = 0;
                if (linkModel.getName().indexOf("Mini") >= 0) {
                    List<Integer> finds = addOnArticleRepository.findByZmzId(zmzId);
                    if (finds == null || finds.size() < 1) {
                        created = true;
                    }
                } else {
                    MovieModel movieModel = utilsService.getMovie(utilsService.clearFormat(linkModel.getName()));
                    if (movieModel == null) {
                        log.error(linkModel.getName() + " find movieModel error");
                    } else {
                        //season exists a.aid,a.curCollection
                        List<Integer> finds = addOnArticleRepository.findSeason(zmzId, movieModel.getSeason());
                        if (finds == null || finds.size() < 1) {
                            season = movieModel.getSeason();
                            created = true;
                        }
                    }
                }
                if (created) {
                    // begin create season
                    SourceArticleFive sourceArticleFour = zmzService.getZMZData(zmzId, cookies, season);
                    if (sourceArticleFour != null && !StringUtils.isEmpty(sourceArticleFour.getMainType())) {
                        saveDataFromPullZmz(zmzId, sourceArticleFour);
                    } else {
                        log.error(zmzId + " pull error or mainType null");
                    }
                }
            }
        }
        log.info("createMovieFromZmzSchedule end");
    }

    /**
     * 生成每天播出表
     *
     * @param year
     * @param month
     */
    public void createDaySchedule(Integer year, Integer month) {
        log.info("createDaySchedule start " + year + " " + month);

        Map<String, List<LinkModel>> map = getZmzMovieList(year, month);
        Iterator<Map.Entry<String, List<LinkModel>>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<LinkModel>> entry = entries.next();
            List<LinkModel> linkModels = entry.getValue();
            StringBuilder dayHtml = new StringBuilder();
            StringBuilder dayHtml2 = new StringBuilder();
            StringBuilder dayHtml3 = new StringBuilder();

            String theDay = entry.getKey();
            //只处理当日的
// || theDay.endsWith(String.valueOf(LocalDateTime.now().plusDays(1).getDayOfMonth()))
            if (theDay.equals(String.valueOf(LocalDateTime.now().getDayOfMonth()))) {

                for (LinkModel linkModel : linkModels) {
                    Integer zmzId = Integer.parseInt(linkModel.getUrl().replace("/resource/", ""));
                    Integer findId = 0;
                    if (linkModel.getName().indexOf("Mini") >= 0) {
                        List<Integer> finds = addOnArticleRepository.findByZmzId(zmzId);
                        if (finds != null && finds.size() > 0) {
                            findId = finds.get(0);
                        }
                    } else {
                        MovieModel movieModel = utilsService.getMovie(utilsService.clearFormat(linkModel.getName()));
                        if (movieModel == null) {
                            log.error(linkModel.getName() + " get movieModel error");
                        } else {
                            //season exists
                            List<Integer> finds = addOnArticleRepository.findSeason(zmzId, movieModel.getSeason());
                            if (finds != null && finds.size() >= 1) {
                                findId = finds.get(0);
                            }
                        }
                    }
                    if (findId > 0) {
                        if (dayHtml.indexOf("/archives/" + findId + ".html") < 0) {
                            dayHtml.append("<a href=\"/archives/" + findId + ".html\">" + utilsService.clearFormat(linkModel.getName()) + "</a>");
                            dayHtml2.append(utilsService.clearFormat(linkModel.getName()) + "http://www.meijuhui.net/archives/" + findId + ".html \r\n");
                            dayHtml3.append(utilsService.clearFormat(linkModel.getName()) + " \r\n");

                        }
                    } else
                        log.error("no find " + zmzId + " " + linkModel.getName());
                }


                String updateHtml = "<div class=\"title d_cast_title\"><h2>今日播出 " + (year + "-" + month + "-" + theDay)
                        + " " + LocalDateTimeUtils.getWeekName((year + "-" + (month < 10 ? "0" + month : month) + "-" + (theDay.length() == 1 ? "0" + theDay : theDay))) + "</h2></div>\r\n" +
                        "<div  class=\"d_cast\">" + dayHtml.toString() + "</div>";
                log.info(updateHtml);
                log.info("<h2>" + theDay + "</h2> \r\n" + dayHtml2.toString());
                log.info("<h2>" + theDay + "</h2> \r\n" + dayHtml3.toString());

                if (theDay.endsWith(String.valueOf(LocalDateTime.now().getDayOfMonth()))) {
                    SysConfig sysConfig = sysConfigRepository.findOne("cast");
                    if (sysConfig != null) {
                        sysConfig.setValue(updateHtml);
                        sysConfigRepository.save(sysConfig);

                        log.info("createDaySchedule save to database");
                    }

                }
                break;
            }
        }

        log.info("createDaySchedule end");
    }

    /**
     * 检测开始时间有效性 zmz比较
     */
    @Transactional
    public void checkBeginTime(Integer year, Integer month) {
        List list = addOnArticleRepository.findBeginTimeBySerializing();

        Map<String, List<LinkModel>> map = getZmzMovieList(year, month);
        Iterator<Map.Entry<String, List<LinkModel>>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<LinkModel>> entry = entries.next();
            List<LinkModel> linkModels = entry.getValue();
            String theDay = entry.getKey();
            LocalDateTime now = LocalDateTime.of(year, month, Integer.parseInt(theDay), 0, 0, 0);
            for (LinkModel linkModel : linkModels) {
                Integer zmzId = Integer.parseInt(linkModel.getUrl().replace("/resource/", ""));
                if (linkModel.getName().indexOf("Mini") >= 0) {
                    voidBeginTime(list, zmzId, 0, now);
                } else {
                    MovieModel movieModel = utilsService.getMovie(utilsService.clearFormat(linkModel.getName()));
                    if (movieModel != null) {
                        voidBeginTime(list, zmzId, movieModel.getSeason(), now);
                    }
                }
            }
        }
    }

    private void voidBeginTime(List list, Integer theZmzId, Integer theSeason, LocalDateTime theNow) {
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            String beginTime = objects[1].toString();
            Integer fromZMZId = Integer.parseInt(objects[2].toString());
            Integer season = Integer.parseInt(objects[3].toString());
            String movieName = objects[4].toString();

            boolean doed = false;
            if (theSeason > 0) {
                if (theZmzId.equals(fromZMZId) && theSeason.equals(season)) {
                    doed = true;
                }
            } else if (theZmzId.equals(fromZMZId)) {
                doed = true;
            }

            if (doed) {
                if (theNow.getDayOfWeek().getValue() != LocalDateTimeUtils.convert(beginTime).getDayOfWeek().getValue()) {
                    log.info(id + " " + movieName + " begintime error");
                    //开始校验并修订
                    if (theNow.getDayOfWeek().getValue() == LocalDateTimeUtils.convert(beginTime).plusDays(1).getDayOfWeek().getValue()) {
                        addOnArticleRepository.updateBeginTime(id, LocalDateTimeUtils.convert(LocalDateTimeUtils.convert(beginTime).plusDays(1)));
                        log.info(id + " update");
                    }
                }
            }

        }
    }

    private Map<String, List<LinkModel>> getZmzMovieList(Integer year, Integer month) {
        Map<String, List<LinkModel>> map = new HashMap<>();
        try {
            String u = "http://www.zimuzu.tv/tv/schedule/index/year/" + year + "/month/" + month;
            URL url = new URL(u);
            Source source = new Source(url);
            List<Element> list = source.getAllElementsByClass("ihbg");

            for (Element element : list) {
                String theDayContent = element.getContent().toString();
                String theDay = utilsService.getTagContent(theDayContent, "<dt>", "</dt>").split(" ")[0].replace("号", "");

                map.put(theDay, utilsService.getHrefs(theDayContent));
            }

        } catch (Exception e) {

        }
        return map;
    }

    /**
     * 处理图片不对的 临时处理
     */
    @Transactional
    public void tempDo() {
//        log.info("tempDo start");
//        List<Integer> listIds = new ArrayList<>();
//        String ids = "326,329,331,343,349,350,356,360,365,369,378,383,385,388,391,392,399,408,424,433,445,448,456,472,474,476,479,481,483,487,489,490,502,503,512,513,521,524,541,546,557,573,578,579,582,585,590,592,594,598,600,601,608,609,621,624,625,636,642,651,653,658,660,663,669,678,685,690,694,708,713,716,719,721,730,737,738,748,759,764,768,771,772,777,782,783,791,795,799,815,824,831,837,844,847,849,850,852,860,873,874,877,879,883,884,895,896,899,901,903,909,911,923,928,930,934,938,964,983,989,990,991,993,1011,1013,1016,1018,1021,1028,1034,1037,1053,1055,1059,1063,1066,1067,1073,1077,1078,1087,1097,1098,1103,1117,1118,1125,1130,1132,1136,1141,1149,1155,1163,1164,1166,1169,1174,1180,1203,1209,1214,1216,1218,1223,1229,1232,1239,1242,1243,1246,1258,1262,1264,1271,1277,1282,1284,1285,1298,1304,1306,1311,1312,1319,1323,1329,1333,1345,1356,1357,1358,1363,1386,1387,1394,1395,1396,1406,1408,1409,1414,1421,1424,1425,1429,1432,1435,1437,1438,1441,1443,1445,1447,1455,1457,1464,1472,1492,1498,1499,1501,1503,1506,1513,1520,1531,1539,1545,1547,1552,1553,1562,1563,1573,1579,1580,1590,1593,1595,1601,1608,1611,1617,1618,1623,1629,1630,1640,1645,1655,1664,1667,1671,1674,1682,1685,1686,1693,1710,1715,1716,1719,1720,1721,1723,1726,1727,1728,1729,1730,1740,1743,1751,1758,1766,1778,1782,1787,1818,1820,1822,1824,1826,1828,1830,1832,1834,1835,1838,1844,1851,1871,1875,1878,1880,1887,1889";
//        for (String item : ids.split(",")) {
//            listIds.add(Integer.parseInt(item));
//        }
//        List<AddOnArticle> finds = addOnArticleRepository.findById(listIds);
//        for (AddOnArticle addOnArticle : finds) {
//            String pictureUrl = utilsService.getPicture(addOnArticle.getBody());
//            if (!StringUtils.isEmpty(pictureUrl)) {
////                addOnArticle.setPictureUrl("<img alt=\"" + addOnArticle.getMovieName() + "\" src=\"" + pictureUrl + "\" />");
////                addOnArticleRepository.save(addOnArticle);
//
////                log.info(addOnArticle.getAid() + " do");
//            } else {
//                log.error(addOnArticle.getAid() + " " + addOnArticle.getMovieName());
//            }
//        }
//
//        log.info("start batchEditForPicture");
//        WebDriver driver = webDriverService.webDriverGenerator();
//        utilsService.getWebDriver(driver);
//        for (Integer i : listIds) {
//            try {
//                editOneOper(i, driver);
//            } catch (Exception e) {
//                log.info(i + " error");
//            }
//        }
//        log.info("end batchEditForPicture");
//        driver.quit();
//
//
//        log.info("tempDo finished");

        log.info("tempDo start");
        List list = addOnArticleRepository.findListAbountDB();
        log.info(list.size());
        for (Object obj : list) {
            Object[] objects = (Object[]) obj;
            Integer id = Integer.parseInt(objects[0].toString());
            Integer fromDBId = Integer.parseInt(objects[1].toString());
            DouBanModel douBanModel = douBanService.get(fromDBId);
            if (douBanModel != null) {
                if (!StringUtils.isEmpty(douBanModel.getImdb()) || douBanModel.getMaxCollection() > 0) {
                    AddOnArticle addOnArticle = addOnArticleRepository.findOne(id);
                    if (!StringUtils.isEmpty(douBanModel.getImdb())) addOnArticle.setImdb(douBanModel.getImdb());
                    if (douBanModel.getMaxCollection() > 0)
                        addOnArticle.setMaxCollection(douBanModel.getMaxCollection());
                    addOnArticleRepository.save(addOnArticle);
                    log.info(id + " do");

                }
            }
        }
        log.info("tempDo finished");
    }
}

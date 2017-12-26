package com.lgh.pull.service.impl;

import com.alibaba.fastjson.JSON;
import com.lgh.pull.entity.SourceArticleFive;
import com.lgh.pull.model.*;
import com.lgh.pull.service.UtilsService;
import com.lgh.pull.service.WebDriverService;
import com.lgh.pull.service.ZmzService;
import com.lgh.pull.utils.HttpHelper;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ZmzServiceImpl implements ZmzService {

    Log log = LogFactory.getLog(ZmzServiceImpl.class);

    @Autowired
    private UtilsService utilsService;

    @Autowired
    private Environment env;

    @Autowired
    private WebDriverService webDriverService;

    public SourceArticleFive getOneMovie(WebDriver driver, Integer zmzId, int season) throws ParseException, IOException {
        SourceArticleFive sourceArticleThree = new SourceArticleFive();

        String mainInfoUrl = "http://www.zimuzu.tv/gresource/" + zmzId;

        getOneMovieMainInfo(zmzId, sourceArticleThree);

        String theSeason = season > 0 ? "第" + season + "季" : "";
        String downloadUrl = "";
        //没有driver调用优化的
        if (driver == null) {
            DownloadUrlModel downloadUrlModel = getOneMovieDownloadUrlSmart(zmzId, season);
            if (downloadUrlModel != null) downloadUrl = downloadUrlModel.getViewHtml();
        } else {
            downloadUrl = getOneMovieDownloadUrl(driver, zmzId, theSeason);
        }
//        sourceArticleThree.setContent(sourceArticleThree.getContent() + movie_downloadInfo);
        sourceArticleThree.setDownUrls(downloadUrl);

        if (sourceArticleThree.getBeginTime() != null && sourceArticleThree.getBeginTime().length() >= 10) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(sourceArticleThree.getBeginTime().substring(0, 10));
            Long time = date.getTime() / 1000;
            sourceArticleThree.setUpdateTime(time);
        }

        if (sourceArticleThree.getUpdateTime() == null) sourceArticleThree.setUpdateTime(0L);

        if (!StringUtils.isEmpty(sourceArticleThree.getType())) {
            sourceArticleThree.setKeywords(sourceArticleThree.getType().replace("/", ","));
            if (sourceArticleThree.getBeginTime() != null && sourceArticleThree.getBeginTime().length() >= 4) {
                sourceArticleThree.setKeywords(sourceArticleThree.getKeywords() + "," + sourceArticleThree.getBeginTime().substring(0, 4));
            }
            if (!StringUtils.isEmpty(sourceArticleThree.getCorp())) {
                sourceArticleThree.setKeywords(sourceArticleThree.getKeywords() + "," + sourceArticleThree.getCorp());
            }

            if (!StringUtils.isEmpty(sourceArticleThree.getEnglishName())) {
                sourceArticleThree.setKeywords(sourceArticleThree.getKeywords() + "," + sourceArticleThree.getEnglishName());
            }
            if (!StringUtils.isEmpty(sourceArticleThree.getShortTitle())) {
                sourceArticleThree.setKeywords(sourceArticleThree.getKeywords() + "," + sourceArticleThree.getShortTitle() + theSeason);
            }


            if (StringUtils.isEmpty(sourceArticleThree.getKeywords())) {
                sourceArticleThree.setKeywords("其他");
            }
        }

        sourceArticleThree.setTitle(sourceArticleThree.getShortTitle() + theSeason + "/全集" + sourceArticleThree.getEnglishName() + "迅雷下载");

        sourceArticleThree.setFromId(zmzId.toString());
        sourceArticleThree.setFromUrl(mainInfoUrl);

        //pingfen
        sourceArticleThree.setPingfen(getScore(zmzId));

        return sourceArticleThree;
    }

    public void getOneMovieMainInfo(Integer zmzId, SourceArticleFive sourceArticleThree) throws IOException {
//        driver.get(url);
//        String html = driver.getPageSource().toString();
        URL html = new URL("http://www.zimuzu.tv/gresource/" + zmzId);
        Source source = new Source(html);
        //title
        Element title = source.getFirstElementByClass("resource-tit");
        sourceArticleThree.setShortTitle("");
        if (title != null) {
            Pattern pattern = Pattern.compile("《(.*?)》");
            Matcher matcher = pattern.matcher(title.getContent().toString());
            if (matcher.find()) {
                sourceArticleThree.setShortTitle(matcher.group(1).trim());
            }

            pattern = Pattern.compile("【(.*?)】");
            matcher = pattern.matcher(title.getContent().toString());
            if (matcher.find()) {
                sourceArticleThree.setMainType(matcher.group(1).trim());
            }
        }


        //summary
        List<Element> intro = source.getAllElementsByClass("con");
        sourceArticleThree.setSummary("");
        if (intro != null && intro.size() >= 2) {
            String info_intro = intro.get(1).getContent().toString();
            sourceArticleThree.setSummary(utilsService.removeHref(info_intro));
        }


        //imgurl
        Element imglink = source.getFirstElementByClass("imglink");
        sourceArticleThree.setPictureUrl("");
        if (imglink != null) {
            String info_imageUrl = getImageUrl(imglink.getContent().toString());
            sourceArticleThree.setPictureUrl(info_imageUrl);
        }

//        String info_intro2 = "";
        Element intro2 = source.getFirstElementByClass("fl-info");
        sourceArticleThree.setEnglishName("");
        sourceArticleThree.setAreaName("");
        sourceArticleThree.setLanguage("");
        sourceArticleThree.setBeginTime("");
        sourceArticleThree.setCorp("");
        sourceArticleThree.setCorp("");
        sourceArticleThree.setType("");
        sourceArticleThree.setImdb("");
        sourceArticleThree.setAlias("");
        sourceArticleThree.setAuthor("");
        sourceArticleThree.setDirector("");
        sourceArticleThree.setZhuYan("");
        if (intro2 != null) {
            List<String> listLi = utilsService.getLiList(intro2.getContent().toString());
            for (String li : listLi) {
                String line = new Source(li).getTextExtractor().toString();

                if (line.startsWith("原名：")) {
                    sourceArticleThree.setEnglishName(line.substring(3).trim());
                } else if (line.startsWith("地区：")) {
                    sourceArticleThree.setAreaName(line.substring(3).trim());
                } else if (line.startsWith("语 言：")) {
                    sourceArticleThree.setLanguage(line.substring(4).trim());
                } else if (line.startsWith("首播：")) {
                    sourceArticleThree.setBeginTime(line.substring(3).length() >= 10 ? line.substring(3, 13).trim() : "");
                } else if (line.startsWith("制作公司：")) {
                    sourceArticleThree.setCorp(line.substring(5).trim());
                } else if (line.startsWith("电视台：")) {
                    sourceArticleThree.setCorp(line.substring(4).trim());
                } else if (line.startsWith("类型：")) {
                    sourceArticleThree.setType(line.substring(3).trim());
                } else if (line.startsWith("IMDB：")) {
                    sourceArticleThree.setImdb(line.substring(5).split(" ")[0].trim());
                } else if (line.startsWith("别名：")) {
                    sourceArticleThree.setAlias(line.substring(3).trim());
                } else if (line.startsWith("編劇：")) {
                    sourceArticleThree.setAuthor(line.substring(3).trim());
                } else if (line.startsWith("导演：")) {
                    sourceArticleThree.setDirector(line.substring(3).trim());
                } else if (line.startsWith("主演：")) {
                    sourceArticleThree.setZhuYan(line.substring(3).trim());
                }


                //翻译：IMDB：
//                if (!line.startsWith("翻译：") && !line.startsWith("制作周期")) {
//                    if (line.startsWith("IMDB：")) {
//                        if (sourceArticleThree.getImdb().length() == 0) {
//                            info_intro2 += "IMDB：</br>";
//                        } else if (line.length() > sourceArticleThree.getImdb().length() + 5)
//                            info_intro2 += "IMDB：<a href=\"" + sourceArticleThree.getImdb() + "\">" + sourceArticleThree.getImdb() + "</a>" + line.substring(sourceArticleThree.getImdb().length() + 5) + "</br>";
//                        else if (line.length() == sourceArticleThree.getImdb().length() + 5)
//                            info_intro2 += "IMDB：<a href=\"" + line.substring(5) + "\">" + line.substring(5) + "</a><br/>";
//                    } else {
//                        info_intro2 += line + "<br/>";
//                    }
//                }
            }
        }
//        sourceArticleThree.setContent("<p>" + sourceArticleThree.getSummary() + "</p>" + "<p><img src=\"" + sourceArticleThree.getPictureUrl() + "\" /></p>" + "<p>" + info_intro2 + "</p>");
    }


    /**
     * 获得下载地址信息
     *
     * @param driver
     * @param theSeason 只获取当前季
     * @return
     */
    public String getOneMovieDownloadUrl(WebDriver driver, Integer zmzId, String theSeason) {
        theSeason = utilsService.getZmzSeasonName(theSeason);

        driver.get("http://www.zimuzu.tv/resource/list/" + zmzId);
        String result = "";
        String html = driver.getPageSource();
        Source source = new Source(html);
        Element tab = source.getFirstElementByClass("media-tab");
        if (tab != null) {
            // log.info(url + " have tab so go over");
//            Map<String, Object> mapJi = new HashMap();
            //获得第几季
            for (Element season : tab.getAllElements()) {
                String sessionValue = season.getAttributeValue("season");
                if (!StringUtils.isEmpty(sessionValue)) {
                    result += "<h3>" + season.getTextExtractor().toString() + "</h3>";
                    String theSeasonDownloadUrl = "";
                    List<Element> list = source.getAllElementsByClass("media-list");
                    for (Element element : list) {
                        theSeasonDownloadUrl += getDownloadUrlItemInfo(element.getContent().toString(), sessionValue);
                    }
                    result += theSeasonDownloadUrl;

                    if (!StringUtils.isEmpty(theSeason) && theSeason.equals(season.getTextExtractor().toString())) {
                        result = theSeasonDownloadUrl;
                        break;
                    } else if (!StringUtils.isEmpty(theSeason)) {
                        result = "";
                    }
                }
            }
        } else {
            List<Element> list = source.getAllElementsByClass("media-list");
            for (Element element : list) {
                result += getDownloadUrlItemInfo(element.getContent().toString(), "");
            }
        }
        return result;
    }

    private String getDownloadUrlItemInfo(String item, String sessionValue) {
        String html = "";
        Source sourceItem = new Source(item);
        Element title = sourceItem.getFirstElementByClass("it");
        if (title != null) {
            String itemTitle = title.getTextExtractor().toString();
//            if (!itemTitle.equals("离线+在线")) {
            String pageHtml = "";
            List<Element> elements = sourceItem.getAllElementsByClass("clearfix");
            for (Element element : elements) {
                if (StringUtils.isEmpty(sessionValue) || (!StringUtils.isEmpty(sessionValue) && element.getAttributeValue("season").equals(sessionValue))) {
                    String page = element.getContent().toString();
                    Source sourcePage = new Source(page);
                    if (itemTitle.equals("离线+在线")) {
                        String pageTitle = sourcePage.getFirstElementByClass("app_fl") != null ? sourcePage.getFirstElementByClass("app_fl").getTextExtractor().toString() : "";

                        Element element1 = sourcePage.getFirstElementByClass("flt");
                        String pageUrl = getPageHref(element1.getContent().toString());
                        if (!StringUtils.isEmpty(pageUrl))
                            pageHtml += "<li>" + pageTitle.replace("-人人影视", "") + pageUrl + "</li>";
                    } else {
                        String pageTitle = sourcePage.getFirstElementByClass("f7") != null ? sourcePage.getFirstElementByClass("f7").getTextExtractor().toString() : "";
                        String pageSize = sourcePage.getFirstElementByClass("f3") != null ? sourcePage.getFirstElementByClass("f3").getTextExtractor().toString() : "";

                        Element element1 = sourcePage.getFirstElementByClass("fr");
                        String pageUrl = getPageHref(element1.getContent().toString());
                        pageHtml += "<li>" + pageTitle.replace("-人人影视", "") + pageSize + pageUrl + "</li>";
                    }
                }
            }

            if (!StringUtils.isEmpty(pageHtml)) {
                if (itemTitle.equals("离线+在线")) {
                    if (pageHtml.indexOf("href=") > 0) {
                        html += "<h2>在线看</h2>";
                        html += "<ol>" + pageHtml + "</ol>";
                    }
                } else {
                    html += "<h2>" + itemTitle + "</h2>";
                    html += "<ol>" + pageHtml + "</ol>";
                }
            }
//            }
        }
        return html;
    }

    private String getPageHref(String text) {
        String result = "";
        Pattern pattern = Pattern.compile("<a(.*?)href=\"([^\\\"]+)\"(.*?)>(.*?)</a>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            boolean ok = false;
            if ((matcher.group(4).equals("网盘") || matcher.group(4).equals("百度云") || matcher.group(4).equals("微云"))
                    && (matcher.group(2).indexOf("pan.baidu.com") >= 0 || matcher.group(2).indexOf("url.cn") >= 0 || matcher.group(2).indexOf(".weiyun.com") >= 0)) {
                ok = true;
            } else if (matcher.group(4).equals("迅雷-磁力") && matcher.group(2).startsWith("magnet:?")) {
                ok = true;
            } else if (matcher.group(4).equals("迅雷-电驴") && matcher.group(2).startsWith("ed2k://")) {
                ok = true;
            }
            if (ok) result += " <a href=\"" + matcher.group(2) + "\" target=\"_blank\">" + matcher.group(4) + "</a>";
        }
        return result;
    }

    private String getImageUrl(String text) {
        String result = "";
        Pattern pattern = Pattern.compile("<img(.*?)src=\"([^\\\"]+)\"(.*?)>");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            result = matcher.group(2);
        }
        return result;
    }


    /**
     * 从字幕组获取数据
     *
     * @param zmzId
     * @param cookies
     * @return
     */
    public SourceArticleFive getZMZData(Integer zmzId, String cookies, Integer season) {
        SourceArticleFive sourceArticleFour = null;
        if (!StringUtils.isEmpty(cookies)) {
            WebDriver driver = null;
            try {
                driver = utilsService.getZMZWebDriver(cookies);
                sourceArticleFour = getOneMovie(driver, zmzId, season);
                driver.quit();
            } catch (Exception e) {
                if (driver != null) driver.quit();
            }
        } else {
            try {
                sourceArticleFour = getOneMovie(null, zmzId, season);
            } catch (Exception e) {
            }
        }

        return sourceArticleFour;
    }

    public SourceArticleFive getZMZData(Integer zmzId, WebDriver driver, Integer season) {
        SourceArticleFive sourceArticleFour = null;
        try {
            sourceArticleFour = getOneMovie(driver, zmzId, season);

        } catch (Exception e) {
            log.error("fail", e);
        }
        return sourceArticleFour;
    }

    public Float getScore(Integer zmzId) {
        try {
            String response = HttpHelper.sendGet("http://www.zimuzu.tv/resource/getScore", "rid=" + zmzId);
            ScoreModel scoreModel = JSON.parseObject(response, ScoreModel.class);
            return scoreModel.getScore() == 10 ? 0 : scoreModel.getScore();
        } catch (Exception e) {
            log.error("getScore zmzId " + zmzId);
            return 0F;
        }
    }

    public PlayStatusModel getStatus(Integer zmzId) {
        try {
            URL url = new URL("http://www.zimuzu.tv/resource/index_json/rid/" + zmzId + "/channel/tv");
            Source source = new Source(url);
            PlayStatusModel model = JSON.parseObject(source.toString().replace("var index_info=", ""), PlayStatusModel.class);
            return model;
        } catch (Exception e) {
            log.error("getStatus zmzId " + zmzId);
        }
        return null;
    }

    public String getResourceUrl(Integer zmzId) {
        PlayStatusModel resource = getStatus(zmzId);
        if (resource != null) {
            List<LinkModel> linkModels = utilsService.getHrefs(resource.getResource_content());
            if (linkModels.size() > 0) return linkModels.get(0).getUrl();
        }
        return "";
    }

    /**
     * 转为前台显示
     *
     * @param list
     * @return
     */
    private String toView(List<ZmzMovieSeasonModel> list) {
        StringBuilder result = new StringBuilder();

        for (ZmzMovieSeasonModel seasonModel : list) {

            if (seasonModel.getFormaters() != null && seasonModel.getFormaters().size() > 0) {
                if (!StringUtils.isEmpty(seasonModel.getSeasonName())) {
                    result.append("<h3>" + utilsService.toZHSeasonName(seasonModel.getSeasonName()) + "</h3>");
                }
                for (ZmzMovieFormatterModel formatterModel : seasonModel.getFormaters()) {

                    if (formatterModel.getEpisondes() != null && formatterModel.getEpisondes().size() > 0) {
                        String formatterName = formatterModel.getFormatterName();
                        //在线看
                        //if (formatterName.endsWith("在线看")) formatterName = "网盘+在线";

                        result.append("<h2>" + formatterName + "</h2><ol>");
                        //集反转
                        try {
                            formatterModel.getEpisondes().sort((h1, h2) -> h1.getEpisode().compareTo(h2.getEpisode()));
                        } catch (Exception ex) {
                            //集为空不用反转
                        }
                        for (ZmzMovieEpisodeModel episodeModel : formatterModel.getEpisondes()) {

                            //该集的链接地址 （集的链接地址处理）
                            StringBuilder strBLinkUrls = new StringBuilder();
                            for (ZmzMovieDownLinkModel downLinkModel : episodeModel.getDownLinks()) {
                                //迅雷 电驴 磁力 网盘 百度云 微云
                                String linkName = downLinkModel.getName();
                                String linkUrl = downLinkModel.getUrl();

                                //|| linkUrl.indexOf(".weiyun.com") >= 0  || linkName.endsWith("微云")
                                if (linkName.endsWith("电驴") || linkName.endsWith("磁力") || linkName.endsWith("网盘")
                                        || linkName.endsWith("百度云") || linkName.endsWith("微云")) {
                                    if (linkUrl.indexOf("pan.baidu.com") >= 0 || linkUrl.indexOf("url.cn") >= 0
                                            || linkUrl.startsWith("magnet:?") || linkUrl.startsWith("ed2k://") || linkUrl.indexOf(".weiyun.com") >= 0) {
                                        if (linkName.endsWith("电驴")) linkName = "迅雷-电驴";
                                        if (linkName.endsWith("磁力")) linkName = "迅雷-磁力";
                                        strBLinkUrls.append(" <a href=\"" + downLinkModel.getUrl() + "\" target=\"_blank\">" + linkName + "</a>");
                                    }
                                }
                            }

                            if (!StringUtils.isEmpty(strBLinkUrls.toString())) {
                                result.append("<li>");
                                if (formatterName.endsWith("在线看")) {
                                    if (!StringUtils.isEmpty(episodeModel.getEpisode()) && !episodeModel.getEpisode().equals("0")) {
                                        result.append("第" + episodeModel.getEpisode() + "集");
                                    }
                                } else {
                                    result.append(episodeModel.getFilename()
                                            .replace("&YYeTs", "")
                                            .replace("%26YYeTs", "")
                                            .replace("人人影视", "")
                                            + (!StringUtils.isEmpty(episodeModel.getFilesize()) && !episodeModel.getFilesize().endsWith("0") ? "(" + episodeModel.getFilesize() + ")" : ""));
                                }
                                result.append(strBLinkUrls.toString());
                                result.append("</li>");
                            }
                        }
                        result.append("</ol>");
                    }
                }
            }
        }

        return result.toString();
    }

    /**
     * 不支持电影下载地址
     *
     * @param zmzId
     * @param season
     * @return
     * @throws IOException
     */
    public DownloadUrlModel getOneMovieDownloadUrlSmart(Integer zmzId, int season) {
        String link = getResourceUrl(zmzId);
//        log.info("getResourceUrl");
        if (StringUtils.isEmpty(link) || link.indexOf("xiazai") < 0) {
            log.error("address is error " + link);
//            log.info("start login mode down");
//            //按登录方式抓取
//            WebDriver driver = null;
//            try {
//                String cookies = env.getProperty("cookies");
//                driver = utilsService.getZMZWebDriver(cookies);
//                String downurls = getOneMovieDownloadUrl(driver, zmzId, season == 0 ? "" : "第" + season + "季");
//                DownloadUrlModel downloadUrlModel = new DownloadUrlModel();
//                downloadUrlModel.setSourceHtml(downurls);
//                downloadUrlModel.setViewHtml(downurls);
//                return downloadUrlModel;
//            } catch (Exception e) {
//                if (driver != null) driver.quit();
//            }
            return null;
        }


        WebDriver driver = webDriverService.webDriverGenerator();
        driver.get(link);

        Source source = new Source(driver.getPageSource().toString());
        driver.quit();
//        URL url;
//        Source source;
//        try {
//            url = new URL(link);
//            source = new Source(url);
//        } catch (IOException e) {
//            return null;
//        }


        Element seasons = source.getElementById("menu");
        ZmzMovieModel zmzMovieModel = new ZmzMovieModel();
        List<ZmzMovieSeasonModel> zmzMovieSeasonModels = new ArrayList<>();
        if (seasons == null) {
            //movie


            Element elementTab = source.getFirstElementByClass("tab-side");
            for (Element element : elementTab.getAllElements()) {
                if (!StringUtils.isEmpty(element.getAttributeValue("href"))
                        && element.getAttributeValue("href").startsWith("#sidetab-")) {
                    ZmzMovieSeasonModel zmzMovieSeasonModel = new ZmzMovieSeasonModel();
                    zmzMovieSeasonModel.setSeasonId(element.getAttributeValue("href").replace("#", ""));
                    zmzMovieSeasonModel.setSeasonName(element.getContent().toString().replace("正片", ""));
                    zmzMovieSeasonModel.setSeason(0);
                    //formatter
                    zmzMovieSeasonModel.setFormaters(getFormater(source, zmzMovieSeasonModel.getSeasonId()));

                    zmzMovieSeasonModels.add(zmzMovieSeasonModel);
                }
            }
            zmzMovieModel.setSeasons(zmzMovieSeasonModels);
        } else {
            for (Element element : seasons.getAllElements()) {
                if (!StringUtils.isEmpty(element.getAttributeValue("href"))
                        && element.getAttributeValue("href").startsWith("#sidetab-")) {
                    ZmzMovieSeasonModel zmzMovieSeasonModel = new ZmzMovieSeasonModel();
                    zmzMovieSeasonModel.setSeasonId(element.getAttributeValue("href").replace("#", ""));
                    zmzMovieSeasonModel.setSeasonName(element.getContent().toString());
                    //有点是迷你剧 没有季
                    if (zmzMovieSeasonModel.getSeasonName().indexOf("季") >= 0)
                        zmzMovieSeasonModel.setSeason(Integer.parseInt(zmzMovieSeasonModel.getSeasonName().replace("季", "").replace("第", "")));
                    else
                        zmzMovieSeasonModel.setSeason(0);

                    //formatter
                    zmzMovieSeasonModel.setFormaters(getFormater(source, zmzMovieSeasonModel.getSeasonId()));

                    zmzMovieSeasonModels.add(zmzMovieSeasonModel);
                }
            }
            zmzMovieModel.setSeasons(zmzMovieSeasonModels);
        }
//        log.info("explain Url");

        //指定具体季
        if (season > 0) {
            ZmzMovieSeasonModel currentSeasion = new ZmzMovieSeasonModel();
            for (ZmzMovieSeasonModel zmzMovieSeasonModel : zmzMovieModel.getSeasons()) {
                if (zmzMovieSeasonModel.getSeason() == season) {
                    currentSeasion = zmzMovieSeasonModel;
                    break;
                }
            }

            List<ZmzMovieSeasonModel> seasonModels = new ArrayList<>();
            seasonModels.add(currentSeasion);
            DownloadUrlModel downloadUrlModel = new DownloadUrlModel();
            downloadUrlModel.setViewHtml(toView(seasonModels));
            downloadUrlModel.setSourceHtml(JSON.toJSONString(seasonModels));
//            log.info("toView");
            return downloadUrlModel;
        }

        DownloadUrlModel downloadUrlModel = new DownloadUrlModel();
        downloadUrlModel.setViewHtml(toView(zmzMovieModel.getSeasons()));
        downloadUrlModel.setSourceHtml(JSON.toJSONString(zmzMovieModel.getSeasons()));
        return downloadUrlModel;
    }

    private List<ZmzMovieFormatterModel> getFormater(Source source, String seasonId) {
        List<ZmzMovieFormatterModel> list = new ArrayList<>();
        Element formater = source.getElementById(seasonId);
        if (formater != null && formater.getFirstElementByClass("tab-header") != null) {
            for (Element element : formater.getFirstElementByClass("tab-header").getAllElements()) {
                if (!StringUtils.isEmpty(element.getAttributeValue("href"))) {
                    ZmzMovieFormatterModel zmzMovieFormatterModel = new ZmzMovieFormatterModel();
                    zmzMovieFormatterModel.setFormatterId(element.getAttributeValue("href").replace("#", ""));

                    String formatterName = element.getContent().toString();
                    Source source1 = new Source(formatterName);
                    if (source1.getFirstElementByClass("badge") != null)
                        zmzMovieFormatterModel.setSubName(source1.getFirstElementByClass("badge").getTextExtractor().toString());
                    else
                        zmzMovieFormatterModel.setSubName("");

                    zmzMovieFormatterModel.setFormatterName(utilsService.getText(source1.toString()));

                    if (!StringUtils.isEmpty(zmzMovieFormatterModel.getSubName()))
                        zmzMovieFormatterModel.setFormatterName(zmzMovieFormatterModel.getFormatterName().replace(zmzMovieFormatterModel.getSubName(), ""));


                    zmzMovieFormatterModel.setEpisondes(getEpisondes(source, zmzMovieFormatterModel.getFormatterId()));
                    list.add(zmzMovieFormatterModel);
                }
            }
        }
        return list;
    }

    private List<ZmzMovieEpisodeModel> getEpisondes(Source source, String formaterId) {
        List<ZmzMovieEpisodeModel> list = new ArrayList<>();
        if (source.getElementById(formaterId) != null && source.getElementById(formaterId).getFirstElementByClass("down-list") != null) {
            Element element = source.getElementById(formaterId).getFirstElementByClass("down-list");
            for (Element item : element.getChildElements()) {
                ZmzMovieEpisodeModel zmzMovieEpisodeModel = new ZmzMovieEpisodeModel();
                //标题部分
                Element elementTitle = item.getFirstElementByClass("title");
                if (elementTitle != null) {
                    Element episodeName = elementTitle.getFirstElementByClass("episode");
                    if (episodeName != null) {
                        zmzMovieEpisodeModel.setEpisodeName(episodeName.getContent().toString());
                        if (zmzMovieEpisodeModel.getEpisodeName().split(" ").length > 1)
                            zmzMovieEpisodeModel.setEpisode(Integer.parseInt(zmzMovieEpisodeModel.getEpisodeName().split(" ")[1].replace("第", "").replace("集", "")));
                    }
                    Element filename = elementTitle.getFirstElementByClass("filename");
                    if (filename != null)
                        zmzMovieEpisodeModel.setFilename(filename.getContent().toString());
                    Element filesize = elementTitle.getFirstElementByClass("filesize");
                    if (filesize != null)
                        zmzMovieEpisodeModel.setFilesize(filesize.getContent().toString());

                }

                //下载地址部分
                Element elementDownLink = item.getFirstElementByClass("down-links");
                if (elementDownLink != null) {
                    List<ZmzMovieDownLinkModel> zmzMovieDownLinkModels = new ArrayList<>();
                    for (Element element1 : elementDownLink.getChildElements()) {
                        List<LinkModel> linkModels = utilsService.getHrefs(element1.getContent().toString());
                        if (linkModels.size() > 0) {
                            ZmzMovieDownLinkModel zmzMovieDownLinkModel = new ZmzMovieDownLinkModel();
                            Source name = new Source(linkModels.get(0).getName());
                            zmzMovieDownLinkModel.setName(name.getTextExtractor().toString());
                            zmzMovieDownLinkModel.setUrl(linkModels.get(0).getUrl());
                            zmzMovieDownLinkModels.add(zmzMovieDownLinkModel);
                        }
//                        for (LinkModel linkModel : linkModels) {
//                            ZmzMovieDownLinkModel zmzMovieDownLinkModel = new ZmzMovieDownLinkModel();
//                            Source name = new Source(linkModel.getName());
//                            zmzMovieDownLinkModel.setName(name.getTextExtractor().toString());
//                            zmzMovieDownLinkModel.setUrl(linkModel.getUrl());
//                            zmzMovieDownLinkModels.add(zmzMovieDownLinkModel);
//                        }
                    }
                    zmzMovieEpisodeModel.setDownLinks(zmzMovieDownLinkModels);
                }

                list.add(zmzMovieEpisodeModel);
            }
        }
        return list;
    }
}

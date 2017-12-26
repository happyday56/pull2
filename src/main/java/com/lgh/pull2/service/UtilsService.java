package com.lgh.pull2.service;


import com.lgh.pull.model.LinkModel;
import com.lgh.pull.model.MovieModel;
import org.apache.http.cookie.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.List;

public interface UtilsService {
    List<LinkModel> getHrefs(String text);

    List<String> getHrefList(String text);

    List<String> getHrefNameList(String text);

    String findBRSearchItem(String text, String startTitleName);

    String findPSearchItem(String text, String startTitleName);

    String replaceBlank(String str);

    String replaceUtf8(String text);

    List<String> getMovieNameList(String text);

    /**
     * 获得季名 如第三季
     *
     * @param movieName
     * @return
     */
    String getSeasonName(String movieName);

    /**
     * 获得字幕组季名 如第21季
     *
     * @param movieName
     * @return
     */
    String getZmzSeasonName(String movieName);

    /**
     * 数字转汉字
     *
     * @param i
     * @return
     */
    String intToZH(int i);

    /**
     * 处理 第5季为第五季
     *
     * @param seasonName
     * @return
     */
    String toZHSeasonName(String seasonName);

    /**
     * 获得季 数字的
     *
     * @param movieName
     * @return
     */
    Integer getSeason(String movieName);

    WebDriver getZMZWebDriver(String cookies);

    WebDriver getZMZWebDriver();

    WebDriver getZMZWebDriverByUser();

    WebDriver getWebDriver(WebDriver webDriver);

    void addAloneCookie(Cookie cookie, WebDriver driver);

    List<String> getLiList(String text);

    String clearFormat(String body);

    String getText(String html);

    String findDivSearchItem(String text, String startTitleName);

    String getPicture(String text);

    String removeHref(String text);

    String getTagContent(String text, String startTag, String endTag);

    String getEpisode(String t);

    MovieModel getMovie(String t);
}

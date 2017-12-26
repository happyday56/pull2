package com.lgh.pull2.service.impl;

import com.lgh.pull.model.LinkModel;
import com.lgh.pull.model.MovieModel;
import com.lgh.pull.service.UtilsService;
import com.lgh.pull.service.WebDriverService;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UtilsServiceImpl implements UtilsService {

    @Autowired
    private WebDriverService webDriverService;

    @Autowired
    private Environment env;

    public List<LinkModel> getHrefs(String text) {
        text = replaceBlank(text);
        List<LinkModel> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("<a(.*?)href=\"([^\\\"]+)\"(.*?)>(.*?)</a>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            list.add(new LinkModel(matcher.group(2), matcher.group(4)));
        }
        return list;
    }

    /**
     * 获取所有连接
     *
     * @param text
     * @return
     */
    public List<String> getHrefList(String text) {
        text = replaceBlank(text);
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("<a(.*?)href=\"([^\\\"]+)\"(.*?)>(.*?)</a>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            list.add(matcher.group(2));
        }
        return list;
    }

    /**
     * 获取所有连接名称
     *
     * @param text
     * @return
     */
    public List<String> getHrefNameList(String text) {
        text = replaceBlank(text);
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("<a(.*?)href=\"([^\\\"]+)\"(.*?)>(.*?)</a>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            list.add(matcher.group(4));
        }
        return list;
    }

    /**
     * 获取《》内的内容
     *
     * @param text
     * @return
     */
    public List<String> getMovieNameList(String text) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("《(.*?)》", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    public String findBRSearchItem(String text, String startTitleName) {
        String result = "";
        Pattern pattern = Pattern.compile(startTitleName + "(.*?)<br", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result = matcher.group(1);
            break;
        }
        return result;
    }

    public String findPSearchItem(String text, String startTitleName) {
        String result = "";
        Pattern pattern = Pattern.compile(startTitleName + "(.*?)</p>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result = matcher.group(1);
            break;
        }
        if (StringUtils.isEmpty(result)) {
            pattern = Pattern.compile(startTitleName + "(.*?)</div>", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(text);
            while (matcher.find()) {
                result = matcher.group(1);
                break;
            }
            return result;
        }
        return result;
    }

    /**
     * java去除字符串中的回车、换行符、制表符
     *
     * @param str
     * @return
     */
    public String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public String replaceUtf8(String text) {
        int[] input = new int[]{0x7f, 0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a,
                0x8b, 0x8c, 0x8d, 0x8e, 0x8f, 0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99,
                0x9a, 0x9b, 0x9c, 0x9d, 0x9e, 0x9f, 0xad, 0x483, 0x484, 0x485, 0x486, 0x487, 0x488, 0x489,
                0x559, 0x55a, 0x58a, 0x591, 0x592, 0x593, 0x594, 0x595, 0x596, 0x597, 0x598, 0x599, 0x59a,
                0x59b, 0x59c, 0x59d, 0x59e, 0x59f, 0x5a0, 0x5a1, 0x5a2, 0x5a3, 0x5a4, 0x5a5, 0x5a6, 0x5a7,
                0x5a8, 0x5a9, 0x5aa, 0x5ab, 0x5ac, 0x5ad, 0x5ae, 0x5af, 0x5b0, 0x5b1, 0x5b2, 0x5b3, 0x5b4,
                0x5b5, 0x5b6, 0x5b7, 0x5b8, 0x5b9, 0x5ba, 0x5bb, 0x5bc, 0x5bd, 0x5bf, 0x5c1, 0x5c2, 0x5c4,
                0x5c5, 0x5c6, 0x5c7, 0x606, 0x607, 0x608, 0x609, 0x60a, 0x63b, 0x63c, 0x63d, 0x63e, 0x63f,
                0x674, 0x6e5, 0x6e6, 0x70f, 0x76e, 0x76f, 0x770, 0x771, 0x772, 0x773, 0x774, 0x775, 0x776,
                0x777, 0x778, 0x779, 0x77a, 0x77b, 0x77c, 0x77d, 0x77e, 0x77f, 0xa51, 0xa75, 0xb44, 0xb62,
                0xb63, 0xc62, 0xc63, 0xce2, 0xce3, 0xd62, 0xd63, 0x135f, 0x200b, 0x200c, 0x200d, 0x200e,
                0x200f, 0x2028, 0x2029, 0x202a, 0x202b, 0x202c, 0x202d, 0x202e, 0x2044, 0x2071, 0xf701,
                0xf702, 0xf703, 0xf704, 0xf705, 0xf706, 0xf707, 0xf708, 0xf709, 0xf70a, 0xf70b, 0xf70c,
                0xf70d, 0xf70e, 0xf710, 0xf711, 0xf712, 0xf713, 0xf714, 0xf715, 0xf716, 0xf717, 0xf718,
                0xf719, 0xf71a, 0xfb1e, 0xfc5e, 0xfc5f, 0xfc60, 0xfc61, 0xfc62, 0xfeff, 0xfffc};
        StringBuilder b = new StringBuilder();
        int lastContinuous = -1;
        int span = 0;
        for (int i = 0; i < input.length; i++) {
            if (lastContinuous == -1 && i < input.length - 1 && input[i] + 1 == input[i + 1]) {
                lastContinuous = input[i];
                span = 1;
            } else {
                if (input[i] == lastContinuous + span) {
                    span++;
                } else if (lastContinuous != -1) {
                    if (b.length() > 0)
                        b.append("|");
                    b.append(String.format("[\\u%s-\\u%s]", zerolize(Integer.toHexString(lastContinuous)),
                            zerolize(Integer.toHexString(lastContinuous + span - 1))));
                    span = 0;
                    lastContinuous = -1;
                    i--;
                } else {
                    b.append("|\\u" + zerolize(Integer.toHexString(input[i])));
                }
            }
        }
        if (lastContinuous != -1) {
            if (b.length() > 0)
                b.append("|");
            b.append(String.format("[\\u%s-\\u%s]", zerolize(Integer.toHexString(lastContinuous)),
                    zerolize(Integer.toHexString(lastContinuous + span - 1))));
        }
        System.out.println(b.toString());
        Pattern pattern = Pattern.compile(b.toString());
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll("");
    }

    private static String zerolize(String s) {
        if (s.length() < 4) {
            s = "000".substring(0, 4 - s.length()) + s;
        }
        return s;
    }

    /**
     * 获得季名 如第三季
     *
     * @param movieName
     * @return
     */
    public String getSeasonName(String movieName) {
        if (movieName.indexOf("第") >= 0) {
            return movieName.substring(movieName.indexOf("第"));
        }
        return "";
    }

    /**
     * 获得字幕组季名 如第21季
     *
     * @param movieName
     * @return
     */
    public String getZmzSeasonName(String movieName) {
        String sessionName = getSeasonName(movieName);
        sessionName = sessionName.replace("二十", "2");
        sessionName = sessionName.replace("三十", "3");
        sessionName = sessionName.replace("四十", "4");
        sessionName = sessionName.replace("五十", "5");
        sessionName = sessionName.replace("六十", "6");
        sessionName = sessionName.replace("七十", "7");
        sessionName = sessionName.replace("八十", "8");
        sessionName = sessionName.replace("九十", "9");
        sessionName = sessionName.replace("十一", "11");
        sessionName = sessionName.replace("十二", "12");
        sessionName = sessionName.replace("十三", "13");
        sessionName = sessionName.replace("十四", "14");
        sessionName = sessionName.replace("十五", "15");
        sessionName = sessionName.replace("十六", "16");
        sessionName = sessionName.replace("十七", "17");
        sessionName = sessionName.replace("十八", "18");
        sessionName = sessionName.replace("十九", "19");
        sessionName = sessionName.replace("十", "10");
        sessionName = sessionName.replace("一", "1");
        sessionName = sessionName.replace("二", "2");
        sessionName = sessionName.replace("三", "3");
        sessionName = sessionName.replace("四", "4");
        sessionName = sessionName.replace("五", "5");
        sessionName = sessionName.replace("六", "6");
        sessionName = sessionName.replace("七", "7");
        sessionName = sessionName.replace("八", "8");
        sessionName = sessionName.replace("九", "9");
        return sessionName;
    }

    public String intToZH(int i) {
        String[] zh = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] unit = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十"};

        String str = "";
        StringBuffer sb = new StringBuffer(String.valueOf(i));
        sb = sb.reverse();
        int r = 0;
        int l = 0;
        for (int j = 0; j < sb.length(); j++) {
            /**
             * 当前数字
             */
            r = Integer.valueOf(sb.substring(j, j + 1));

            if (j != 0)
            /**
             * 上一个数字
             */
                l = Integer.valueOf(sb.substring(j - 1, j));

            if (j == 0) {
                if (r != 0 || sb.length() == 1)
                    str = zh[r];
                continue;
            }

            if (j == 1 || j == 2 || j == 3 || j == 5 || j == 6 || j == 7 || j == 9) {
                if (r != 0)
                    str = zh[r] + unit[j] + str;
                else if (l != 0)
                    str = zh[r] + str;
                continue;
            }

            if (j == 4 || j == 8) {
                str = unit[j] + str;
                if ((l != 0 && r == 0) || r != 0)
                    str = zh[r] + str;
                continue;
            }
        }
        return str.replace("一十", "十");
    }

    /**
     * 处理 第5季为第五季
     *
     * @param seasonName
     * @return
     */
    public String toZHSeasonName(String seasonName) {
        if (seasonName.startsWith("第") && seasonName.endsWith("季")) {
            return "第" + intToZH(Integer.parseInt(seasonName.replace("第", "")
                    .replace("季", "")))
                    + "季";

        } else {
            return seasonName;
        }
    }

    /**
     * 获得季 数字的
     *
     * @param movieName
     * @return
     */
    public Integer getSeason(String movieName) {
        String name = getZmzSeasonName(movieName)
                .replace("第", "")
                .replace("季", "");
        if (!StringUtils.isEmpty(name)) {
            if (name.indexOf("至") >= 0) return 0;
            try {
                return Integer.parseInt(name);
            } catch (Exception ex) {
                return 0;
            }
        }
        return 0;
    }

    public WebDriver getZMZWebDriver(String cookies) {
        WebDriver driver = webDriverService.webDriverGenerator();

        String loginUrl = "http://www.zimuzu.tv/user/login";
        driver.get(loginUrl);
//        webDriver.findElement(By.name("email")).sendKeys("106517651@qq.com");
//        webDriver.findElement(By.name("password")).sendKeys("2008qweasd");
//        webDriver.findElement(By.id("login")).click();
        //2.模拟cookie登录 采用之前登录的cookie

        Map<String, String> map = new HashMap<>();
        for (String cookie : cookies.split(";")) {
            String[] item = cookie.split("=");
            map.put(item[0].trim(), item[1].trim());
        }

//        Map<String, String> map = new HashMap<>();
//        map.put("UM_distinctid", "15d0307eba0406-068ba30cfa8099-8383667-100200-15d0307eba1120");
//        map.put("PHPSESSID", "p7k4urq136nd07napl9nifm0r0");
//        map.put("mykeywords", "a%3A1%3A%7Bi%3A0%3Bs%3A15%3A%22%E6%9D%83%E5%8A%9B%E7%9A%84%E6%B8%B8%E6%88%8F%22%3B%7D");
//        map.put("cps3 ", "yhd%2F1504109183%3Bsuning%2F1504109250");
//
//        map.put("GINFO", "uid%3D4364502%26nickname%3Dhappyday56%26group_id%3D1%26avatar_t%3Dhttp%3A%2F%2Ftu.zmzjstu.com%2Fftp%2Favatar%2Ff_noavatar_t.gif%26main_group_id%3D0%26common_group_id%3D55");
//        map.put("CNZZDATA1254180690", "562333839-1498994003-%7C1504183850");
//        map.put("GKEY", "c8bdcbf617f6ab7724569b0ed7a0844e");
//        map.put("ctrip ", "ctrip%2F1504186888");


        String domain = "www.zimuzu.tv";
        map.forEach((x, y) -> {
            BasicClientCookie cookie = new BasicClientCookie(x, y);
            cookie.setDomain(domain);
            cookie.setPath("/");
            addAloneCookie(cookie, driver);
        });
        return driver;
    }




    public WebDriver getZMZWebDriver() {
        String cookies = env.getProperty("cookies");
        return getZMZWebDriver(cookies);
    }

    public WebDriver getZMZWebDriverByUser() {
        WebDriver driver = webDriverService.webDriverGenerator();
        String loginUrl = "http://www.zimuzu.tv/user/login";
        driver.get(loginUrl);
        driver.findElement(By.name("email")).sendKeys("106517651@qq.com");
        driver.findElement(By.name("password")).sendKeys("2008qweasd");
        driver.findElement(By.id("login")).click();

        try {
            Thread.sleep(20000);
        } catch (Exception ex) {
        }


        String domain = "www.zimuzu.tv";
        for (org.openqa.selenium.Cookie ck : driver.manage().getCookies()) {
            BasicClientCookie cookie = new BasicClientCookie(ck.getName(), ck.getValue());
            cookie.setDomain(domain);
            cookie.setPath("/");
            addAloneCookie(cookie, driver);
        }

        return driver;
    }

    public WebDriver getWebDriver(WebDriver webDriver) {

        //1.先请求下网站
        webDriver.get("http://www.meijuhui.net/top100/login.php");

        //2.模拟cookie登录 采用之前登录的cookie
        Map<String, String> map = new HashMap<>();
        String cookies = env.getProperty("webcookies");
        for (String item : cookies.split(";")) {
            String[] arrValues = item.split("=");
            map.put(arrValues[0].trim(), arrValues[1].trim());
        }

        String domain = "www.meijuhui.net";
        WebDriver finalDriver = webDriver;
        map.forEach((x, y) -> {
            BasicClientCookie cookie = new BasicClientCookie(x, y);
            cookie.setDomain(domain);
            cookie.setPath("/");
            addAloneCookie(cookie, finalDriver);
        });
        return webDriver;
    }

    public void addAloneCookie(Cookie cookie, WebDriver driver) {
        org.openqa.selenium.Cookie cookie1 = new org.openqa.selenium.Cookie(cookie.getName(), cookie.getValue(), cookie
                .getDomain(), cookie.getPath(), cookie.getExpiryDate(), cookie.isSecure());
//        webDriver.manage().deleteCookie(cookie1);
        driver.manage().addCookie(cookie1);
    }

    public List<String> getLiList(String text) {
        List result = new ArrayList();
        Pattern pattern = Pattern.compile("<li(.*?)>(.*?)</li>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group(2));
        }
        return result;
    }


    public String clearFormat(String body) {
        //            body = body.replaceAll("<(/?\\S+)\\s*?[^<]*?(/?)>", "<$1$2>");

        //删除不需要的标签
        body = body.replaceAll("<[/]?(font|FONT|big|cite|code|del|dfn|em|font|ins|kbd|samp|small|strike|strong|sub|sup|tt|var|span|SPAN|xml|XML|del|DEL|ins|INS|meta|META|[ovwxpOVWXP]:\\w+)[^>]*?>", "");
        //删除不需要的属性
        body = body.replaceAll("<([^>]*)(?:lang|LANG|id|lang|width|height|align|hspace|valign|class|CLASS|style|STYLE|size|SIZE|face|FACE|[ovwxpOVWXP]:\\w+)=(?:'[^']*'|\"\"[^\"\"]*\"\"|[^>]+)([^>]*)>", "<$1$2>");
        body = body.replaceAll("<([^>]*)(?:lang|LANG|id|lang|width|height|align|hspace|valign|class|CLASS|style|STYLE|size|SIZE|face|FACE|[ovwxpOVWXP]:\\w+)=(?:'[^']*'|\"\"[^\"\"]*\"\"|[^>]+)([^>]*)>", "<$1$2>");
        body = body.replaceAll("<([^>]*)(?:lang|LANG|id|lang|width|height|align|hspace|valign|class|CLASS|style|STYLE|size|SIZE|face|FACE|[ovwxpOVWXP]:\\w+)=(?:'[^']*'|\"\"[^\"\"]*\"\"|[^>]+)([^>]*)>", "<$1$2>");

        body = body.replace("<p >", "<p>");
        body = body.replace("<div >", "<div>");
        return body;
    }

    /**
     * 目前还去不样式 脚本
     *
     * @param html
     * @return
     */
    public String getText(String html) {
        html = html.replaceAll("\t|\r|\n", "");//"\t|\r|\n"
        html = html.replaceAll("<[^>]*>", "");
        return html;
    }

    public String findDivSearchItem(String text, String startTitleName) {
        String result = "";
        Pattern pattern = Pattern.compile(startTitleName + "(.*?)</div>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result = matcher.group(1);
            break;
        }
        return result;
    }

    public String getPicture(String text) {
        String result = "";
        Pattern pattern = Pattern.compile("<img(.*?)src=\"([^\\\"]+)\"(.*?)>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(2);
        }
        return result;
    }

    public String removeHref(String text) {
        return text.replaceAll("<a(.*?)href=\"([^\\\"]+)\"(.*?)>(.*?)</a>", "$4");
    }

    public String getTagContent(String text, String startTag, String endTag) {
        String result = "";
        Pattern pattern = Pattern.compile(startTag + "(.*?)" + endTag, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result = matcher.group(1);
            break;
        }
        return result;
    }

    /**
     * 获得第几集
     *
     * @param t
     * @return
     */
    public String getEpisode(String t) {
        String result = "";
        for (String text : t.split("\\.")) {
            if (text.length() >= 6 && ((text.startsWith("S") && text.indexOf("E") == 3) || (text.startsWith("s") && text.indexOf("e") == 3))) {
                result = text.substring(4);
                break;
            }
//            Pattern pattern = Pattern.compile("S(.*?)E(.*?)");
//            Matcher matcher = pattern.matcher(text);
//            if (matcher.find()) {
//                result = matcher.group(2);
//                break;
//            }
        }
        if (StringUtils.isEmpty(result)) {
            for (String text : t.split(" ")) {
                if (text.length() >= 6 && ((text.startsWith("S") && text.indexOf("E") == 3) || (text.startsWith("s") && text.indexOf("e") == 3))) {
                    result = text.substring(4);
                    break;
                }
//                Pattern pattern = Pattern.compile("S(.*?)E(.*?)");
//                Matcher matcher = pattern.matcher(text);
//                if (matcher.find()) {
//                    result = matcher.group(2);
//                    break;
//                }
            }
        }
        return result.length() > 2 ? result.substring(0, 2) : result;
    }

    public MovieModel getMovie(String t) {
        String season = "";
        String episode = "";
        for (String text : t.split("\\.")) {
            if (text.length() >= 6 && ((text.startsWith("S") && text.indexOf("E") == 3) || (text.startsWith("s") && text.indexOf("e") == 3))) {
                season = text.substring(1, 3);
                episode = text.substring(4, 6);
                break;
            }
        }
        if (StringUtils.isEmpty(season) || StringUtils.isEmpty(episode)) {
            for (String text : t.split(" ")) {
                if (text.length() >= 6 && ((text.startsWith("S") && text.indexOf("E") == 3) || (text.startsWith("s") && text.indexOf("e") == 3))) {
                    season = text.substring(1, 3);
                    episode = text.substring(4, 6);
                    break;
                }
            }
        }

        if (!StringUtils.isEmpty(season) && !StringUtils.isEmpty(episode)) {
            MovieModel movieModel = new MovieModel();
            movieModel.setSeason(Integer.parseInt(season.startsWith("0") ? season.substring(1) : season));
            movieModel.setEpisode(Integer.parseInt(episode.startsWith("0") ? episode.substring(1) : episode));
            return movieModel;
        }
        return null;
    }

}

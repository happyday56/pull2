package com.lgh.pull.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lgh on 2016/7/12.
 */
public class RegexHelper {

    private static String REGEX_HREF = "<a.*?href=\"(?<url>.+?)\".*?>(?<content>.+?)</a>";



    /**
     * 移除超链接
     *
     * @param text 字符串内容
     * @return
     */
    public static String removeHref(String text) {
        if(!StringUtils.isEmpty(text)) {
            Pattern pattern = Pattern.compile(REGEX_HREF);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String content = matcher.group("content");
                text = text.replace(matcher.group(), content);
            }
        }
        return text;
    }


    /**
     * 按正则方式查找内容
     *
     * @param text  字符串内容
     * @param regex 正则
     * @return
     */
    public static String findOne(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 按正则方式查找内容
     *
     * @param text  字符串内容
     * @param regex 正则
     * @return
     */
    public static List<String> findAll(String text, String regex) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }


}

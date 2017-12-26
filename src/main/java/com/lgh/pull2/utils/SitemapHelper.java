package com.lgh.pull2.utils;

import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by hot on 2017/10/27.
 */
public class SitemapHelper {

    public static String generateXml(String urls) {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "\t<urlset>\n" + urls + "</urlset>";
        return xml;
    }

    public static String generateItemXml(String loc, String lastmod, String changefreq, String priority) {
        String itemXml = "\t<url>\n";
        itemXml += "\t  <loc>" + loc + "</loc>\n";
        if (!StringUtils.isEmpty(lastmod)) {
            itemXml += "\t  <lastmod>" + lastmod + "</lastmod>\n";
        }
        if (!StringUtils.isEmpty(changefreq)) {
            itemXml += "\t  <changefreq>" + changefreq + "</changefreq>\n";
        }
        if (!StringUtils.isEmpty(priority)) {
            itemXml += "\t  <priority>" + priority + "</priority>\n";
        }
        itemXml += "\t</url>\n";
        return itemXml;
    }

    public static String generateIndexXml(List<String> urls, String lastmod) {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
        xml += "\t<sitemapindex>\n";
        for (String url : urls) {
            xml += "\t     <sitemap>\n" +
                    "\t       <loc>" + url + "</loc>\n" +
                    "\t       <lastmod>" + lastmod + "</lastmod>\n" +
                    "\t     </sitemap>\n";
        }
        xml += "\t</sitemapindex>";
        return xml;
    }
}

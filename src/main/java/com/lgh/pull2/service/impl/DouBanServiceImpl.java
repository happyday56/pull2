package com.lgh.pull2.service.impl;

import com.lgh.pull.model.DouBanModel;
import com.lgh.pull.service.DouBanService;
import com.lgh.pull.service.UtilsService;
import com.lgh.pull.utils.LocalDateTimeUtils;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;

@Service
public class DouBanServiceImpl implements DouBanService {
    private Log log = LogFactory.getLog(DouBanServiceImpl.class);
    @Autowired
    private UtilsService utilsService;

    public DouBanModel get(Integer dbId) {

        try {
            URL url = new URL("https://movie.douban.com/subject/" + dbId + "/");
            Source source = new Source(url);

            DouBanModel model = new DouBanModel();
            //剧情简介
            model.setSummary("");
            Element summary = source.getElementById("link-report");
            if (summary != null) {
                String s = utilsService.removeHref(utilsService.replaceUtf8(utilsService.replaceBlank(utilsService.clearFormat(summary.getContent().toString())).replace("&copy;豆瓣", "")));
                model.setSummary(s.trim());
            }

            model.setPictureUrl("");
            Element pic = source.getElementById("mainpic");
            if (pic != null)
                model.setPictureUrl(utilsService.getPicture(pic.getContent().toString()).replace(".webp", ".jpg"));

            Element info = source.getElementById("info");
            if (info != null) {
                String infoContent = utilsService.clearFormat(info.getContent().toString());

                model.setSeason(0);
                Element elementSeason = source.getElementById("season");
                if (elementSeason != null) {
                    for (Element item : elementSeason.getChildElements()) {
                        if (item.getAttributeValue("selected") != null && item.getAttributeValue("selected").equals("selected")) {
                            model.setSeason(Integer.parseInt(item.getContent().toString()));
                        }
                    }
                }

                String beginTime = utilsService.findBRSearchItem(infoContent, "首播:").trim();
                if (StringUtils.isEmpty(beginTime)) {
                    beginTime = utilsService.findBRSearchItem(infoContent, "上映日期:").trim();
                }

                boolean isFromAmerica = true;
//                if (beginTime.indexOf("美国") >= 0) isFromAmerica = true;

                beginTime = beginTime.indexOf("(") >= 0 ? beginTime.substring(0, beginTime.indexOf("(")) : beginTime;
                beginTime = beginTime.length() >= 10 ? beginTime.substring(0, 10) : "";

                //加一天
                if (!StringUtils.isEmpty(beginTime) && isFromAmerica) {
                    beginTime = LocalDateTimeUtils.getNext(beginTime);
                }
                model.setBeginTime(beginTime);


                String imdb = utilsService.findBRSearchItem(infoContent, "IMDb链接: ");
                List<String> imdbs = utilsService.getHrefNameList(imdb);
                if (imdbs.size() > 0) model.setImdb(imdbs.get(0).trim());
                else model.setImdb("");

                String maxCollection = utilsService.findBRSearchItem(infoContent, "集数:").trim();
                if (!StringUtils.isEmpty(maxCollection))
                    model.setMaxCollection(Integer.parseInt(maxCollection));
                else
                    model.setMaxCollection(0);
            }

            return model;
        } catch (Exception ex) {
            log.error("get from douban error", ex);
        }
        return null;
    }

}

package com.lgh.pull.service.impl;

import com.lgh.pull.entity.SourceArticleTwo;
import com.lgh.pull.model.SpliderArticleModel;
import com.lgh.pull.repository.SourceArticleTwoRepository;
import com.lgh.pull.service.SourceArticleTwoService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/20.
 */
@Service
public class SourceArticleTwoServiceImpl implements SourceArticleTwoService {

    private Log log = LogFactory.getLog(SourceArticleTwoServiceImpl.class);

    @Autowired
    private SourceArticleTwoRepository sourceArticleTwoRepository;

//    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void downData() {
        int i = 1;
        while (i < 6000) {
            String url = "http://www.kmeiju.net/archives/" + i + ".html";
            try {
                SpliderArticleModel model = splider(url);
                if (model != null) {
                    SourceArticleTwo articleTwo = new SourceArticleTwo();
                    articleTwo.setTitle(model.getTitle());
                    articleTwo.setContent(model.getContent());
                    sourceArticleTwoRepository.save(articleTwo);

                    log.info("do " + i);

                }
            } catch (Exception ex) {
            }
            i++;
        }

//        String url = "http://www.kmeiju.net/archives/" + 5299 + ".html";
//        splider(url);
    }

    private SpliderArticleModel splider(String sourceUrl) throws IOException {

        SpliderArticleModel model = null;

        URL url = new URL(sourceUrl);
        Source source = new Source(url);

        Element element = source.getFirstElement("h1");
        if (element != null) {
            String title = element.getContent().toString();

            String content = "";
            Element elementTable = source.getFirstElement("table");
            content += getMovieList(elementTable.getContent().toString());
//            for (Element elementRow : elementTable.getAllElements("tr")) {
//                if (elementRow.getAllElements("td").size() == 3) {
//                    Element elementTd = elementRow.getAllElements("td").get(1);
//                    if (elementTd.getContent().toString().indexOf("ed2k://") >= 0) {
//                        content += getMovieList(elementTd.getContent().toString());
//                    } else {
//                        content += "<li>" + elementTd.getContent().toString() + "</li>";
//                    }
//                }
//            }

            content = "<ol>" + content + "</ol>";


            model = new SpliderArticleModel();
            model.setTitle(title);
            model.setContent(content);

        }
        return model;
    }

    private String getMovieList(String text) {
        String result = "";
        Pattern pattern = Pattern.compile("<a(.*?)href=\"([^\\\"]+)\"(.*?)>(.*?)</a>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            if (matcher.group(2).indexOf("ed2k://") >= 0)
                result += "<li><a href=\"" + matcher.group(2) + "\">" + matcher.group(4) + "</a></li>";
        }
        return result;
    }

}

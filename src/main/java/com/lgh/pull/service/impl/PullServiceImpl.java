package com.lgh.pull.service.impl;


import com.lgh.pull.model.SpliderArticleModel;
import com.lgh.pull.model.xml.*;
import com.lgh.pull.model.xml.Process;
import com.lgh.pull.repository.SourceArticleFiveRepository;
import com.lgh.pull.repository.SourceArticleFourRepository;
import com.lgh.pull.repository.SourceArticleThreeRepository;
import com.lgh.pull.service.PullService;
import com.lgh.pull.service.PushService;
import com.lgh.pull.service.UtilsService;
import com.lgh.pull.service.WebDriverService;
import com.lgh.pull.utils.FileUtil;
import com.lgh.pull.utils.RegexHelper;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hot on 2017/6/13.
 */
@Service
public class PullServiceImpl implements PullService {
    private static Log log = LogFactory.getLog(PullServiceImpl.class);

    @Autowired
    private SourceArticleThreeRepository sourceArticleThreeRepository;

    @Autowired
    private SourceArticleFourRepository sourceArticleFourRepository;

    @Autowired
    private SourceArticleFiveRepository sourceArticleFiveRepository;

    @Autowired
    private PushService pushService;

    @Autowired
    private UtilsService utilsService;

    public List<SpliderArticleModel> start() throws JAXBException, InterruptedException {
        //1.读取配置文件
        File file = new File(this.getClass().getResource("/").getPath() + "targets.xml");
        if (!file.exists()) {
            log.error("not find targets.xml");
            return null;
        }
        //读取项目配置的XML文件
        ProjectRoot root = FileUtil.ConvertToBean(file, ProjectRoot.class);

        //2.处理配置文件
        List<String> spiderUrls = new ArrayList<>();
        List<SpliderArticleModel> result = handleConfigXml(root, spiderUrls);
        return result;
    }

    /**
     * 处理配置文件
     *
     * @param root
     * @param spiderUrls
     * @throws InterruptedException
     */
    private List<SpliderArticleModel> handleConfigXml(ProjectRoot root, List<String> spiderUrls) throws InterruptedException {
        log.debug("handle config xml start");

        Date uploadTime = new Date(System.currentTimeMillis());
        Integer totalCount = 0;
        Integer errorCount = 0;
        List<SpliderArticleModel> result = new ArrayList<>();

        List<Project> listProject = root.getProjects();
        for (Project project : listProject) {
            //判断项目是否开启
            if (!project.isEnabled()) {
                log.error("project " + project.getName() + " no enabled");
                continue;
            }
            //判断目标是否为空
            if (project.getTarget() == null) {
                log.error("project " + project.getName() + " no target");
                continue;
            }
            log.info(project.getName() + " start handleTarget....");

            List<String> listFinalUrl = new ArrayList<>();
            // 获取项目处理目标，分析后，返回需要处理的具体页面
            try {
                List<String> listUrl = handleTarget(project.getTarget());
                //过滤重复的，及以前访问过的
                for (String doFinalUrl : listUrl) {
                    if (!spiderUrls.contains(doFinalUrl)) {
                        listFinalUrl.add(doFinalUrl);
                    }
                }
            } catch (Exception exp) {
                log.error("handleTarget error " + exp.getMessage());
                continue;
            }

//            totalCount += listFinalUrl.size();
//            log.info("start web page");
            List<Process> processes = project.getProcesses();
            for (String doFinalUrl : listFinalUrl) {
                Thread.sleep(2 * 1000);
                try {
                    SpliderArticleModel model = handleProcesses(doFinalUrl, processes);
                    result.add(model);
                } catch (Exception exp) {
                    log.error("web url:" + doFinalUrl, exp);
                    errorCount++;
                }
            }
            spiderUrls.addAll(listFinalUrl);
            log.info("project " + project.getName() + " finined");
        }
        return result;
    }


    /**
     * 处理目标， 获取具体网页列表
     *
     * @param target
     * @return
     * @throws IOException
     */
    private List<String> handleTarget(Target target) throws IOException {
        List<String> result = new ArrayList();
        List<String> listSourceUrl = new ArrayList();
        //1.多个具体网址
        List<Single_Url> multi_url = target.getMulti_url();
        if (multi_url != null) {
            for (Single_Url single_url : multi_url) listSourceUrl.add(single_url.getHref());
        }


        //2.处理通配符网址
        Wildcard_Url wildcard_Url = target.getWildcard_url();
        if (wildcard_Url != null) {
            String target_url = wildcard_Url.getHref();

            Integer change = 0;
            Integer start = wildcard_Url.getStartpos();
            Integer end = wildcard_Url.getEndpos();
            //如果开始比结束大则互换
            if (start > end) {
                change = end;
                end = start;
                start = change;
            }

            //获取要解析的网址列表
            for (int i = start; i <= end; i++) {
                String url = target_url.replace("(*)", String.valueOf(i));
                listSourceUrl.add(url);
                // Pattern p = Pattern.compile("(*)");
                // Matcher matcher = p.matcher(target_url);
                // String url = matcher.replaceAll(String.valueOf(i));
            }
        }

        // 3.根据规则进行解析上面提供的网址，获取具体网页地址列表
        Target_Regex target_regex = target.getTarget_regex();
        if (target_regex != null) {
            String root = !StringUtils.isEmpty(target_regex.getRoot()) ? target_regex.getRoot().toLowerCase() : "";

            for (String sourceurl : listSourceUrl) {
                // 下载页面
//                URL url = new URL(sourceurl);
                String html = getHtmlByDriver(sourceurl);
                Source source = new Source(html);
                // 获取页面内容
                List<String> targetUrls = RegexHelper.findAll(source.getSource().toString(), target_regex.getValue());
                for (String targetUrl : targetUrls) result.add(root + targetUrl);
            }
        }

        //4.规则为空的情况下直接采用过滤取URL
        Target_Filter target_filter = target.getTarget_filter();
        if (target_filter != null) {
            String root = !StringUtils.isEmpty(target_filter.getRoot()) ? target_filter.getRoot().toLowerCase() : "";
            String suffix = !StringUtils.isEmpty(target_filter.getSuffix()) ? target_filter.getSuffix() : "";
            for (String sourceurl : listSourceUrl) {
                // 下载页面
//                URL url = new URL(sourceurl);
                String html = getHtmlByDriver(sourceurl);
                Source source = new Source(html);
                List<Element> elements = source.getAllElements(target_filter.getKey(), target_filter.getValue(), false);
                for (Element element : elements) {
                    Pattern p = Pattern.compile("href\\=\\\"(.+?)\\\"");
                    Matcher matcher = p.matcher(element.getContent().toString());
                    while (matcher.find()) {
                        String m = matcher.group(1);
                        if (m.lastIndexOf(".") >= 0) {
                            m = m.substring(0, m.lastIndexOf(".")) + suffix + m.substring(m.lastIndexOf("."));
                        }
                        result.add(root + m);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 处理单个最终页面
     *
     * @param doFinalUrl
     * @param listProcess
     * @throws IOException
     */

    private SpliderArticleModel handleProcesses(String doFinalUrl, List<Process> listProcess) throws IOException, URISyntaxException {
        SpliderArticleModel result = new SpliderArticleModel();

//        URL url = new URL(doFinalUrl);
        String html = getHtmlByDriver(doFinalUrl);
        Source source = new Source(html);

        //处理description和keywords
        // 获取关键字
        List<StartTag> keywords = source.getAllStartTags("name", "keywords", false);
        for (StartTag tag : keywords) {
            result.setKeywords(tag.getAttributeValue("content"));
        }
        // 获取描述
        List<StartTag> description = source.getAllStartTags("name", "description", false);
        for (StartTag tag : description) {
            result.setDescription(tag.getAttributeValue("content"));
        }

        result.setFromUrl(doFinalUrl);

        for (Process process : listProcess) {
            handleProcess(source, process, result);
        }

        return result;
    }

    /**
     * 处理单个process 一个process对应的一个字段 （单个文章的标题 内容 图片）
     * 步骤:
     * 1.以正则方式获取内容
     * 2.以标签方式获取
     * 3.对以1或2方式获取的内容进行过滤
     * 4.把处理内容保存到实体中
     *
     * @param source
     * @param process
     * @param result
     */
    private void handleProcess(Source source, Process process, SpliderArticleModel result) throws IOException, URISyntaxException {
        String sourceHtml = source.getSource().toString();
        //处理后的内容
        String processContent = "";

        //1.以正则方式获取内容
        String regex_filter = process.getProcess_regex_filter();
        if (!StringUtils.isEmpty(regex_filter)) {
            processContent = RegexHelper.findOne(sourceHtml, regex_filter);
        }

        //2.以标签方式获取
        Process_Tag_Filter process_tag_filter = process.getProcess_tag_filter();
        if (process_tag_filter != null) {
            String key = process_tag_filter.getKey();
            String value = process_tag_filter.getValue();

            //不管id class name 统一采用属性方式获取
            List<Element> listElement = source.getAllElements(key, value, false);
            Integer pos = process_tag_filter.getPos();
            Integer postCount = 1;
            for (Element element : listElement) {
                if (pos == postCount) {
                    processContent = element.getContent().toString();
                    //进行获取子层级处理(5层)
                    Integer childrenLevel = process_tag_filter.getChildrenLevel();
                    if (!StringUtils.isEmpty(processContent) && childrenLevel != null && childrenLevel > 0) {
                        Source source1 = new Source(processContent);
                        if (childrenLevel == 1)
                            processContent = source1.getChildElements().get(0).getContent().toString();
                        else if (childrenLevel == 2)
                            processContent = source1.getChildElements().get(0).getChildElements().get(0).getContent().toString();
                        else if (childrenLevel == 3)
                            processContent = source1.getChildElements().get(0).getChildElements().get(0).getChildElements().get(0).getContent().toString();
                        else if (childrenLevel == 4)
                            processContent = source1.getChildElements().get(0).getChildElements().get(0).getChildElements().get(0).getChildElements().get(0).getContent().toString();
                        else if (childrenLevel == 5)
                            processContent = source1.getChildElements().get(0).getChildElements().get(0).getChildElements().get(0).getChildElements().get(0).getChildElements().get(0).getContent().toString();

                    }
                    break;
                }
                postCount++;
            }
        }

        //3.对以1或2方式获取的内容进行过滤
        if (process.getProcess_clean() != null)
            processContent = handleProcessClean(processContent, process.getProcess_clean());

        //4.把处理内容保存到实体中（标题 内容 图片）
        String field = process.getField();
        if (field.equals("title"))
            result.setTitle(processContent);
        else if (field.equals("content")) {
            result.setContent(processContent);
            //处理内容的同时处理简介summary
            if (!StringUtils.isEmpty(processContent)) {
                String processSummary = getFilterSummary(processContent);
                if (StringUtils.isEmpty(processSummary)) processSummary = result.getTitle();
                result.setSummary(processSummary);
            }
        } else if (field.equals("category")) {
            result.setCategory(processContent);
        }
    }

    /**
     * 获得过滤的简介
     *
     * @param processContent
     * @return
     */
    private String getFilterSummary(String processContent) {
        if (!StringUtils.isEmpty(processContent)) {
            Source sourceSummary = new Source(processContent);
            String processSummary = sourceSummary.getTextExtractor().toString();
            if (processSummary.length() > 150)
                processSummary = processSummary.substring(0, 150);
            if (processSummary.lastIndexOf("。") > 0) {
                processSummary = processSummary.substring(0, processSummary.lastIndexOf("。") + 1);
            } else if (processSummary.lastIndexOf("！") > 0) {
                processSummary = processSummary.substring(0, processSummary.lastIndexOf("！") + 1);
            }
            return processSummary;
        }
        return null;
    }

    /**
     * 对内容进行清理
     * 只处理一级的标签 TODO: 2016/6/9  太单一
     *
     * @param content
     * @param clean_tags
     */
    private String handleProcessClean(String content, List<Clean_Tag> clean_tags) {
        if (clean_tags != null && clean_tags.size() > 0) {
            Source source = new Source(content);
            StringBuilder strB = new StringBuilder();
            for (Element element : source.getChildElements()) {
                //默认不清理
                boolean isClearTag = false;
                for (Clean_Tag clean_tag : clean_tags) {
                    if ("attribute".equals(clean_tag.getType())) {
                        if (element.getAttributes().getValue(clean_tag.getKey()) != null &&
                                element.getAttributes().getValue(clean_tag.getKey()).equals(clean_tag.getValue())) {
                            isClearTag = true;
                            break;
                        }
                    }

                    if ("tag".equals(clean_tag.getType()) && element.getName().equals(clean_tag.getValue())) {
                        isClearTag = true;
                        break;
                    }
                }

                //不清理才添加到返回数据中
                if (!isClearTag && !element.isEmpty()) {
                    strB.append(element.toString());
                }
            }
            content = strB.toString();
        }
        return content;
    }

    @Autowired
    private WebDriverService webDriverService;

    private String getHtmlByDriver(String url) {
        WebDriver driver = webDriverService.webDriverGenerator();
        driver.get(url);
        // 内容
        String content = driver.getPageSource();
        driver.quit();
        return content;
    }

    /**
     * 电影
     */
//    public void pullMovie() {
//        WebDriver driver = utilsService.getZMZWebDriver();
//
//        int i = 1;
//        while (i <= 11) {
//            log.info(i + " list");
//
////            String listUrl = "http://www.zimuzu.tv/fresourcelist?page=" + i + "&channel=movie&area=&category=&year=&tvstation=&sort=score";
//            // String listUrl = "http://www.zimuzu.tv/fresourcelist?channel=documentary&area=&category=&year=&tvstation=&sort=score&page=" + i;//han moivie need remove
//            //String listUrl = "http://www.zimuzu.tv/fresourcelist?channel=tv&area=&category=%E5%8A%A8%E7%94%BB&year=&tvstation=&sort=score&page=" + i;
//            String listUrl = "http://www.zimuzu.tv/resourcelist?channel=tv&area=&category=%E5%8A%A8%E7%94%BB&year=&tvstation=&sort=score&page=" + i;
//            driver.get(listUrl);
//
//            String listHtml = driver.getPageSource().toString();
//            Source source = new Source(listHtml);
//
//            Element m_listItems = source.getFirstElementByClass("resource-showlist");
//            if (m_listItems != null) {
//                List<Element> listItems = m_listItems.getAllElementsByClass("clearfix");
//                for (Element element : listItems) {
//                    try {
//                        if (element.getName().equals("li")) {
//                            String url = getUrl(element.getContent().toString());
//                            String id = url.replace("/resource/", "");
//                            log.info(id + " resource id");
//                            if (!StringUtils.isEmpty(id)) {
//                                //"http://www.zimuzu.tv/gresource/20199"
//                                //"http://www.zimuzu.tv/resource/list/34274"
//                                // 记录片特殊处理 电影不要
////                                if (element.getFirstElementByClass("tag") != null && !element.getFirstElementByClass("tag").getTextExtractor().toString().equals("电影")) {
//
//                                SourceArticleFive sourceArticleThree = getOneMovie(driver, id, "");
//
//                                //score
//                                Element score = element.getFirstElementByClass("point");
//                                sourceArticleThree.setPingfen(0f);
//                                if (score != null && !StringUtils.isEmpty(score.getTextExtractor().toString())) {
//                                    sourceArticleThree.setPingfen(Float.parseFloat(score.getTextExtractor().toString()));
//                                }
//
//                                sourceArticleFiveRepository.save(sourceArticleThree);
////                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        log.error("get " + element.getContent().toString() + " fail", e);
//                    }
//                }
//            }
//
//            i++;
//        }
//
//
//        //one test
////        try {
////            SourceArticleFour sourceArticleThree = getOneMovie(driver, "10590");
////            sourceArticleFourRepository.save(sourceArticleThree);
////        } catch (Exception e) {
////            log.error("fail", e);
////        }
//
//        driver.quit();
//    }














    public void pullWebAlexaTop() {


        WebDriver driver = webDriverService.webDriverGenerator();


        String webUrl = "http://www.alexa.cn/siterank/";
        int i = 1;
        while (i <= 500) {
            driver.get(webUrl + i);
            List<WebElement> elements = driver.findElements(By.className("siterank-sitelist"));
            for (WebElement element : elements) {
                log.info(element.getText().toString());
            }
            i++;
        }

        driver.quit();
    }


}

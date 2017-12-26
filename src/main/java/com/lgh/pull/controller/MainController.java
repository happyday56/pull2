package com.lgh.pull.controller;

import com.lgh.pull.exception.UpdatedFailedException;
import com.lgh.pull.service.ArchivesService;
import com.lgh.pull.service.PushService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Created by hot on 2017/8/28.
 */
@Controller
@RequestMapping("/main")
public class MainController {

    private static Log log = LogFactory.getLog(MainController.class);

    @Autowired
    private ArchivesService archivesService;

    @Autowired
    private PushService pushService;

    @Autowired
    private Environment env;

    @RequestMapping("/pull")
    public String pull() {
        return "pull";
    }

    /**
     * @param mode
     * @param zmzId
     * @param ttId
     * @param typeId
     * @param cookies
     * @param toId
     * @param from     1 tt 0 zmz
     * @param endZmzId
     * @param endId
     * @param month
     * @param dbId
     * @param season
     * @return
     */
    @RequestMapping("/pullDo")
    public String pullDo(Integer mode, Integer zmzId, Integer ttId, Integer typeId
            , String cookies, Integer toId, Integer from, Integer endZmzId
            , Integer endId, String month, Integer dbId, int season) {

//        if (StringUtils.isEmpty(cookies))
//            cookies = env.getProperty("cookies");

        String tip = "success";
        try {
            if (mode == 0) {
                archivesService.addNewFromZMZ(zmzId, ttId, typeId, cookies, dbId, season);
            } else if (mode == 1) {
                archivesService.updateDownUrls(from, toId);
            } else if (mode == 2) {
                archivesService.updateAbouts(toId);
            } else if (mode == 3) {
                //从自定的字幕组Id开始批量新增
                archivesService.addNewFromStartZMZ(zmzId, endZmzId, cookies);
            } else if (mode == 4) {
                archivesService.doUrlsFromZMZBatchSmart();
            } else if (mode == 5) {
                archivesService.batchEditForPicture(toId, endId);
            } else if (mode == 6) {
                archivesService.updateAllImdbAndMaxCollectionFromDB();
            } else if (mode == 52) {
                archivesService.createBackList();
            } else if (mode == 51) {
                archivesService.doThisSE();
            } else if (mode == 53) {
                archivesService.checkDeadLink();
            } else if (mode == 54) {
                archivesService.doSummaryNoLink();
            } else if (mode == 55) {
                archivesService.doUrlsFromTTBatchSmart();
            } else if (mode == 91) {
                archivesService.doKeywordsYear();
            } else if (mode == 92) {
                archivesService.doRecommend();
            } else if (mode == 58) {
                archivesService.createLastMonthBack(Integer.parseInt(month.split("-")[0]), Integer.parseInt(month.split("-")[1]),toId);
            } else if (mode == 7) {
                archivesService.updateAboutFromDB(toId);
            } else if (mode == 101) {
                archivesService.tempDo();
            } else if (mode == 59) {
                archivesService.createDaySchedule(Integer.parseInt(month.split("-")[0]), Integer.parseInt(month.split("-")[1]));
            } else if (mode == 60) {
                archivesService.createMovieFromZmzSchedule(Integer.parseInt(month.split("-")[0]), Integer.parseInt(month.split("-")[1]));
            } else if (mode == 61) {
                archivesService.checkBeginTime(Integer.parseInt(month.split("-")[0]), Integer.parseInt(month.split("-")[1]));
            } else if (mode == 102) {
                archivesService.autoCreateTagSiteMapXml();
            }
        } catch (UpdatedFailedException e) {
            return "redirect:tip?tip=fail";
        } catch (IOException e) {
            return "redirect:tip?tip=fail";
        }
        return "redirect:tip?tip=" + tip;
    }

    @RequestMapping("/tip")
    public String tip(String tip, Model model) {
        model.addAttribute("tip", tip);
        return "tip";
    }
}

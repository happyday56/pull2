package com.lgh.pull2.service;

import com.lgh.pull.exception.UpdatedFailedException;

import java.io.IOException;

/**
 * Created by hot on 2017/8/28.
 */
public interface ArchivesService {
    void addNewFromZMZ(Integer zmzId, Integer ttId, Integer typeId, String cookies, Integer dbId, Integer season) throws UpdatedFailedException;

    void updateDownUrls(Integer type, Integer id) throws UpdatedFailedException;

    void updateAbouts(Integer toId) throws UpdatedFailedException, IOException;

//    void doUrlsFromTTBatch();

    void doUrlsFromTTBatchSmart();

    void autoDoUrls();

    void autoDoSysConfig();

    void autoCreateTagSiteMapXml();

    void autoCreateIndexSiteMapXml();

    void autoCreateDaySchedule();

    void autoDoSEAndBack();

    void autoDoArticle();

    void autoDoScoreTags();

    void addNewFromStartZMZ(Integer startZmzId, Integer endZmzId, String cookies);

//    void doUrlsFromZMZBatch(String cookies);

    void doUrlsFromZMZBatchSmart();

    void batchEditForPicture(Integer toId, Integer endId);

    void updateAllImdbAndMaxCollectionFromDB();

    void createBackList();

    void doThisSE();

    void checkDeadLink();

    void doSummaryNoLink();

    void doKeywordsYear();

    void doRecommend();

    //    void doOver();
    void createLastMonthBack(Integer year, Integer month, Integer aid);

    void updateAboutFromDB(Integer id);

//    String findNewMovie(Integer year, Integer month);

    void tempDo();

    void createMovieFromZmzSchedule(Integer year, Integer month);


    void createDaySchedule(Integer year, Integer month);

    void checkBeginTime(Integer year, Integer month);
}

package com.lgh.pull2.service;

import java.io.IOException;

/**
 * Created by hot on 2017/6/16.
 */
public interface PushService {
    void doRecommend();

    void login();


    void doLitPic();

    void doUpdateTime();

    void getScore() throws IOException;

    void updateMovieContentTitle();

    void updateKeywordsNull();

    void initSearchItem();

    void updateSummaryAnddownUrlsAndRecommonder();




    void findRepeatLinks(Integer id, String downUrls);

    void checkImageFileExist();

    int getCurrentCollection(String downUrls);




}

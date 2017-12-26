package com.lgh.pull.service;

import com.lgh.pull.entity.SourceArticleFive;
import com.lgh.pull.model.DownloadUrlModel;
import com.lgh.pull.model.PlayStatusModel;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.text.ParseException;

public interface ZmzService {
    SourceArticleFive getOneMovie(WebDriver driver, Integer id, int season) throws ParseException, IOException;

    void getOneMovieMainInfo(Integer zmzId, SourceArticleFive sourceArticleThree) throws IOException;

    String getOneMovieDownloadUrl(WebDriver driver, Integer zmzId, String theSeason);


    SourceArticleFive getZMZData(Integer zmzId, String cookies, Integer season);

    SourceArticleFive getZMZData(Integer zmzId, WebDriver driver, Integer season);

    Float getScore(Integer zmzId);

    /**
     * 获得本季状态和resource_content
     *
     * @param zmzId
     * @return
     * @throws IOException
     */
    PlayStatusModel getStatus(Integer zmzId);

    DownloadUrlModel getOneMovieDownloadUrlSmart(Integer zmzId, int season);
}

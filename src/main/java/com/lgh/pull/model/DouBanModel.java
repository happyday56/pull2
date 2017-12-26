package com.lgh.pull.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DouBanModel {
    /**
     * 简介
     */
    private String summary = "";

    private String imdb = "";
    /**
     * 首播
     */
    private String beginTime = "";
    /**
     * 图片地址
     */
    private String pictureUrl = "";
    /**
     * 最大集数
     */
    private Integer maxCollection = 0;

    /**
     * 季
     */
    private Integer season = 0;
}

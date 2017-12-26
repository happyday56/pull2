package com.lgh.pull.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/6/17.
 */
@Entity
@Getter
@Setter
@Table(name = "dede_addonarticle")
public class AddOnArticle {

    @Id
    @Column(name = "aid")
    private Integer aid;

    /**
     * 内容
     */
    @Lob
    @Column(name = "body")
    private String body;
    /**
     * 评分
     */
    @Column(name = "pingfen", precision = 12, scale = 1)
    private Float pingfen;

    @Column(name = "typeid")
    private Integer typeid;

    /**
     * 留言数量
     */
    @Column(name = "feedback")
    private Integer feedback;

    /**
     * 英文名
     */
    @Column(length = 200, name = "englishname")
    private String englishName;

    /**
     * 地区
     */
    @Column(length = 100, name = "areaname")
    private String areaName;

    /**
     * 语言
     */
    @Column(length = 200, name = "language")
    private String language;

    /**
     * 首播
     */
    @Column(length = 200, name = "begintime")
    private String beginTime;
    /**
     * 公司 电视台
     */
    @Column(length = 200, name = "corp")
    private String corp;
    /**
     * 类型 （战争/剧情等）
     */
    @Column(length = 200, name = "type")
    private String type;

    @Column(length = 200, name = "imdb")
    private String imdb;
    /**
     * 别名
     */
    @Column(length = 200, name = "alias")
    private String alias;
    /**
     * 编剧
     */
    @Column(length = 200, name = "author")
    private String author;
    /**
     * 导演
     */
    @Column(length = 500, name = "director")
    private String director;

    /**
     * 主演
     */
    @Column(length = 500, name = "zhuyan")
    private String zhuYan;
    /**
     * 电影名
     */
    @Column(length = 100, name = "moviename")
    private String movieName;

    /**
     * 简介
     */
    @Lob
    @Column(name = "summary")
    private String summary = "";

    /**
     * 下载地址
     */
    @Lob
    @Column(name = "downurls")
    private String downUrls = "";

    /**
     * 相关推荐
     */
    @Lob
    @Column(name = "recommend")
    private String recommend = "";

    /**
     * 图片地址
     */
    @Lob
    @Column(name = "pictureurl")
    private String pictureUrl = "";

    @Column(name = "fromzmzid")
    private Integer fromZMZId = 0;

    @Column(name = "fromttid")
    private Integer fromTTId = 0;

    @Column(name = "fromdbid")
    private Integer fromDBId = 0;

    /**
     * 美剧状态 未开播 连载中 本季完结 本剧完结
     */
    @Column(name = "staus", length = 20)
    private String staus = "";

    /**
     * 当前集数
     */
    @Column(name = "curcollection")
    private Integer curCollection = 0;

    /**
     * 最大集数
     */
    @Column(name = "maxcollection")
    private Integer maxCollection = 0;

    /**
     * 季
     */
    @Column(name = "season")
    private Integer season = 0;
    /**
     * 补充说明
     */
    @Lob
    @Column(name = "extra")
    private String extra="";
}

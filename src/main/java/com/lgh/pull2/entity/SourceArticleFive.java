package com.lgh.pull2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by hot on 2017/8/30.
 */
@Entity
@Getter
@Setter
public class SourceArticleFive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 标题
     */
    @Column(length = 200)
    private String title;

    /**
     * 评分
     */
    private Float pingfen;

    /**
     * 短标题
     */
    @Column(length = 100)
    private String shortTitle;

    /**
     * 图片地址
     */
    @Column(length = 200)
    private String pictureUrl;

    /**
     * 英文名
     */
    @Column(length = 200)
    private String englishName;

    /**
     * 地区
     */
    @Column(length = 100)
    private String areaName;

    /**
     * 语言
     */
    @Column(length = 200)
    private String language;

    /**
     * 首播
     */
    @Column(length = 200)
    private String beginTime;
    /**
     * 公司 电视台
     */
    @Column(length = 200)
    private String corp;
    /**
     * 类型 （战争/剧情等）
     */
    @Column(length = 200)
    private String type;

    @Column(length = 200)
    private String imdb;
    /**
     * 别名
     */
    @Column(length = 200)
    private String alias;
    /**
     * 编剧
     */
    @Column(length = 200)
    private String author;
    /**
     * 导演
     */
    @Column(length = 200)
    private String director;

    /**
     * 主演
     */
    @Column(length = 500)
    private String zhuYan;

    /**
     * 简介
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String summary;

    /**
     * 内容
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String content;
    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 关键字
     */
    @Column(length = 200)
    private String keywords;

    /**
     * 爬虫的网址
     */
    @Column(length = 500)
    private String fromUrl;


    @Column(length = 100)
    private String fromId;


    /**
     * 下载地址
     */
    @Lob
    @Column(name = "downurls")
    private String downUrls;

    /**
     * 主类型 如 纪录片 电影 电视剧 剧 动画 （公开课）
     */
    @Transient
    private String mainType;
}

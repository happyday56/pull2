package com.lgh.pull2.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by hot on 2017/6/13.
 */
@Getter
@Setter
public class SpliderArticleModel {
    /**
     * 分类
     */
    @Column(length = 500)
    private String category;
    /**
     * 标题
     */
    @Column(length = 200)
    private String title;

//    /**
//     * 图片地址
//     */
//    @Column(length = 200)
//    private String pictureUrl;
    /**
     * 内容
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String content;


    /**
     * 摘要
     */
    @Column(length = 500)
    private String summary;

    /**
     * 关键字
     */
    @Column(length = 200)
    private String keywords;

    /**
     * 描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 爬虫的网址
     */
    @Column(length = 500)
    private String fromUrl;

//    /**
//     * 时间
//     */
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date time;
}

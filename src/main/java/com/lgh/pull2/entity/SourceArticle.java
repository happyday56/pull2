package com.lgh.pull2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 文章数据源头
 * Created by hot on 2017/6/13.
 */
@Entity
@Getter
@Setter
public class SourceArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
}

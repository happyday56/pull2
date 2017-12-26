package com.lgh.pull2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/6/20.
 */
@Entity
@Getter
@Setter
public class SourceArticleTwo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}

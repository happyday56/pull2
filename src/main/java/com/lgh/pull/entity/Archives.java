package com.lgh.pull.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/6/17.
 */
@Entity
@Getter
@Setter
@Table(name = "dede_archives")
public class Archives {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "litpic")
    private String litPic;

    @Column(name = "senddate")
    private Long senddate;

    @Column(name = "pubdate")
    private Long updateTime;

    @Column(name = "typeid")
    private Integer typeid;

    @Column(name = "keywords", length = 100)
    private String keywords;

    @Column(name = "voteid")
    private Integer voteId;
}

package com.lgh.pull2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/9/1.
 */
@Entity
@Getter
@Setter
@Table(name = "dede_arctiny")
public class Arctiny {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "typeid")
    private Integer typeId;

    @Column(name = "typeid2")
    private String typeid2;


    @Column(name = "arcrank")
    private Integer arcrank;

    @Column(name = "channel")
    private Integer channel;

    @Column(name = "senddate")
    private Integer sendDate;

    @Column(name = "sortrank")
    private Integer sortRank;

    @Column(name = "mid")
    private Integer mid;
}

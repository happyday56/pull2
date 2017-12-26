package com.lgh.pull.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "downloadarticle")
public class DownloadArticle {
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 下载地址
     */
    @Lob
    @Column(name = "downurls")
    private String downUrls;

    /**
     * 上次下载时间
     */
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
}

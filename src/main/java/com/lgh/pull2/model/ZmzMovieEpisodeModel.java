package com.lgh.pull2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ZmzMovieEpisodeModel {
    private Integer episode;
    /**
     * 第二季第一集
     */
    private String episodeName;
    private String filename;
    private String filesize;
    private List<ZmzMovieDownLinkModel> downLinks;

}

package com.lgh.pull2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ZmzMovieFormatterModel {
    private String formatterId;
    private String formatterName;
    /**
     * 子标题
     */
    private String subName;
    private List<ZmzMovieEpisodeModel> episondes;
}

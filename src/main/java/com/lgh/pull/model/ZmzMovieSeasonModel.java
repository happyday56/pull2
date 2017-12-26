package com.lgh.pull.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public  class ZmzMovieSeasonModel
{
    private String seasonId;
    private Integer season;
    private String seasonName;
    private List<ZmzMovieFormatterModel> formaters;
}

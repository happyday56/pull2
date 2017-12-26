package com.lgh.pull.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ZmzMovieModel {
//    private String name;
//    private String englishName;
    List<ZmzMovieSeasonModel> seasons;
}


package com.lgh.pull2.service;

import java.util.List;

public interface WebService {
    void generateMovieHtml(Integer id);
    void batchGenerateMovieHtml(List<Integer> listUpdated);
}

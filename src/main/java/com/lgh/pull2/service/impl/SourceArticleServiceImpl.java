package com.lgh.pull2.service.impl;

import com.lgh.pull.entity.SourceArticle;
import com.lgh.pull.model.SpliderArticleModel;
import com.lgh.pull.repository.SourceArticleRepository;
import com.lgh.pull.service.PullService;
import com.lgh.pull.service.SourceArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hot on 2017/6/13.
 */
@Service
public class SourceArticleServiceImpl implements SourceArticleService {

    @Autowired
    private PullService pullService;
    @Autowired
    private SourceArticleRepository sourceArticleRepository;

//    @Scheduled(initialDelay = 3000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void savePullData() throws JAXBException, InterruptedException {
        List<SourceArticle> list = new ArrayList<>();
        List<SpliderArticleModel> spliderArticleModels = pullService.start();
        for (SpliderArticleModel spliderArticleModel : spliderArticleModels) {
            SourceArticle sourceArticle = new SourceArticle();
            sourceArticle.setTitle(spliderArticleModel.getTitle());
            sourceArticle.setCategory(spliderArticleModel.getCategory());
            sourceArticle.setFromUrl(spliderArticleModel.getFromUrl());
            sourceArticle.setContent(spliderArticleModel.getContent());
            sourceArticle.setDescription(spliderArticleModel.getDescription());
            sourceArticle.setKeywords(spliderArticleModel.getKeywords());
            sourceArticle.setSummary(spliderArticleModel.getSummary());
            list.add(sourceArticle);
        }

        sourceArticleRepository.save(list);
    }

}

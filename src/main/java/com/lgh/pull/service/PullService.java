package com.lgh.pull.service;

import com.lgh.pull.model.SpliderArticleModel;

import javax.xml.bind.JAXBException;
import java.util.List;

/**
 * Created by hot on 2017/6/13.
 */
public interface PullService {


    List<SpliderArticleModel> start() throws JAXBException, InterruptedException;

    void pullWebAlexaTop();

}

package com.lgh.pull.service;

import javax.xml.bind.JAXBException;

/**
 * Created by hot on 2017/6/13.
 */
public interface SourceArticleService {

    void savePullData() throws JAXBException, InterruptedException;
}

package com.lgh.pull.service.impl;

import com.lgh.pull.model.TianTianModel;
import com.lgh.pull.service.TianTianService;
import com.lgh.pull.service.UtilsService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class TianTianServiceImpl implements TianTianService {

    private Log log = LogFactory.getLog(TianTianServiceImpl.class);

    @Autowired
    private UtilsService utilsService;

    public TianTianModel getImdbFromTT(Integer ttId) {

        try {
            URL url = new URL("http://www.kmeiju.net/archives/" + ttId + ".html");
            Source source = new Source(url);
            Element element = source.getElementById("content");
            if (element != null) {
                TianTianModel tianTianModel = new TianTianModel();
                String content = utilsService.clearFormat(element.getContent().toString());
                tianTianModel.setImdb(utilsService.findDivSearchItem(content, "IMDb链接:").trim());
                tianTianModel.setMaxCollection(utilsService.findBRSearchItem(content, "集数:").trim());
                return tianTianModel;
            }
        } catch (Exception ex) {
            log.error("getDownUrlsFromTT error");
        }
        return null;
    }
}

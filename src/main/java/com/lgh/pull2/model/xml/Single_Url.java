package com.lgh.pull2.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 单个具体目标网址
 */
public class Single_Url {
    private String href;

    public String getHref() {
        return href;
    }

    @XmlAttribute(name = "href")
    public void setHref(String href) {
        this.href = href;
    }
}

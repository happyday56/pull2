package com.lgh.pull.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 通配符网址 (目标对应的网址，可能是首页或列表等) 跟Multi_Url可二选其一
 */
public class Wildcard_Url {
    /**
     * 通配符网址配置
     */
    private String href;
    /**
     * 开始 可以比结束的小
     */
    private int startpos;
    /**
     * 结束
     */
    private Integer endpos;

    public String getHref() {
        return href;
    }

    @XmlAttribute(name = "href")
    public void setHref(String href) {
        this.href = href;
    }

    public Integer getStartpos() {
        return startpos;
    }

    @XmlAttribute(name = "startpos")
    public void setStartpos(Integer startpos) {
        this.startpos = startpos;
    }

    public Integer getEndpos() {
        return endpos;
    }

    @XmlAttribute(name = "endpos")
    public void setEndpos(Integer endpos) {
        this.endpos = endpos;
    }
}

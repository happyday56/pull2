package com.lgh.pull.model.xml;


import javax.xml.bind.annotation.XmlAttribute;

/**
 * 抓取目标列表过滤 直接取链接url
 * Created by Administrator on 2016/6/4.
 */
public class Target_Filter {

    /**
     * 如果链接没有root，只有相对地址，需添加
     */
    private String root;
    /**
     * 标签的属性 包含 id,class,name等(width，href，target也支持)
     */

    private String key;
    /**
     * 属性的值
     */

    private String value;

    /**
     * 扩展地址后缀
     */

    private String suffix;


    public String getRoot() {
        return root;
    }

    @XmlAttribute(name = "root")
    public void setRoot(String root) {
        this.root = root;
    }

    public String getKey() {
        return key;
    }

    @XmlAttribute(name = "key")
    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    @XmlAttribute(name = "value")
    public void setValue(String value) {
        this.value = value;
    }

    public String getSuffix() {
        return suffix;
    }

    @XmlAttribute(name = "suffix")
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}

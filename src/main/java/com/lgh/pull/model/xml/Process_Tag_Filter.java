package com.lgh.pull.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 通过标签方式处理 如 id为conent获取内容
 */
public class Process_Tag_Filter {
    /**
     * 获取标签的位置 默认为1 第一个
     */
    private Integer pos = 1;
    /**
     * 标签的属性 包含 id,class,name等(width，href，target也支持)
     */
    private String key;
    /**
     * 属性的值
     */
    private String value;


    /**
     * 自己层次
     * 如果上面获取的数据太外面，可以进行找子项处理
     */
    private Integer childrenLevel;

    public Integer getPos() {
        return pos;
    }

    @XmlAttribute(name = "pos")
    public void setPos(Integer pos) {
        this.pos = pos;
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

    public Integer getChildrenLevel() {
        return childrenLevel;
    }

    @XmlAttribute(name = "childrenLevel")
    public void setChildrenLevel(Integer childrenLevel) {
        this.childrenLevel = childrenLevel;
    }
}

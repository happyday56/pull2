package com.lgh.pull.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 清理标签
 * Created by lenovo on 2015/7/19.
 */
public class Clean_Tag {

    /**
     * 类型 attribute tag
     */
    private String type;
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    /**
     * 属性时 key有效
     * @param key
     */
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

    public String getType() {
        return type;
    }
    @XmlAttribute(name = "type")
    public void setType(String type) {
        this.type = type;
    }
//    /**
//     * 位置 begin 开始 end 结尾
//     */
//    private String postion;
//
//    /**
//     * 行数
//     */
//    private Integer rows;
//
//    public String getPostion() {
//        return postion;
//    }
//    @XmlAttribute(name = "postion")
//    public void setPostion(String postion) {
//        this.postion = postion;
//    }
//
//    public Integer getRows() {
//        return rows;
//    }
//    @XmlAttribute(name = "rows")
//    public void setRows(Integer rows) {
//        this.rows = rows;
//    }

}

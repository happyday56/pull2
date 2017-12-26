package com.lgh.pull.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * 处理目标定义
 *
 * @author Administrator
 */
public class Target {
    /**
     * 处理目标编码 默认utf-8(暂时没用 可以考虑放入project)
     */
    private String encode = "utf-8";
    /**
     * 处理时间 默认8000(暂时没用 可以考虑放入project)
     */
    private Integer timeout = 8000;
    /**
     * 多个列表网址 (目标对应的网址，可能是首页或列表等) 跟Wildcard_Url可二选其一,也可都设置上
     */
    private List<Single_Url> multi_url;
    /**
     * 通配符网址 (目标对应的网址，可能是首页或列表等) 跟Multi_Url可二选其一,也可都设置上
     */
    private Wildcard_Url wildcard_url;

    /**
     * 网址处理规则
     */
    private Target_Regex target_regex;

    /**
     * 网址处理过滤 处理非规则的情况 target_regex设置为空
     * 根据过滤内容直接取链接url
     */
    private Target_Filter target_filter;

    public String getEncode() {
        return encode;
    }

    @XmlAttribute(name = "encode")
    public void setEncode(String encode) {
        this.encode = encode;
    }

    public Integer getTimeout() {
        return timeout;
    }

    @XmlAttribute(name = "timeout")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public List<Single_Url> getMulti_url() {
        return multi_url;
    }

    @XmlElementWrapper(name = "multi_url")
    @XmlElement(name = "single_url")
    public void setMulti_url(List<Single_Url> multi_url) {
        this.multi_url = multi_url;
    }


    public Wildcard_Url getWildcard_url() {
        return wildcard_url;
    }

    public void setWildcard_url(Wildcard_Url wildcard_url) {
        this.wildcard_url = wildcard_url;
    }

    public Target_Regex getTarget_regex() {
        return target_regex;
    }

    public void setTarget_regex(Target_Regex target_regex) {
        this.target_regex = target_regex;
    }

    public Target_Filter getTarget_filter() {
        return target_filter;
    }

    public void setTarget_filter(Target_Filter target_filter) {
        this.target_filter = target_filter;
    }

}

package com.lgh.pull.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * 具体抓取的网站项目
 */
public class Project {

    /**
     * 是否启用 false不启用 暂时不处理这个
     */
    private boolean enabled;
    /**
     * 爬虫的域名
     */
    private String domain;

    /**
     * 所属分类 必要的 插入数据库时使用
     */
    private String category;

    /**
     * 种类
     */
    private String kind;
    /**
     * 项目名称
     */
    private String name;
    /**
     * 项目的目标定义
     */
    private Target target;

    /**
     * 项目的处理方式定义
     */
    private List<Process> processes;


    public boolean isEnabled() {
        return enabled;
    }

    @XmlAttribute(name = "enabled")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public String getDomain() {
        return domain;
    }

    @XmlAttribute(name = "domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCategory() {
        return category;
    }

    @XmlAttribute(name = "category")
    public void setCategory(String category) {
        this.category = category;
    }

    public String getKind() {
        return kind;
    }

    @XmlAttribute(name = "kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    @XmlElementWrapper(name = "processes")
    @XmlElement(name = "process")
    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }


}

package com.lgh.pull2.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * 处理分析最终的网页内容 内容由target分析后获得（核心处理）
 */
public class Process {
    /**
     * 代表处理流程 true 下一个处理使用此缓存的内容，此内容不用存入数据库 如获取具体div中的内容
     * 暂时没用
     */
    private Boolean flow;
    /**
     * 暂时不用
     */
    private String table;
    /**
     * 表的字段 根据此字段存入数据 title content key description
     */
    private String field;

    /**
     * 通过标签方式处理 如 id为conent获取内容
     */
    private Process_Tag_Filter process_tag_filter;


    /**
     * 通过正则方式处理
     */
    private String process_regex_filter;


    /**
     * 处理过程清理不必要的数据 步骤在处理内容后面
     */
    private List<Clean_Tag> process_clean;

    public Boolean getFlow() {
        return flow;
    }

    @XmlAttribute(name = "flow")
    public void setFlow(Boolean flow) {
        this.flow = flow;
    }

    public String getTable() {
        return table;
    }

    @XmlAttribute(name = "table")
    public void setTable(String table) {
        this.table = table;
    }

    public String getField() {
        return field;
    }

    @XmlAttribute(name = "field")
    public void setField(String field) {
        this.field = field;
    }


    public Process_Tag_Filter getProcess_tag_filter() {
        return process_tag_filter;
    }

    public void setProcess_tag_filter(Process_Tag_Filter process_tag_filter) {
        this.process_tag_filter = process_tag_filter;
    }


    public String getProcess_regex_filter() {
        return process_regex_filter;
    }

    public void setProcess_regex_filter(String process_regex_filter) {
        this.process_regex_filter = process_regex_filter;
    }

    public List<Clean_Tag> getProcess_clean() {
        return process_clean;
    }

    @XmlElementWrapper(name = "process_clean")
    @XmlElement(name = "clean_tag")
    public void setProcess_clean(List<Clean_Tag> process_clean) {
        this.process_clean = process_clean;
    }
}

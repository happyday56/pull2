package com.lgh.pull.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 目标列表网址的处理规则
 */
public class Target_Regex {
	/**
	 *如果获取的网址只是相对地址 则补上网站网址
	 */
	private String root;
	/**
	 * 规则（正则）
	 */
	private String value;
	/**
	 * 扩展地址后缀
	 */
	@XmlAttribute(name = "suffix")
	private String suffix;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRoot() {
		return root;
	}

	@XmlAttribute(name = "root")
	public void setRoot(String root) {
		this.root = root;
	}
}

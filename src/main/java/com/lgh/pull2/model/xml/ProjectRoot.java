package com.lgh.pull2.model.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 项目根节点 包含多个项目
 */
@XmlRootElement(name = "projectroot")
public class ProjectRoot {

	private List<Project> projects;


	public List<Project> getProjects() {
		return projects;
	}

	@XmlElementWrapper(name = "projects")
	@XmlElement(name = "project")
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
}

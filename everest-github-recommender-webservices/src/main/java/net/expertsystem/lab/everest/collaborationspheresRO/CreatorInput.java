package net.expertsystem.lab.everest.collaborationspheresRO;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreatorInput {
	private String creator;	
	private String search;
	
	public CreatorInput() {}
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
}

package net.expertsystem.lab.everest.collaborationspheresRO;

import java.util.List;

public class Scientist {
	private String name;
	private String id;
	private List<ResearchObject> listRO;
	
	public Scientist(String id){
		this.id=id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ResearchObject> getListRO() {
		return listRO;
	}

	public void setListRO(List<ResearchObject> listRO) {
		this.listRO = listRO;
	}
}

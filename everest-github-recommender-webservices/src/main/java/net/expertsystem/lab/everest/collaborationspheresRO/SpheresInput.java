package net.expertsystem.lab.everest.collaborationspheresRO;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpheresInput {
	private String [] types;	
	private String [] urls;
	private String iduser;
	
	public SpheresInput() {}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public String[] getUrls() {
		return urls;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;
	}

	public String getIduser() {
		return iduser;
	}

	public void setIduser(String iduser) {
		this.iduser = iduser;
	}
	
}

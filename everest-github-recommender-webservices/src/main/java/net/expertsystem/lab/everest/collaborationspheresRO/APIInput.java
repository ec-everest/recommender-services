package net.expertsystem.lab.everest.collaborationspheresRO;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class APIInput {
	private String [] ros;	
	private String [] scientists;
	
	
	public APIInput() {}


	public String[] getRos() {
		return ros;
	}


	public void setRos(String[] ros) {
		this.ros = ros;
	}


	public String[] getScientists() {
		return scientists;
	}


	public void setScientists(String[] scientists) {
		this.scientists = scientists;
	}	
}

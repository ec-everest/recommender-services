package net.expertsystem.lab.everest.collaborationspheresRO;

public class UserProfile {
	private String sub;
	private String VRC;
	private String given_name;
	private String family_name;
	private String email;
	
	public UserProfile() {}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getVRC() {
		return VRC;
	}

	public void setVRC(String vRC) {
		VRC = vRC;
	}

	public String getGiven_name() {
		return given_name;
	}

	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}

	public String getFamily_name() {
		return family_name;
	}

	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}

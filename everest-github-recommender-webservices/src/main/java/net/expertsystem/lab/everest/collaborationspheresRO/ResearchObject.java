package net.expertsystem.lab.everest.collaborationspheresRO;

import java.util.List;

public class ResearchObject {
	private String id;
	private String title;
	private String created;
	private String author;
	private String description;
	private List<String>domains;
	private List<String>concepts;
	private List<String>expressions;
	private List<String>people;
	private List<String>places;
	private List<String>org;
	private String sketch;
	private String score;
	

	public ResearchObject(String id){
		this.id=id;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getCreated() {
		return created;
	}


	public void setCreated(String created) {
		this.created = created;
	}

	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public List<String> getDomains() {
		return domains;
	}


	public void setDomains(List<String> domains) {
		this.domains = domains;
	}


	public List<String> getConcepts() {
		return concepts;
	}


	public void setConcepts(List<String> concepts) {
		this.concepts = concepts;
	}


	public List<String> getExpressions() {
		return expressions;
	}


	public void setExpressions(List<String> expressions) {
		this.expressions = expressions;
	}


	public List<String> getPeople() {
		return people;
	}


	public void setPeople(List<String> people) {
		this.people = people;
	}


	public List<String> getPlaces() {
		return places;
	}


	public void setPlaces(List<String> places) {
		this.places = places;
	}


	public List<String> getOrg() {
		return org;
	}


	public void setOrg(List<String> org) {
		this.org = org;
	}

	public String getSketch() {
		return sketch;
	}


	public void setSketch(String sketch) {
		this.sketch = sketch;
	}


	public String getScore() {
		return score;
	}


	public void setScore(String score) {
		this.score = score;
	}


}

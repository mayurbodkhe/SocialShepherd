package com.ss.ml.opinion.miner.stanford;

import java.util.List;
import java.util.Map;

public class SentimentAnalyisRawDetails {
	
	private String sentiment;
	private Double score;
	private List<String> features;
	private List<String> opinions;
	private List<RelationShipDetails> relations;
	private Map<String, String> entities;
	private String rawText;
	
	public SentimentAnalyisRawDetails () {}

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public List<String> getOpinions() {
		return opinions;
	}

	public void setOpinions(List<String> opinions) {
		this.opinions = opinions;
	}

	public List<RelationShipDetails> getRelations() {
		return relations;
	}

	public void setRelations(List<RelationShipDetails> relations) {
		this.relations = relations;
	}

	public Map<String, String> getEntities() {
		return entities;
	}

	public void setEntities(Map<String, String> entities) {
		this.entities = entities;
	}

	public String getRawText() {
		return rawText;
	}

	public void setRawText(String rawText) {
		this.rawText = rawText;
	}
	
	
}

class RelationShipDetails{
	
	private String relationshipGov;
	private String relationshipDep;
	private String relationshipType;
	
	public RelationShipDetails(){}

	public String getRelationshipGov() {
		return relationshipGov;
	}

	public void setRelationshipGov(String relationshipGov) {
		this.relationshipGov = relationshipGov;
	}

	public String getRelationshipDep() {
		return relationshipDep;
	}

	public void setRelationshipDep(String relationshipDep) {
		this.relationshipDep = relationshipDep;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}
	
	
}

package com.ss.ml.opinion.miner.stanford;

import java.util.Set;

public class SentimentDetails {
	
	private Set<String> sentiments;
    private String feature;
    private Double score;
    
    public SentimentDetails(){}

	public Set<String> getSentiments() {
		return sentiments;
	}

	public void setSentiments(Set<String> sentiments) {
		this.sentiments = sentiments;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
    
}
	
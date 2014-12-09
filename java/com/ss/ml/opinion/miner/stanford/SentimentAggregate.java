package com.ss.ml.opinion.miner.stanford;

public class SentimentAggregate{
	
	private String sentiment;
	private Double score;
	
	public SentimentAggregate() {}

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
}
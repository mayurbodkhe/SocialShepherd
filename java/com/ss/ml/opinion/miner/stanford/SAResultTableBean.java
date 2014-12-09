package com.ss.ml.opinion.miner.stanford;

public class SAResultTableBean {

	public SAResultTableBean() {};
	
	private String userName;
	private String tweetText;
	private String overallSentimentScore;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTweetText() {
		return tweetText;
	}
	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}
	public String getOverallSentimentScore() {
		return overallSentimentScore;
	}
	public void setOverallSentimentScore(String overallSentimentScore) {
		this.overallSentimentScore = overallSentimentScore;
	}
	
	
	
}

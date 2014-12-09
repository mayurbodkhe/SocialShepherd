package com.ss.ml.opinion.miner.stanford;

public class SentimentTableBean {
	
	private int sentenceId;
	private String dataSourceName;
	private String shortText;
	private String overallSentiment;
	
	public SentimentTableBean (){}

		
	public int getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getShortText() {
		return shortText;
	}

	public void setShortText(String shortText) {
		this.shortText = shortText;
	}

	public String getOverallSentiment() {
		return overallSentiment;
	}

	public void setOverallSentiment(String overallSentiment) {
		this.overallSentiment = overallSentiment;
	}
	
}

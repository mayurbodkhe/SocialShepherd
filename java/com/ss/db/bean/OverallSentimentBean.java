package com.ss.db.bean;

import java.text.DecimalFormat;
import java.util.Map;

public class OverallSentimentBean {
	
	private static final String[] sentimentScores = { "Very Negative","Negative", "Neutral", "Positive", "Very Positive"};
	
	private int veryPositiveCount;
	private double veryPositivePercentage;
	private int veryNegativeCount;
	private double veryNegativePercentage;
	private int neutralCount;
	private double neutralPercentage;
	private int positiveCount;
	private double positivePercentage;
	private int negativeCount;
	private double negativePercentage;
	private int totalCount;
	private double totalPercentage;
	
	private int overallPostiveCount;
	private int overallNegativeCount;
	private double overallPositivePercentage;
	private double overallNegativePercentage;
	
	public OverallSentimentBean(){
		
	}
	
	public OverallSentimentBean(Map<String,Integer> overallSentimentMap){
	
		DecimalFormat df = new DecimalFormat("#.##");
		
		for(Map.Entry<String, Integer> entry: overallSentimentMap.entrySet()){
			if(sentimentScores[0].equalsIgnoreCase(entry.getKey()))
				this.veryNegativeCount = entry.getValue();//5
			else if(sentimentScores[1].equalsIgnoreCase(entry.getKey()))
				this.negativeCount = entry.getValue();//60
			else if(sentimentScores[2].equalsIgnoreCase(entry.getKey()))
				this.neutralCount = entry.getValue();//31
			else if(sentimentScores[3].equalsIgnoreCase(entry.getKey()))
				this.positiveCount = entry.getValue();//11
			else if(sentimentScores[4].equalsIgnoreCase(entry.getKey()))
				this.veryPositiveCount = entry.getValue();//1
		}
		
		this.totalCount = this.veryNegativeCount + this.negativeCount + this.neutralCount + this.positiveCount + this.veryPositiveCount;
			
		this.veryNegativePercentage = Double.valueOf(df.format((double) this.veryNegativeCount / (double) this.totalCount * 100));
		this.negativePercentage = Double.valueOf(df.format((double) this.negativeCount / (double) this.totalCount * 100)); 
		this.neutralPercentage = Double.valueOf(df.format((double) this.neutralCount / (double) this.totalCount * 100));
		this.positivePercentage = Double.valueOf(df.format((double) this.positiveCount / (double) this.totalCount * 100)); 
			
		this.overallPostiveCount = this.veryPositiveCount + this.positiveCount;
		this.overallNegativeCount = this.veryNegativeCount + this.negativeCount;
			
		this.overallPositivePercentage = Double.valueOf(df.format((double) overallPostiveCount / (double) this.totalCount * 100));
		this.overallNegativePercentage = Double.valueOf(df.format((double) overallNegativeCount / (double) this.totalCount * 100));
	}

	public int getVeryPositiveCount() {
		return veryPositiveCount;
	}

	public void setVeryPositiveCount(int veryPositiveCount) {
		this.veryPositiveCount = veryPositiveCount;
	}

	public double getVeryPositivePercentage() {
		return veryPositivePercentage;
	}

	public void setVeryPositivePercentage(double veryPositivePercentage) {
		this.veryPositivePercentage = veryPositivePercentage;
	}

	public int getVeryNegativeCount() {
		return veryNegativeCount;
	}

	public void setVeryNegativeCount(int veryNegativeCount) {
		this.veryNegativeCount = veryNegativeCount;
	}

	public double getVeryNegativePercentage() {
		return veryNegativePercentage;
	}

	public void setVeryNegativePercentage(double veryNegativePercentage) {
		this.veryNegativePercentage = veryNegativePercentage;
	}

	public int getNeutralCount() {
		return neutralCount;
	}

	public void setNeutralCount(int neutralCount) {
		this.neutralCount = neutralCount;
	}

	public double getNeutralPercentage() {
		return neutralPercentage;
	}

	public void setNeutralPercentage(double neutralPercentage) {
		this.neutralPercentage = neutralPercentage;
	}

	public int getPositiveCount() {
		return positiveCount;
	}

	public void setPositiveCount(int positiveCount) {
		this.positiveCount = positiveCount;
	}

	public double getPositivePercentage() {
		return positivePercentage;
	}

	public void setPositivePercentage(double positivePercentage) {
		this.positivePercentage = positivePercentage;
	}

	public int getNegativeCount() {
		return negativeCount;
	}

	public void setNegativeCount(int negativeCount) {
		this.negativeCount = negativeCount;
	}

	public double getNegativePercentage() {
		return negativePercentage;
	}

	public void setNegativePercentage(double negativePercentage) {
		this.negativePercentage = negativePercentage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public double getTotalPercentage() {
		return totalPercentage;
	}

	public void setTotalPercentage(double totalPercentage) {
		this.totalPercentage = totalPercentage;
	}

	public int getOverallPostiveCount() {
		return overallPostiveCount;
	}

	public void setOverallPostiveCount(int overallPostiveCount) {
		this.overallPostiveCount = overallPostiveCount;
	}

	public int getOverallNegativeCount() {
		return overallNegativeCount;
	}

	public void setOverallNegativeCount(int overallNegativeCount) {
		this.overallNegativeCount = overallNegativeCount;
	}

	public double getOverallPositivePercentage() {
		return overallPositivePercentage;
	}

	public void setOverallPositivePercentage(double overallPositivePercentage) {
		this.overallPositivePercentage = overallPositivePercentage;
	}

	public double getOverallNegativePercentage() {
		return overallNegativePercentage;
	}

	public void setOverallNegativePercentage(double overallNegativePercentage) {
		this.overallNegativePercentage = overallNegativePercentage;
	}

	public static String[] getSentimentscores() {
		return sentimentScores;
	}

	

	
	
	}

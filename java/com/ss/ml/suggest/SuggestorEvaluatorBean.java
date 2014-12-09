package com.ss.ml.suggest;

public class SuggestorEvaluatorBean {
	
	private double fMeasure;
	private double precision;
	private double recall;
	private double reach;
	private double timeToBuildModelInSecs;
	
	public SuggestorEvaluatorBean(){}

	public double getfMeasure() {
		return fMeasure;
	}

	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	
	public double getReach() {
		return reach;
	}

	public void setReach(double reach) {
		this.reach = reach;
	}

	public double getTimeToBuildModelInSecs() {
		return timeToBuildModelInSecs;
	}

	public void setTimeToBuildModelInSecs(double timeToBuildModelInSecs) {
		this.timeToBuildModelInSecs = timeToBuildModelInSecs;
	}
	
}

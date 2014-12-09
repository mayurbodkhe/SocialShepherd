package com.ss.ml.forecast;

public class ClassifierEvaluatorBean {
	
	private double fMeasure;
	private double precision;
	private double recall;
	private double truePositive;
	private double falsePositive;
	private double trueNegative;
	private double falseNegative;
	private double errorRate;
	private double rmse;
	private double timeToBuildModelInSecs;
	
	public double getRmse() {
		return rmse;
	}

	public void setRmse(double rmse) {
		this.rmse = rmse;
	}

	public ClassifierEvaluatorBean(){}

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

	public double getTruePositive() {
		return truePositive;
	}

	public void setTruePositive(double truePositive) {
		this.truePositive = truePositive;
	}

	public double getFalsePositive() {
		return falsePositive;
	}

	public void setFalsePositive(double falsePositive) {
		this.falsePositive = falsePositive;
	}

	public double getTrueNegative() {
		return trueNegative;
	}

	public void setTrueNegative(double trueNegative) {
		this.trueNegative = trueNegative;
	}

	public double getFalseNegative() {
		return falseNegative;
	}

	public void setFalseNegative(double falseNegative) {
		this.falseNegative = falseNegative;
	}

	public double getErrorRate() {
		return errorRate;
	}

	public void setErrorRate(double errorRate) {
		this.errorRate = errorRate;
	}

	public double getTimeToBuildModelInSecs() {
		return timeToBuildModelInSecs;
	}

	public void setTimeToBuildModelInSecs(double timeToBuildModelInSecs) {
		this.timeToBuildModelInSecs = timeToBuildModelInSecs;
	}
	
}

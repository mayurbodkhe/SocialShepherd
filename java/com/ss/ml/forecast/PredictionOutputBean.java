package com.ss.ml.forecast;

import java.util.List;

public class PredictionOutputBean {
	
	private List<String> columnHeaders;
	private ClassifierEvaluatorBean evaluatorBean;
	private List<PredictionInputBean> inputBeans;
	
	public PredictionOutputBean(){}
	
	public List<String> getColumnHeaders() {
		return columnHeaders;
	}
	public void setColumnHeaders(List<String> columnHeaders) {
		this.columnHeaders = columnHeaders;
	}
	public ClassifierEvaluatorBean getEvaluatorBean() {
		return evaluatorBean;
	}
	public void setEvaluatorBean(ClassifierEvaluatorBean evaluatorBean) {
		this.evaluatorBean = evaluatorBean;
	}
	public List<PredictionInputBean> getInputBeans() {
		return inputBeans;
	}
	public void setInputBeans(List<PredictionInputBean> inputBeans) {
		this.inputBeans = inputBeans;
	}
	
	

}

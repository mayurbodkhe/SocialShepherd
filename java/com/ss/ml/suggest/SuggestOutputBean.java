package com.ss.ml.suggest;

import java.util.List;

public class SuggestOutputBean {
	
	private List<String> columnHeaders;
	private SuggestorEvaluatorBean evaluatorBean;
	private List<SuggestDataBean> dataBeans;
	
	public SuggestOutputBean(){}
	
	public List<String> getColumnHeaders() {
		return columnHeaders;
	}
	public void setColumnHeaders(List<String> columnHeaders) {
		this.columnHeaders = columnHeaders;
	}
	public SuggestorEvaluatorBean getEvaluatorBean() {
		return evaluatorBean;
	}
	public void setEvaluatorBean(SuggestorEvaluatorBean evaluatorBean) {
		this.evaluatorBean = evaluatorBean;
	}

	public List<SuggestDataBean> getDataBeans() {
		return dataBeans;
	}

	public void setDataBeans(List<SuggestDataBean> dataBeans) {
		this.dataBeans = dataBeans;
	}
	
	
}

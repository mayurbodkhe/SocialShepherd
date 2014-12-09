package com.ss.ml.suggest;

public class RecommendationTableBean {
	
	private String dataSource;
	private String swotText;
	private int swotCount;
	
	public RecommendationTableBean (){}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getSwotText() {
		return swotText;
	}

	public void setSwotText(String swotText) {
		this.swotText = swotText;
	}

	public int getSwotCount() {
		return swotCount;
	}

	public void setSwotCount(int swotCount) {
		this.swotCount = swotCount;
	}
	
}

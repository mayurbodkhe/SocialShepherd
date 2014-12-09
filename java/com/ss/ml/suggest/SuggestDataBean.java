package com.ss.ml.suggest;

public class SuggestDataBean {

	private String userName;
	private String suggestedItem;
	
	
	public SuggestDataBean () {}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSuggestedItem() {
		return suggestedItem;
	}
	public void setSuggestedItem(String suggestedItem) {
		this.suggestedItem = suggestedItem;
	}
		
}

package com.ss.ml.opinion.miner.stanford;

import java.util.Set;

public class FeatureOpinion {
	
	private String feature;
	private Set<String> opinions;
	private String featureOpinionText;
	
	public FeatureOpinion(){}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public Set<String> getOpinions() {
		return opinions;
	}

	public void setOpinions(Set<String> opinions) {
		this.opinions = opinions;
	}

	public String getFeatureOpinionText() {
		return featureOpinionText;
	}

	public void setFeatureOpinionText(String featureOpinionText) {
		this.featureOpinionText = featureOpinionText;
	}
	
	
	

}

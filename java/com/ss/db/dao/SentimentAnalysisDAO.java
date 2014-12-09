package com.ss.db.dao;

import java.util.List;
import java.util.Map;
//import com.ss.ml.opinion.miner.stanford.EntityDetails;
import com.ss.ml.opinion.miner.stanford.SentimentAnalysisResponse;
import com.ss.ml.opinion.miner.stanford.SentimentTableBean;

public interface SentimentAnalysisDAO {
	
	public void insertSentenceDetails(int dataSourceId, List<SentimentAnalysisResponse> responses);
	
	public List<SentimentTableBean> getSentimentByCount(int count);
	
	//public List<EntityDetails> getEntitiesByCount(int count);
	
	public Map<String, Integer> getTopKFeatureBySentimentType(int count, int sentimentType);
	
	public Map<String, Integer> getTopKOpinionBySentimentType(int count, int sentimentType);
	
	public Map<String, Integer> getTopKSWOTFeatureBySWOTType(int count, int swotType);
	
	public String getCompleteSentenceById(int sentenceId);
	
	public Map<String, Integer> getOverallSentiment();
	
	public Map<String, Integer> getTopKSourceBySentimentType(int count, int sentimentType);
	
	public Map<String, Integer> getTopKEntityBySentimentType(int count, int sentimentType);
	
}

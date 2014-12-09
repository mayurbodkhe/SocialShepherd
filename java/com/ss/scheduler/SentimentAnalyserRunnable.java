package com.ss.scheduler;

import java.util.List;

import com.ss.ml.opinion.miner.stanford.SentimentAnalysisResponse;
import com.ss.ml.opinion.miner.stanford.StanfordCoreNLPSentimentProcessor;
import com.ss.services.DBService;

public class SentimentAnalyserRunnable implements Runnable{
	 
	private String text;
	     
	public SentimentAnalyserRunnable(String s){
	        this.text=s;
	}
	 
	@Override
	public void run() {
		try {
			StanfordCoreNLPSentimentProcessor processor = new StanfordCoreNLPSentimentProcessor();
			List<SentimentAnalysisResponse> responses = processor.getSentiment(text);
			DBService.getInstance().getSentimentAnalysisDAO().insertSentenceDetails(1, responses);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	 
	 @Override
	 public String toString(){
		 return this.text;
    }
}


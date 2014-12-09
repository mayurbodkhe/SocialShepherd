package com.ss.services;

import com.ss.ml.forecast.Predictor;
import com.ss.ml.opinion.miner.stanford.StanfordCoreNLPSentimentProcessor;
import com.ss.ml.suggest.Suggestor;

public class MLService {
	
	private static MLService mlService;
	
	private Predictor predictor;
	private Suggestor suggestor;
	private StanfordCoreNLPSentimentProcessor sentimentProcessor;

	
	private MLService() {
		this.predictor = new Predictor();
		this.sentimentProcessor = new StanfordCoreNLPSentimentProcessor();
		this.suggestor = new Suggestor();
	}

	public static MLService getInstance(){
		if(mlService == null)
			mlService = new MLService();
		
		return mlService;
	}
	
	public Predictor getPredictor() {
		return predictor;
	}

	public void setPredictor(Predictor predictor) {
		this.predictor = predictor;
	}

	public Suggestor getSuggestor() {
		return suggestor;
	}

	public void setSuggestor(Suggestor suggestor) {
		this.suggestor = suggestor;
	}

	public StanfordCoreNLPSentimentProcessor getSentimentProcessor() {
		return sentimentProcessor;
	}

	public void setSentimentProcessor(
			StanfordCoreNLPSentimentProcessor sentimentProcessor) {
		this.sentimentProcessor = sentimentProcessor;
	}
	

}

package com.ss.ml.opinion.miner.stanford;

import java.util.List;

public interface SentimentProcessor {

	public List<SentimentAnalysisResponse> getSentiment(String text);
}

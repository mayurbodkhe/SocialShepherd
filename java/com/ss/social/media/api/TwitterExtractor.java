package com.ss.social.media.api;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ss.ml.opinion.miner.stanford.SentimentAnalysisResponse;
import com.ss.ml.opinion.miner.stanford.StanfordCoreNLPSentimentProcessor;
import com.ss.services.DBService;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;

public class TwitterExtractor {
	
	private static final Logger logger = Logger.getLogger(TwitterExtractor.class);
	
	public List<TweetInfo> getTweetsTaggedByCount(String tag, int tweetCount){
		
		Query twitterQuery = null;
		QueryResult twitterQueryResult = null;
		List<Status> tweets = null;
		List<TweetInfo> tweetsInfos = null;
		TweetInfo tweetInfo = null;

		try {
			
			long tweetExtractStartTime = System.currentTimeMillis();
			
			twitterQuery = new Query("#"+tag);
			twitterQuery.count(tweetCount);
	           
			twitterQueryResult = TwitterConnection.getInstance().getConnection().search(twitterQuery);
			tweets = twitterQueryResult.getTweets();
			tweetsInfos = new ArrayList<TweetInfo>();
	       
			for(Status tweet : tweets){
				tweetInfo = new TweetInfo();
	            tweetInfo.setUser(tweet.getUser().getScreenName());
	            tweetInfo.setText(tweet.getText());
	            
	            tweetsInfos.add(tweetInfo);
			}
	        
			DecimalFormat df = new DecimalFormat("#.##");
			logger.info("Finished Tweets search in "+Double.valueOf(df.format((double) (System.currentTimeMillis() - tweetExtractStartTime)/1000.0))+"(s)");
			
		} catch (Exception ex) {
			logger.info("Error while extracting Tweets", ex);
	    }
		 
		return tweetsInfos;
	}
	
	public static void main(String[] args) {
		
		
		
		List<TweetInfo> tweetInfos = new TwitterExtractor().getTweetsTaggedByCount("TelecomRegulatoryAuthorityOfIndia﻿", 100);
		
		
		StanfordCoreNLPSentimentProcessor processor = new StanfordCoreNLPSentimentProcessor();
		for(TweetInfo tweetInfo:tweetInfos){
		List<SentimentAnalysisResponse> responses = processor.getSentiment(tweetInfo.getText());
		DBService.getInstance().getSentimentAnalysisDAO().insertSentenceDetails(2, responses);
		}
	}

}

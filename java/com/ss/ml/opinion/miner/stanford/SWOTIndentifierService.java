package com.ss.ml.opinion.miner.stanford;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


/**
* <h1>SWOTIndentifierService</h1>
* The class separates Opinions into SWOT catergories
* At present hard coded words are used for categorisation, accurracy can be further boosted by lemmatisation 
*
* @author  Mayur Bodakhe
* @version 1.0
* @since   2014-11-09
*/
public class SWOTIndentifierService {
	
	private static final Logger logger = Logger.getLogger(SWOTIndentifierService.class);
	
	 public static enum SWOT_TYPE {
	        
		 STRENGTH(1), WEEKNESS(2), OPPORTUNITY(3), THREAT(4);
	        private int type;

	        private SWOT_TYPE(int value) {
	                type = value;
	        }
	        
	        public int getType(){
	        	return type;
	        }
	};   
	
	//STRENGTHS
	public static final String[] STRENGTH_WORDS = {"expert", "super", "available", "strong", "good", "best", "excellent", "best", "spare", "advantage", "lowest", "quality", "reliable", "performance", "confidence", "right"};

	//WEEKNESSES
	public static final String[] WEEKNESS_WORDS = {"complex", "costly", "streched", "limited", "training", "insufficient", "small", "short", "shortage", "naive"};
	
	//OPPORTUNITIES
	public static final String[] OPPORTUNITY_WORDS = {"improvement", "will", "would", "should", "could", "surprise", "seek", "can", "favorable", "improve", "average"};
	
	//THREATS
	public static final String[] THREAT_WORDS = {"delay", "constraints", "critical", "negative", "bad", "worst", "poor", "lack", "can", "unfavorable"};
	
	
	public static Map<String,List<String>> getOpinionBySWOTType (List<SentimentAnalysisResponse> responses, int swotType){
		
		List<SentimentDetails> sentimentDetails = null;
		Set<String> sentiments = null;
		List<String> opinionList = null;
		String feature = null;
		
		Map<String, List<String>> returnMap = null;
		List<String> foundStrengthList = null;
		try{
			
			if(swotType == SWOT_TYPE.STRENGTH.getType())
				opinionList = Arrays.asList(STRENGTH_WORDS);
			else if(swotType == SWOT_TYPE.WEEKNESS.getType())
				opinionList = Arrays.asList(WEEKNESS_WORDS);
			else if(swotType == SWOT_TYPE.OPPORTUNITY.getType())
				opinionList = Arrays.asList(OPPORTUNITY_WORDS);
			else if(swotType == SWOT_TYPE.THREAT.getType())
				opinionList = Arrays.asList(THREAT_WORDS);
					
			returnMap = new LinkedHashMap<String, List<String>>();
			
			for(SentimentAnalysisResponse response: responses){
				
				sentimentDetails = getAllSentimentsCombined(response);
				
				if(sentimentDetails != null && sentimentDetails.size()>0){
					
					for(SentimentDetails detail: sentimentDetails){
						
						sentiments = detail.getSentiments();
						feature = detail.getFeature();
						
						if(sentiments != null && sentiments.size()>0){
							
							foundStrengthList = new LinkedList<String>();
							
							for(String sentiment: sentiments){
								
								if(opinionList.contains(sentiment)){
									
									foundStrengthList.add(sentiment);
								}
							}
							
							if(foundStrengthList != null && foundStrengthList.size()>0){
								returnMap.put(feature, foundStrengthList);
							}
						}
					}
				}
			}
		
		}catch(Exception ex){
			logger.error("Error durirng getStrengthFeature", ex);
		}
		
		return returnMap;
		
	}
	
	public static Map<String,List<String>> getWeeknessFeature (List<SentimentAnalysisResponse> responses){
		
		List<SentimentDetails> sentimentDetails = null;
		Set<String> sentiments = null;
		List<String> strengthsList = null;
		String feature = null;
		
		Map<String, List<String>> returnMap = null;
		List<String> foundStrengthList = null;
		try{
			
			strengthsList = Arrays.asList(STRENGTH_WORDS);
			
			returnMap = new LinkedHashMap<String, List<String>>();
			
			for(SentimentAnalysisResponse response: responses){
				
				sentimentDetails = response.getPositive();
				
				if(sentimentDetails != null && sentimentDetails.size()>0){
					
					for(SentimentDetails detail: sentimentDetails){
						
						sentiments = detail.getSentiments();
						feature = detail.getFeature();
						
						if(sentiments != null && sentiments.size()>0){
							
							foundStrengthList = new LinkedList<String>();
							
							for(String sentiment: sentiments){
								
								if(strengthsList.contains(sentiment)){
									
									foundStrengthList.add(sentiment);
								}
							}
							
							if(foundStrengthList != null && foundStrengthList.size()>0){
								returnMap.put(feature, foundStrengthList);
							}
						}
					}
				}
			}
		
		}catch(Exception ex){
			logger.error("Error durirng getStrengthFeature", ex);
		}
		
		return returnMap;
		
	}
	
	
	private static List<SentimentDetails> getAllSentimentsCombined(SentimentAnalysisResponse response){
		
		List<SentimentDetails> list = null;
		
		try{
			
			list = new LinkedList<SentimentDetails>();
			
			if(response.getNegative() != null && response.getNegative().size()>0)
				list.addAll(response.getNegative());
			
			if(response.getNeutral() != null && response.getNeutral().size()>0)
				list.addAll(response.getNeutral());
			
			if(response.getPositive() != null && response.getPositive().size()>0)
				list.addAll(response.getPositive());
			
		}catch(Exception ex){
			logger.error("Error during getAllSentimentsCombined", ex);
		}
		
		return list;
	}
	
}

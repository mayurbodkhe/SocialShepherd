package com.ss.services.rest;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.ss.ml.opinion.miner.stanford.SentimentAnalysisResponse;
import com.ss.services.DataPullerService;
import com.ss.services.MLService;
import com.ss.social.media.api.TweetInfo;

@Path("/getTweetSA")
public class SentimentAnalyserREST {

	public static final int tweetReturnCount = 5;
	private static final Logger logger = Logger.getLogger(SentimentAnalyserREST.class);
	
	@GET
	@Path("/{param}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTweetSentiment(@PathParam("param") String hashTag) {
 
		List<SentimentAnalysisResponse> response = null;
		List<TweetInfo> tweetList = null;
		List<SentimentAnalysisResponse> tweetResponse = null;
		ObjectMapper mapper;
		String outputJSONAsString = "";
		
		
		try{
			response = new LinkedList<SentimentAnalysisResponse>();
			
			tweetList = DataPullerService.getInstance().getTwitterExtractor().getTweetsTaggedByCount(hashTag, tweetReturnCount);
			
			if(tweetList != null && tweetList.size()>0){
				for(TweetInfo tweetInfo: tweetList){
					tweetResponse = MLService.getInstance().getSentimentProcessor().getSentiment(tweetInfo.getText());
					response.addAll(tweetResponse);
				}
			}else{
				SentimentAnalysisResponse res = new SentimentAnalysisResponse();
				res.setRawTex("EMPTY_HASHTAG");
				response.add(res);
			}
		
			mapper = new ObjectMapper();
			outputJSONAsString = mapper.writeValueAsString(response);
			
		} catch (Exception ex) {
			logger.error("SentimentAnalyserREST Error : ", ex);
		
		}
		return outputJSONAsString;
	}

}

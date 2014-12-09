package com.ss.ml.opinion.miner.stanford;

//import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class SentimentAnalysisResponse implements Serializable {
	
	private static final long serialVersionUID = -4880925640373343339L;

	private SentimentAggregate aggregate;
	
	private List<SentimentDetails> negative;
	private List<SentimentDetails> neutral;
	private List<SentimentDetails> positive;
	
	private List<EntityDetails> entities;
	
	private String rawTex;
	
	public SentimentAnalysisResponse (){}

	public SentimentAggregate getAggregate() {
		return aggregate;
	}

	public void setAggregate(SentimentAggregate aggregate) {
		this.aggregate = aggregate;
	}

	public List<SentimentDetails> getNegative() {
		return negative;
	}

	public void setNegative(List<SentimentDetails> negative) {
		this.negative = negative;
	}

	public List<SentimentDetails> getNeutral() {
		return neutral;
	}

	public void setNeutral(List<SentimentDetails> neutral) {
		this.neutral = neutral;
	}

	public List<SentimentDetails> getPositive() {
		return positive;
	}

	public void setPositive(List<SentimentDetails> positive) {
		this.positive = positive;
	}

	public List<EntityDetails> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityDetails> entities) {
		this.entities = entities;
	}
	
	
	
	
	public String getRawTex() {
		return rawTex;
	}

	public void setRawTex(String rawTex) {
		this.rawTex = rawTex;
	}

//	public SentimentAnalysisResponse analyseSentimentUsingText(String text, String language) {
//        
//        String response = null;
//        try {
//            
//            ObjectMapper mapper = new ObjectMapper();
//            JsonParser parse = new JsonFactory().createJsonParser(response);
//            SentimentAnalysisResponse resp = mapper.readValue(parse,SentimentAnalysisResponse.class);
//            return resp;
//        } catch (JsonMappingException e){
//            System.out.println("error encountered for response " + response + " with text " + text);
//            return null;
//        } catch (IOException e) {
//        	System.out.println("Exception encountered when trying to analyse sentiment");
//            return null;
//        }
//    }
}
	


	



	
	

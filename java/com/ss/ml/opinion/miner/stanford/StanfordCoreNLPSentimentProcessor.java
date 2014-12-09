package com.ss.ml.opinion.miner.stanford;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.ss.services.DBService;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

/**
* <h1>StanfordCoreNLPSentimentProcessor</h1>
* The class provides Sentiment Analysis functionalities
* with help of Stanford CoreNLP API 
*
* @author  Mayur Bodakhe
* @version 1.0
* @since   2014-11-09
*/
public class StanfordCoreNLPSentimentProcessor implements SentimentProcessor{

	private static final Logger logger = Logger.getLogger(StanfordCoreNLPSentimentProcessor.class);
	
	/** 5 Ways Sentiment Analysis Scoring supported */
	public static final String[] sentimentScores = { "Very Negative","Negative", "Neutral", "Positive", "Very Positive"};
	
	/** Default Sentiment Level */
	private static final int DEFAULT_SENTIMENTSCORE = 2;
	
	private static StanfordCoreNLP pipeline;
	
	public StanfordCoreNLPSentimentProcessor(){
		
		if(pipeline == null){
			Properties props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment, ner");
			props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	
			pipeline = new StanfordCoreNLP(props);	
		}
	}
	
	/**
     * This method provides Sentiment statistics for given text with help of Stanford CoreNLP API
     * @param Input Text
     * @return SentimentAnalysisResponse object list
     */
	@Override
	public List<SentimentAnalysisResponse> getSentiment(String text) {
		
		SentimentAnalysisResponse response = null;
		List<SentimentAnalysisResponse> responseList = null;
		
		SentimentAggregate aggregate;
		
		List<SentimentDetails> negative;
		List<SentimentDetails> neutral;
		List<SentimentDetails> positive;
		
		List<EntityDetails> entities;
		EntityDetails entityDetails = null;
		
		List<SentimentAnalyisRawDetails> rawDetailsList = null;
		
		List<String> features;
		List<String> opinions;
		
		int featureSentimentScore = DEFAULT_SENTIMENTSCORE;
	
		SentimentDetails sentimentDetails = null;
		
		
		try{
			
			long saStart = System.currentTimeMillis();
			
			//#1 - Obtain Raws Sentiment Statistics
			rawDetailsList = processText(text);
			
			if(rawDetailsList != null && rawDetailsList.size() > 0){
				
				responseList = new LinkedList<SentimentAnalysisResponse>();
				
				for(SentimentAnalyisRawDetails details: rawDetailsList){
					
					response = new SentimentAnalysisResponse();
					
					aggregate = new SentimentAggregate();
					negative = new LinkedList<SentimentDetails>();
					neutral = new LinkedList<SentimentDetails>();
					positive = new LinkedList<SentimentDetails>();
					entities = new LinkedList<EntityDetails>();
					
					aggregate.setScore(details.getScore());
					aggregate.setSentiment(details.getSentiment());
					response.setAggregate(aggregate);
					
					if(details.getEntities() != null && details.getEntities().size() >0){
						for(Map.Entry<String, String> entry: details.getEntities().entrySet()){
							entityDetails = new EntityDetails();
							entityDetails.setEntityName(entry.getKey());
							entityDetails.setEntityType(entry.getValue());
							
							if(!entry.getValue().equalsIgnoreCase("DURATION") &&
							   !entry.getValue().equalsIgnoreCase("NUMBER") &&
							   !entry.getValue().equalsIgnoreCase("DATE") &&
							   !entry.getValue().equalsIgnoreCase("MONEY") &&
							   !entry.getValue().equalsIgnoreCase("ORDINAL") &&
							   !entry.getValue().equalsIgnoreCase("PERCENT") &&
							   !entry.getValue().equalsIgnoreCase("SET") &&
							   !entry.getValue().equalsIgnoreCase("TIME")){
								entities.add(entityDetails);
							}
						}
					}
					
					features = details.getFeatures();
					opinions = details.getOpinions();
					
					if(features !=null && features.size() >0){
						for(String feature: features){
							
							featureSentimentScore = details.getScore().intValue();
							
							sentimentDetails = new SentimentDetails();
							sentimentDetails.setFeature(feature);
							sentimentDetails.setScore((double)featureSentimentScore);
							sentimentDetails.setSentiments(Sets.newHashSet(opinions));
							
							if(featureSentimentScore == 0 || featureSentimentScore == 1){
								negative.add(sentimentDetails);
									
							}else if(featureSentimentScore == 2){
								neutral.add(sentimentDetails);
									
							}else if(featureSentimentScore == 3 || featureSentimentScore == 4){
								positive.add(sentimentDetails);
							} 
						}
					}
					//#4 - Add Entities, Negative, Neutral & Positive Feature for each sentence
					response.setEntities(entities);
					response.setNegative(negative);
					response.setNeutral(neutral);
					response.setPositive(positive);
					response.setRawTex(details.getRawText());
					
					responseList.add(response);
				}
			}
			
			DecimalFormat df = new DecimalFormat("#.##");
	        logger.info("SA Done in  "+ Double.valueOf(df.format((System.currentTimeMillis() - saStart)/1000)) + " (s)");
			
		}catch(Exception ex){
			logger.error("SA Error : ", ex);
		}
		
		return responseList;
	}
	
	
	
	/**
     * This method provides Raw Sentiment statistics for given text with help of Stanford CoreNLP API
     * @param Input Text
     * @return SentimentAnalyisRawDetails object list
     */
	private List<SentimentAnalyisRawDetails> processText(String text){
		
		List<SentimentAnalyisRawDetails> rawDetails = null;
		SentimentAnalyisRawDetails details = null;
		
		int score = DEFAULT_SENTIMENTSCORE;
	
		List<String> features;
		List<String> opinions;
		List<RelationShipDetails> relations;
		Map<String, String> entities;
		
		TreebankLanguagePack treebankLanguagePack;
		GrammaticalStructureFactory structureFactory;
		GrammaticalStructure structure;
		Collection<TypedDependency> typedDependencies;
		Object[] typedDependenciesList;
		
		String tokenWORD, tokenPOS, tokenNE;
		
		RelationShipDetails relationShipDetails = null;
		
		Annotation annotation = null;
		
		try{
			
			rawDetails = new LinkedList<SentimentAnalyisRawDetails>();
		
			annotation = pipeline.process(text);
					
			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				
				relations = new LinkedList<RelationShipDetails>();
				entities = new LinkedHashMap<String, String>();
				
				features = new LinkedList<String>();
				opinions = new LinkedList<String>();
				
				Tree sentimentTree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
				score = RNNCoreAnnotations.getPredictedClass(sentimentTree);
				
				//#3 - Find Relationship details
				Tree nerTree = sentence.get(TreeAnnotation.class);
				treebankLanguagePack = new PennTreebankLanguagePack();
				structureFactory = treebankLanguagePack.grammaticalStructureFactory();
				structure = structureFactory.newGrammaticalStructure(nerTree);
				typedDependencies = structure.typedDependenciesCollapsed();
				typedDependenciesList = typedDependencies.toArray();
				
				for (Object object : typedDependenciesList) {
					TypedDependency typedDependency = (TypedDependency) object;
					
					relationShipDetails = new RelationShipDetails();
					relationShipDetails.setRelationshipGov(typedDependency.gov().nodeString());
					relationShipDetails.setRelationshipDep(typedDependency.dep().nodeString());
					relationShipDetails.setRelationshipType(typedDependency.reln().toString());
					
					relations.add(relationShipDetails);
					
				}
				
				
				//#4 - Find POS and NER details
				for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
			       
					tokenWORD = token.get(TextAnnotation.class);
					tokenPOS = token.get(PartOfSpeechAnnotation.class);
					tokenNE = token.get(NamedEntityTagAnnotation.class);
					
					if("NN".equalsIgnoreCase(tokenPOS)){
						
						features.add(tokenWORD);
						
					}else if("JJ".equalsIgnoreCase(tokenPOS)  ||
							 "JJS".equalsIgnoreCase(tokenPOS) ||
							 "JJR".equalsIgnoreCase(tokenPOS)){
						
						opinions.add(tokenWORD);
					}
					
					if(tokenNE != null && !"O".equalsIgnoreCase(tokenNE)) {
						entities.put(tokenWORD, tokenNE);
					}
					
				}
				
				
				details = new SentimentAnalyisRawDetails();
				
				//#6 - Create SentimentAnalyisRawDetails
				details.setSentiment(sentimentScores[score]);
				details.setScore((double)score);
				
				details.setRelations(relations);
							
				details.setFeatures(features);
				details.setOpinions(opinions);
				
				details.setEntities(entities);
				
				details.setRawText(sentence.toString());
				
				//#7 - Add to List
				rawDetails.add(details);
			}
			 
			
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
		
		
		return rawDetails;
	}
	
	
	
	
	
	public static void main(String[] args) throws IOException {
	
//		String text = "I want to register complaint against Airtel. I have gone almost mad and harassed because of these non-sense people. I was using airtel broadband connection for internet. I have submitted the request to disconnect this service in August, 2014. I called customer care that kindly disconnect this, replying to which they said lets put this on hold for a month and after one month if you still don't want to continue then we will disconnect and in this period of hold you will not be charged anything.Also, I wasn't using this connection in this period of hold. After one month, customer care executive called me i.e. in end of September and asked to pay bill for September also. I said it was under the period of hold and you people have said that you will not be charged anything in period of hold so why should I pay???? They said they don't have proof regarding that I have put the connection on the hold even when customer care executive said that he put the connection on hold from his side. Moreover, now when I am asking them to disconnect this service since past 15 days they are not even putting the request for this and I am getting charged for this month also even when I haven't used this service since 30, August 2014."+
//				"When I call customer care they talk for 1 hour and waste lot of time. They even put phone on hold for 15-20 minutes. This is completely a case of mental harassment and financial loss." +
//				"I request you to please help at the earliest.";
//		StanfordCoreNLPSentimentProcessor processor = new StanfordCoreNLPSentimentProcessor();
//		List<SentimentAnalysisResponse> responses = processor.getSentiment(tweetText);
//		for(SentimentAnalysisResponse response: responses){
//			System.out.println(response.getAggregate().getSentiment());
//			
//			ObjectMapper mapper = new ObjectMapper();
//			System.out.println(mapper.writeValueAsString(response));
//		}
		
		
		String textArea [] = {"Moody's Investors Service has said Bharti Airtel's decision to sell approximately 4,800 of its telecommunications towers in Nigeria is credit positive."+
"The Sunil Bharti Mittal-led company sold the towers to US based American Towers Company for about $1.05 billion in a move to pare debt."+
"This transaction is credit positive as it will allow Bharti to pay down balance sheet debt, reduce its interest cost, and reduce total capital expenditure on passive infrastructure investments,Moody's Vice President and Senior Analyst Annalisa DiChiara said in a statement on Friday."+
"Bharti Airtel's debt at the end of the September quarter was Rs 62,215.8 crore."+
"Shares of the company were on Friday trading 0.56 per cent lower at Rs 383.70 on the Bombay Stock Exchange"};
			
		StanfordCoreNLPSentimentProcessor processor = new StanfordCoreNLPSentimentProcessor();
		for(String text123:textArea){
		List<SentimentAnalysisResponse> responses = processor.getSentiment(text123);
		DBService.getInstance().getSentimentAnalysisDAO().insertSentenceDetails(4, responses);
		}
		
		String textArea2 [] = {"Airtel and Vodafone at present hold 8 Mhz spectrum each in premium 900 Mhz band in Delhi, but they have to vacate them and shift to their newly acquired frequencies in 900 Mhz band. In the auction, Airtel could win back only 6 Mhz and Vodafone 5 Mhz of spectrum in the same band."+
				"The licences of Bharti Airtel and Vodafone are expiring in Delhi on November 29."+
				"The two companies also bought some spectrum in 1800 Mhz band to make up for spectrum they lost in 900 Mhz band."+
				"Mobile signals transmitted in 900 Mhz band cover almost double area compared to signals transmitted in 1800 Mhz band."+
				"Sectoral regulator TRAI had earlier warned that telecom services in national capital may be disrupted partially from December due to delays by DoT in fresh spectrum allocation to Airtel and Vodafone."+
				"These operators have about 20 million mobile subscribers on their networks in Delhi or in other words 45 per cent of the total subscriber base here."};
		
		for(String text123:textArea2){
			List<SentimentAnalysisResponse> responses = processor.getSentiment(text123);
			DBService.getInstance().getSentimentAnalysisDAO().insertSentenceDetails(5, responses);
			}
	}

}

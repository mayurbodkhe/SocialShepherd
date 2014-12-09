package com.ss.ml.suggest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVParser;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.RandomUtils;


/**
* <h1>Predictor</h1>
* The class behaves like Suggestor by implementing 
* Maching Learning Recommendation Algorithms from Apache Mahout
*
* @author  Mayur Bodakhe
* @version 1.0
* @since   2014-11-09
*/

public class Suggestor {
	
	/**
	 * Log class which is used for sophisticated error
	 * logging.
	 */
	private Logger log = Logger.getLogger(Suggestor.class.getName());
	
	private static final String tableHeaders[] = new String[]{"UserName", "Suggested Iterm"};
	
	/** DataModel to use */
	private DataModel dataModel;
	
	/** ItemSimilarity Algorithm to use */
	private ItemSimilarity itemSimilarity;
		
	/** Recommender Algorithm to use */
	private Recommender recommender;
		
	/** Input Data Fully Qualified Path */
	private String inputDataSetAbsolutePath;
	
	/**
	 * Number of recommendation to be done
	 */
	private int recommendationCount = 5;
	
	/**
	 * An MemoryIDMigrator which is able to create for every string
	 * a long representation. Further it can store the string which
	 * were put in and it is possible to do the mapping back.
	 */
	private MemoryIDMigrator id2thing = new MemoryIDMigrator();
	
	
	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public ItemSimilarity getUserSimilarity() {
		return itemSimilarity;
	}

	public void setUserSimilarity(ItemSimilarity itemSimilarity) {
		this.itemSimilarity = itemSimilarity;
	}

	public Recommender getRecommender() {
		return recommender;
	}

	public void setRecommender(Recommender recommender) {
		this.recommender = recommender;
	}

	public String getInputDataSetAbsolutePath() {
		return inputDataSetAbsolutePath;
	}

	public void setInputDataSetAbsolutePath(String inputDataSetAbsolutePath) {
		this.inputDataSetAbsolutePath = inputDataSetAbsolutePath;
	}

	public int getRecommendationCount() {
		return recommendationCount;
	}

	public void setRecommendationCount(int recommendationCount) {
		this.recommendationCount = recommendationCount;
	}

	public Suggestor(){
		
	}
	
	
	public void runDefaultSuggestor(String filePath) {
		 
		File file = null;
		Map<Long,List<Preference>> preferecesOfUsers = null;
		CSVParser parser = null;
		FastByIDMap<PreferenceArray> preferecesOfUsersFastMap = null;
		String[] line = null;
		String userName = null;
		String itemSuggested = null;
		long userLong, itemLong;
		List<Preference> userPrefList = null;
		@SuppressWarnings("unused")
		String header [] = null;
		
		try {

			//1. Read Input File CSV
			file = new File(filePath);
			
			//2.  use a CSV parser for reading the file
			// use UTF-8 as character set
			 parser = new CSVParser(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			
			
			//3. create a map for saving the preferences (likes) for
		     preferecesOfUsers = new HashMap<Long,List<Preference>>();
						
						
			//4. parse out the header
			header = parser.getLine();
							
			while((line = parser.getLine()) != null) {
						
				userName = line[0];
				itemSuggested = line[1];
			
							
				//5. create a long from the person name
				userLong = id2thing.toLongID(userName);
							
				//6. store the mapping for the user
				id2thing.storeMapping(userLong, userName);
							
				//7. create a long from the like name
				itemLong = id2thing.toLongID(itemSuggested);
							
				//8. store the mapping for the item
				id2thing.storeMapping(itemLong, itemSuggested);
							
				//9. create user preference list			
				if((userPrefList = preferecesOfUsers.get(userLong)) == null) {
					userPrefList = new ArrayList<Preference>();
					preferecesOfUsers.put(userLong, userPrefList);
				}
				
				//10. add the like that we just found to this user
				userPrefList.add(new GenericPreference(userLong, itemLong, 1));
							log.fine("Adding "+userName+"("+userLong+") to "+itemSuggested+"("+itemLong+")");
				}
						
				//11. create the corresponding mahout data structure from the map
				preferecesOfUsersFastMap = new FastByIDMap<PreferenceArray>();
				for(Entry<Long, List<Preference>> entry : preferecesOfUsers.entrySet()) {
					preferecesOfUsersFastMap.put(entry.getKey(), new GenericUserPreferenceArray(entry.getValue()));
			}

								
			//12. Load file into DataModel
			this.dataModel = new GenericDataModel(preferecesOfUsersFastMap); 
			
			//13. Itembased LogLikelihoodSimilarity on datamodel
			this.itemSimilarity = new LogLikelihoodSimilarity(dataModel);
			
			//14. User GenericUserBasedRecommender on datamodel
			this.recommender = new GenericItemBasedRecommender(this.dataModel, this.itemSimilarity);
		    

		} catch (Exception e) {
			log.log(Level.SEVERE, "Error during initSuggestor", e);
		}
	}
	
	
	
	
	public SuggestOutputBean getSuggestions(){
		
		SuggestOutputBean outputBean = null;
	    List<SuggestDataBean> dataBeans = null;
	    long userId = 0;
	    SuggestDataBean dataBean = null;
	    String[] suggestionsForUser = null;
	   
			
		try{
		
			//1. get ColumnHeader
			outputBean = new SuggestOutputBean();
			outputBean.setColumnHeaders(Arrays.asList(tableHeaders));
			
			dataBeans = new LinkedList<SuggestDataBean>();
			
			//2. Evaluate Classifier
			outputBean.setEvaluatorBean(evaluateClassifier(this.dataModel, this.itemSimilarity));
			
			//3. Obtain Suggestions
			for (LongPrimitiveIterator iterator = this.dataModel.getUserIDs(); iterator.hasNext();)
		      {
		        userId = iterator.next();
			 	suggestionsForUser =  suggestItemsByUserName(this.id2thing.toStringID(userId), this.recommendationCount);
				
				if (suggestionsForUser != null && suggestionsForUser.length >0)
		          {
		        	  for(String suggestedItem: suggestionsForUser){
		        		  	dataBean = new SuggestDataBean();
		        	  		dataBean.setUserName(this.id2thing.toStringID(userId));
		        	  		dataBean.setSuggestedItem(suggestedItem);
		        	  		dataBeans.add(dataBean);
		        	  }
		          }
		          else
		          {
		        	  	dataBean = new SuggestDataBean();
	        	  		dataBean.setUserName(this.id2thing.toStringID(userId));
	        	  		dataBean.setSuggestedItem("NO_SUGGESTIONS");
	        	  		dataBeans.add(dataBean);
		             
		          }
		      }
			
			outputBean.setDataBeans(dataBeans);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return outputBean;
	}
	
	
	
	public String[] suggestItemsByUserName(String userName, int suggestCount) throws TasteException {
		List<String> recommendations = new ArrayList<String>(); 
		try {
			List<RecommendedItem> items = this.recommender.recommend(id2thing.toLongID(userName), suggestCount);
			for(RecommendedItem item : items) {
				recommendations.add(id2thing.toStringID(item.getItemID()));
			}
		} catch (TasteException e) {
			log.log(Level.SEVERE, "Error during retrieving recommendations", e);
			throw e;
		}
		return recommendations.toArray(new String[recommendations.size()]);
	}
	
	
	private SuggestorEvaluatorBean evaluateSuggestor(final DataModel currentDataModel, final ItemSimilarity currenItemSimilarity){
		
		SuggestorEvaluatorBean evaluatorBean = null;
		DecimalFormat df = null;
		long start = 0;
		
		RecommenderIRStatsEvaluator recommenderEvaluator = null;
		RecommenderBuilder recommenderBuilder = null;
		IRStatistics eval = null;
		
		try{
			
			start = System.currentTimeMillis();
			
			RandomUtils.useTestSeed();
			
			evaluatorBean = new SuggestorEvaluatorBean();
			
			recommenderEvaluator = new GenericRecommenderIRStatsEvaluator();
		    
			recommenderBuilder = new RecommenderBuilder() {
		          public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		              ItemSimilarity itemSimilarity = currenItemSimilarity;
		              return new GenericItemBasedRecommender(currentDataModel, itemSimilarity);
		          }
		      };
		         
		      eval = recommenderEvaluator.evaluate(
		            		  recommenderBuilder, null, currentDataModel,
		                      null, 2, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		      
	     
		     df = new DecimalFormat("#.##");
		     evaluatorBean.setTimeToBuildModelInSecs(Double.valueOf(df.format((double) (System.currentTimeMillis() - start)/1000.0)));
		     
		     
		     evaluatorBean.setfMeasure(Double.valueOf(df.format(eval.getFallOut())));	 
		  	 evaluatorBean.setPrecision(Double.valueOf(df.format(eval.getPrecision())));	 
		  	 evaluatorBean.setRecall(Double.valueOf(df.format(eval.getRecall())));	 
		  	 evaluatorBean.setReach(Double.valueOf(df.format(eval.getReach())));	 
		 
		}catch (Exception ex){
			ex.printStackTrace();
		}
		
		return evaluatorBean;
	}
	
	private SuggestorEvaluatorBean evaluateClassifier(DataModel dataModel, ItemSimilarity itemSimilarity2) {
		final DataModel currentDataModel = dataModel;
		final ItemSimilarity currentItemSimilarity = itemSimilarity;

		return evaluateSuggestor(currentDataModel,currentItemSimilarity);
	}
	
	
	public static void main(String[] args) {
		

	Suggestor predictor = new Suggestor();
	predictor.runDefaultSuggestor("F:\\Mayur\\workspace\\facebook-recommender-demo-master\\src\\main\\resources\\DemoFriendsLikes2.csv");
	SuggestOutputBean outputBean = predictor.getSuggestions();
		
	for(SuggestDataBean inputBean:outputBean.getDataBeans()){
		System.out.println(inputBean.getUserName() + "-->"+inputBean.getSuggestedItem());
	}
}
}
	

package com.ss.db.dao.mysql;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.sql.PreparedStatement;
import java.util.List;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import com.ss.db.dao.SentimentAnalysisDAO;
import com.ss.ml.opinion.miner.stanford.EntityDetails;
import com.ss.ml.opinion.miner.stanford.SWOTIndentifierService;
import com.ss.ml.opinion.miner.stanford.SWOTIndentifierService.SWOT_TYPE;
import com.ss.ml.opinion.miner.stanford.SentimentAnalysisResponse;
import com.ss.ml.opinion.miner.stanford.SentimentDetails;
import com.ss.ml.opinion.miner.stanford.SentimentTableBean;


public class MySqlSentimentAnalysisDAO implements SentimentAnalysisDAO{
	
	public static enum SENTIMENT_TYPE {
        
		 	POSITIVE(1), NEUTRAL(2), NEGATIVE(3), OVERALL(4);
	        private int setimentType;

	        private SENTIMENT_TYPE(int value) {
	        	setimentType = value;
	        }
	        
	        public int getSentimentType(){
	        	return setimentType;
	        }
	};   

	public static String getMaxGroupIdSQL = "SELECT MAX(sentenceGroupId) FROM tblSentimentAnalysisSentenceDetails";
	public static String tblSentimentAnalysisSentenceDetailsInsertSQL = "INSERT INTO tblSentimentAnalysisSentenceDetails (sentenceSourceId, sentenceRawText, sentenceOverallPolarity, sentenceInsertDateTime, sentenceGroupId, shortText) VALUES (?, ?, ?, ?, ?,?)";
	public static String tblSentimentAnalysisSentenceGroupMappingInsertSQL = "INSERT INTO tblSentimentAnalysisSentenceGroupMapping (groupId, sentenceId) VALUES (?, ?)";
	
	public static String tblSentimentAnalysisFeatureDetailInsertSQL = "INSERT INTO tblSentimentAnalysisFeatureDetails (featureText, featureSentenceId, featureSentencePolarity, featureInsertDateTime) VALUES (?, ?, ?, ?)";
	public static String tblSentimentAnalysisOpinionDetailsInsertSQL = "INSERT INTO tblSentimentAnalysisOpinionDetails (opinionText, opinionFeatureId, opinionInsertDateTime) VALUES (?, ?, ?)";
	public static String tblSentimentAnalysisExtractedEntityDetailsInsertSQL = "INSERT INTO tblSentimentAnalysisExtractedEntityDetails (entityText, entityType, entitySentenceId, entiryInsertDateTime) VALUES (?, ?, ?, ?)";
	public static String tblSentimentAnalysisStrengthIdentifierInsertSQL = "INSERT INTO tblSentimentAnalysisStrengthIdentifier (strengthInsertDateTime, strengthType, strengthSentenceId, strengthFeatureText, strengthSourceId) VALUES (?, ?, ?, ?, ?)";
	public static String tblSentimentAnalysisWeeknessIdentifierInsertSQL = "INSERT INTO tblSentimentAnalysisWeeknessIdentifier (weeknessInsertDateTime, weeknessType, weeknessSentenceId, weeknessFeatureText, weeknessSourceId) VALUES (?, ?, ?, ?, ?)";
	public static String tblSentimentAnalysisOpportunityIdentifierInsertSQL = "INSERT INTO tblSentimentAnalysisOpportunityIdentifier (opportunityInsertDateTime, opportunityType, opportunitySentenceId, opportunityFeatureText, opportunitySourceId) VALUES (?, ?, ?, ?, ?)";
	public static String tblSentimentAnalysisThreatIdentifierInsertSQL = "INSERT INTO tblSentimentAnalysisThreatIdentifier (threatInsertDateTime, threatType, threatSentenceId, threatFeatureText, threatSourceId) VALUES (?, ?, ?, ?, ?)";
	
	public static String sentimemtTableSQL = "SELECT sentenceId, dataSourceName, shortText, sentenceOverallPolarity FROM tblSentimentAnalysisSentenceDetails JOIN tblSentimentAnalysisDataSource ON tblSentimentAnalysisSentenceDetails.sentenceSourceId = tblSentimentAnalysisDataSource.dataSourceId ORDER BY sentenceInsertDateTime DESC LIMIT ?";
	//public static String entityTOPSQL = "SELECT DISTINCT entityText, entityType FROM tblSentimentAnalysisExtractedEntityDetails ORDER BY entiryInsertDateTime DESC LIMIT ?";
	//public static String featureTOPSQL = "SELECT featureText, COUNT(featureText) FROM tblSentimentAnalysisFeatureDetails GROUP BY featureText ORDER BY COUNT(featureText) DESC LIMIT ?";
	//public static String opinionTOPSQL = "SELECT opinionText, COUNT(opinionText) FROM tblSentimentAnalysisOpinionDetails GROUP BY opinionText ORDER BY COUNT(opinionText) DESC LIMIT ?";
	
	public static String getCompleteSentenceSQL = "SELECT sentenceRawText FROM tblSentimentAnalysisSentenceDetails WHERE sentenceId=?";
	
	public static String getOverallSentimentSQL = "SELECT sentenceOverallPolarity, COUNT(sentenceOverallPolarity) FROM tblSentimentAnalysisSentenceDetails GROUP BY sentenceOverallPolarity";
	//public static String getSentenceSourceSQL = "SELECT dataSourceName, COUNT(dataSourceName) FROM tblSentimentAnalysisSentenceDetails JOIN  tblSentimentAnalysisDataSource ON tblSentimentAnalysisSentenceDetails.sentenceSourceId = tblSentimentAnalysisDataSource.dataSourceId GROUP BY dataSourceName ORDER BY COUNT(dataSourceName) DESC ";
	
	//Feature Explorer SQLs
	public static String featureTOPByPositiveSentimentSQL = "SELECT featureText, COUNT(featureText) FROM tblSentimentAnalysisFeatureDetails JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisFeatureDetails.featureSentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Positive\", \"Very Positive\") GROUP BY featureText ORDER BY COUNT(featureText) DESC LIMIT ?";
	public static String featureTOPByNeutralSentimentSQL = "SELECT featureText, COUNT(featureText) FROM tblSentimentAnalysisFeatureDetails JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisFeatureDetails.featureSentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Neutral\") GROUP BY featureText ORDER BY COUNT(featureText) DESC LIMIT ?";
	public static String featureTOPByNegativeSentimentSQL = "SELECT featureText, COUNT(featureText) FROM tblSentimentAnalysisFeatureDetails JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisFeatureDetails.featureSentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Negative\", \"Very Negative\") GROUP BY featureText ORDER BY COUNT(featureText) DESC LIMIT ?";
	public static String featureTOPByOverallSentimentSQL = "SELECT featureText, COUNT(featureText) FROM tblSentimentAnalysisFeatureDetails JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisFeatureDetails.featureSentenceId = tblSentimentAnalysisSentenceDetails.sentenceId GROUP BY featureText ORDER BY COUNT(featureText) DESC LIMIT ?";
	
	//Opinion Explorer SQLs
	public static String opinionTOPByPositiveSentimentSQL = "SELECT opinionText, COUNT(opinionText) FROM tblSentimentAnalysisOpinionDetails JOIN tblSentimentAnalysisFeatureDetails ON  tblSentimentAnalysisOpinionDetails.opinionFeatureId = tblSentimentAnalysisFeatureDetails.featureId JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisFeatureDetails.featureSentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Positive\", \"Very Positive\") GROUP BY opinionText ORDER BY COUNT(featureText) DESC LIMIT ?";
	public static String opinionTOPByNeutralSentimentSQL = "SELECT opinionText, COUNT(opinionText) FROM tblSentimentAnalysisOpinionDetails JOIN tblSentimentAnalysisFeatureDetails ON  tblSentimentAnalysisOpinionDetails.opinionFeatureId = tblSentimentAnalysisFeatureDetails.featureId JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisFeatureDetails.featureSentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Neutral\") GROUP BY opinionText ORDER BY COUNT(featureText) DESC LIMIT ?";
	public static String opinionTOPByNegativeSentimentSQL = "SELECT opinionText, COUNT(opinionText) FROM tblSentimentAnalysisOpinionDetails JOIN tblSentimentAnalysisFeatureDetails ON  tblSentimentAnalysisOpinionDetails.opinionFeatureId = tblSentimentAnalysisFeatureDetails.featureId JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisFeatureDetails.featureSentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Negative\", \"Very Negative\") GROUP BY opinionText ORDER BY COUNT(featureText) DESC LIMIT ?";
	public static String opinionTOPByOverallSentimentSQL = "SELECT opinionText, COUNT(opinionText) FROM tblSentimentAnalysisOpinionDetails JOIN tblSentimentAnalysisFeatureDetails ON  tblSentimentAnalysisOpinionDetails.opinionFeatureId = tblSentimentAnalysisFeatureDetails.featureId JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisFeatureDetails.featureSentenceId = tblSentimentAnalysisSentenceDetails.sentenceId GROUP BY opinionText ORDER BY COUNT(featureText) DESC LIMIT ?";
	
	//Source Explorer SQLs
	public static String sourceTOPByPositiveSentimentSQL = "SELECT dataSourceName, COUNT(dataSourceName) FROM tblSentimentAnalysisSentenceDetails JOIN  tblSentimentAnalysisDataSource ON tblSentimentAnalysisSentenceDetails.sentenceSourceId = tblSentimentAnalysisDataSource.dataSourceId WHERE sentenceOverallPolarity IN (\"Positive\", \"Very Positive\") GROUP BY dataSourceName ORDER BY COUNT(dataSourceName) DESC LIMIT ?";
	public static String sourceTOPByNeutralSentimentSQL = "SELECT dataSourceName, COUNT(dataSourceName) FROM tblSentimentAnalysisSentenceDetails JOIN  tblSentimentAnalysisDataSource ON tblSentimentAnalysisSentenceDetails.sentenceSourceId = tblSentimentAnalysisDataSource.dataSourceId WHERE sentenceOverallPolarity IN (\"Neutral\") GROUP BY dataSourceName ORDER BY COUNT(dataSourceName) DESC LIMIT ?";
	public static String sourceTOPByNegativeSentimentSQL = "SELECT dataSourceName, COUNT(dataSourceName) FROM tblSentimentAnalysisSentenceDetails JOIN  tblSentimentAnalysisDataSource ON tblSentimentAnalysisSentenceDetails.sentenceSourceId = tblSentimentAnalysisDataSource.dataSourceId WHERE sentenceOverallPolarity IN (\"Negative\", \"Very Negative\") GROUP BY dataSourceName ORDER BY COUNT(dataSourceName) DESC LIMIT ?";
	public static String sourceTOPByOverallSentimentSQL = "SELECT dataSourceName, COUNT(dataSourceName) FROM tblSentimentAnalysisSentenceDetails JOIN  tblSentimentAnalysisDataSource ON tblSentimentAnalysisSentenceDetails.sentenceSourceId = tblSentimentAnalysisDataSource.dataSourceId GROUP BY dataSourceName ORDER BY COUNT(dataSourceName) DESC LIMIT ?";
	
	//SWOT Identifier SQLs
	public static String strengthTOPSQL = "SELECT strengthFeatureText, COUNT(strengthFeatureText) FROM tblSentimentAnalysisStrengthIdentifier GROUP BY strengthFeatureText ORDER BY COUNT(strengthFeatureText) DESC LIMIT ?";
	public static String weeknessTOPSQL = "SELECT weeknessFeatureText, COUNT(weeknessFeatureText) FROM tblSentimentAnalysisWeeknessIdentifier GROUP BY weeknessFeatureText ORDER BY COUNT(weeknessFeatureText) DESC LIMIT ?";
	public static String opportunityTOPSQL = "SELECT opportunityFeatureText, COUNT(opportunityFeatureText) FROM tblSentimentAnalysisOpportunityIdentifier GROUP BY opportunityFeatureText ORDER BY COUNT(opportunityFeatureText) DESC LIMIT ?";
	public static String threatTOPSQL = "SELECT threatFeatureText, COUNT(threatFeatureText) FROM tblSentimentAnalysisThreatIdentifier GROUP BY threatFeatureText ORDER BY COUNT(threatFeatureText) DESC LIMIT ?";
	
	//Entity Explorer SQLs
	public static String entityTOPByPositiveSentimentSQL = "SELECT CONCAT(entityText, ' (', entityType, ')') AS entity_text, COUNT(CONCAT(entityText, ' (', entityType, ')')) AS entity_count FROM tblSentimentAnalysisExtractedEntityDetails JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisExtractedEntityDetails.entitySentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Positive\", \"Very Positive\") GROUP BY entity_text ORDER BY COUNT(entity_text) DESC LIMIT ?";
	public static String entityTOPByNeutralSentimentSQL = "SELECT CONCAT(entityText, ' (', entityType, ')') AS entity_text, COUNT(CONCAT(entityText, ' (', entityType, ')')) AS entity_count FROM tblSentimentAnalysisExtractedEntityDetails JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisExtractedEntityDetails.entitySentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Neutral\") GROUP BY entity_text ORDER BY COUNT(entity_text) DESC LIMIT ?";
	public static String entityTOPByNegativeSentimentSQL = "SELECT CONCAT(entityText, ' (', entityType, ')') AS entity_text, COUNT(CONCAT(entityText, ' (', entityType, ')')) AS entity_count FROM tblSentimentAnalysisExtractedEntityDetails JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisExtractedEntityDetails.entitySentenceId = tblSentimentAnalysisSentenceDetails.sentenceId WHERE sentenceOverallPolarity IN (\"Negative\", \"Very Negative\") GROUP BY entity_text ORDER BY COUNT(entity_text) DESC LIMIT ?";
	public static String entityTOPByOverallSentimentSQL = "SELECT CONCAT(entityText, ' (', entityType, ')') AS entity_text, COUNT(CONCAT(entityText, ' (', entityType, ')')) AS entity_count FROM tblSentimentAnalysisExtractedEntityDetails JOIN tblSentimentAnalysisSentenceDetails ON tblSentimentAnalysisExtractedEntityDetails.entitySentenceId = tblSentimentAnalysisSentenceDetails.sentenceId GROUP BY entity_text ORDER BY COUNT(entity_text) DESC LIMIT ?";

	public static final SimpleDateFormat insertTimeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String[] sentimentScores = { "Very Negative","Negative", "Neutral", "Positive", "Very Positive"};
	
	@Override
	public void insertSentenceDetails(int dataSourceId, List<SentimentAnalysisResponse> responses) {
		
		PreparedStatement ps1 = null, ps2 = null, ps3 = null, ps4 = null, ps5 = null, ps6 = null, ps7 = null, ps8 = null, ps9 = null, ps10 = null;
		
		
		ResultSet resultSet = null;
		
		Connection dbConnect = null;
		
		int maxGroupId = 0;
		Date currentDate = null;
		String currentTime = null;
		int sentenceId = 0;
		
		String feature = null;
		int sentimentScoreInt = 0;
		int featureId = 0;
		
		Map<String, List<String>> swotIdentifiers = null;
		
		try 
		{
			currentDate = new Date();
			currentTime = insertTimeStampFormat.format(currentDate);
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			
			dbConnect.setAutoCommit(false);
			
			ps1 = dbConnect.prepareStatement(getMaxGroupIdSQL);
			ps2 = dbConnect.prepareStatement(tblSentimentAnalysisSentenceDetailsInsertSQL,Statement.RETURN_GENERATED_KEYS);
			ps3 = dbConnect.prepareStatement(tblSentimentAnalysisSentenceGroupMappingInsertSQL);
			ps4 = dbConnect.prepareStatement(tblSentimentAnalysisExtractedEntityDetailsInsertSQL);
			ps5 = dbConnect.prepareStatement(tblSentimentAnalysisFeatureDetailInsertSQL,Statement.RETURN_GENERATED_KEYS);
			ps6 = dbConnect.prepareStatement(tblSentimentAnalysisOpinionDetailsInsertSQL);
			ps7 = dbConnect.prepareStatement(tblSentimentAnalysisStrengthIdentifierInsertSQL);
			ps8 = dbConnect.prepareStatement(tblSentimentAnalysisWeeknessIdentifierInsertSQL);
			ps9 = dbConnect.prepareStatement(tblSentimentAnalysisOpportunityIdentifierInsertSQL);
			ps10 = dbConnect.prepareStatement(tblSentimentAnalysisThreatIdentifierInsertSQL);
			
			resultSet = ps1.executeQuery();
			
			if(resultSet.next()){
				maxGroupId = resultSet.getInt(1);
			}
			
			if(responses !=null && responses.size()>0 ){
				
				for (SentimentAnalysisResponse response: responses) {
									
					ps2.setInt(1, dataSourceId);
					ps2.setString(2, response.getRawTex());
					ps2.setString(3, response.getAggregate().getSentiment()); 
					ps2.setString(4, currentTime);
					ps2.setInt(5, maxGroupId+1);
					ps2.setString(6, response.getRawTex().length()> 200 ? response.getRawTex().substring(0, 200):response.getRawTex());
					
					ps2.executeUpdate();
					resultSet = ps2.getGeneratedKeys();
					
					if(resultSet.next()){
						sentenceId = resultSet.getInt(1);	
					}
					
					
					ps3.setInt(1, maxGroupId+1);
					ps3.setInt(2, sentenceId);
					
					ps3.executeUpdate();
					
					if(response.getEntities() !=null && response.getEntities().size()>0){
						for (EntityDetails entity: response.getEntities()){
							
							ps4.setString(1, entity.getEntityName());
							ps4.setString(2, entity.getEntityType());
							ps4.setInt(3, sentenceId);
							ps4.setString(4, currentTime);
							
							ps4.addBatch();
						}
						
						ps4.executeBatch();
					}
					
					if(response.getNegative() !=null && response.getNegative().size() >0){
						for(SentimentDetails detail: response.getNegative()){
							feature = detail.getFeature();
							
							sentimentScoreInt = (int)detail.getScore().doubleValue();
							
							ps5.setString(1, feature);
							ps5.setInt(2, sentenceId);
							ps5.setString(3, sentimentScores[sentimentScoreInt]);
							ps5.setString(4, currentTime);
							
							ps5.executeUpdate();
							resultSet = ps5.getGeneratedKeys();
							
							if(resultSet.next()){
								featureId = resultSet.getInt(1);	
							}
							
							if(detail.getSentiments() != null && detail.getSentiments().size()>0){
								for(String opinion: detail.getSentiments()){
									
									ps6.setString(1, opinion);
									ps6.setInt(2, featureId);
									ps6.setString(3, currentTime);
									
									ps6.addBatch();
								}
								
								ps6.executeBatch();
							}
							
						}
					}
					
					if(response.getNeutral() !=null && response.getNeutral().size()>0){
						for(SentimentDetails detail: response.getNeutral()){
							feature = detail.getFeature();
							
							sentimentScoreInt = (int)detail.getScore().doubleValue();
							
							ps5.setString(1, feature);
							ps5.setInt(2, sentenceId);
							ps5.setString(3, sentimentScores[sentimentScoreInt]);
							ps5.setString(4, currentTime);
							
							ps5.executeUpdate();
							resultSet = ps5.getGeneratedKeys();
							
							if(resultSet.next()){
								featureId = resultSet.getInt(1);	
							}
							
							if(detail.getSentiments() != null && detail.getSentiments().size()>0){
								for(String opinion: detail.getSentiments()){
									
									ps6.setString(1, opinion);
									ps6.setInt(2, featureId);
									ps6.setString(3, currentTime);
									
									ps6.addBatch();
								}
								
								ps6.executeBatch();
							}
							
						}
					}
					
					
					if(response.getPositive() != null && response.getPositive().size() >0){
						for(SentimentDetails detail: response.getPositive()){
							feature = detail.getFeature();
							
							sentimentScoreInt = (int)detail.getScore().doubleValue();
							
							ps5.setString(1, feature);
							ps5.setInt(2, sentenceId);
							ps5.setString(3, sentimentScores[sentimentScoreInt]);
							ps5.setString(4, currentTime);
							
							ps5.executeUpdate();
							resultSet = ps5.getGeneratedKeys();
							
							if(resultSet.next()){
								featureId = resultSet.getInt(1);	
							}
							
							if(detail.getSentiments() != null && detail.getSentiments().size()>0){
								for(String opinion: detail.getSentiments()){
									
									ps6.setString(1, opinion);
									ps6.setInt(2, featureId);
									ps6.setString(3, currentTime);
									
									ps6.addBatch();
								}
								
								ps6.executeBatch();
							}
							
						}
					}
				    
					swotIdentifiers = SWOTIndentifierService.getOpinionBySWOTType(responses, SWOT_TYPE.STRENGTH.getType());
					
					if(swotIdentifiers != null && swotIdentifiers.size()>0){
						
						for(Map.Entry<String, List<String>> entry: swotIdentifiers.entrySet()){
							
							for(String word: entry.getValue()){
								ps7.setString(1, currentTime);
								ps7.setString(2, word);
								ps7.setInt(3, sentenceId);
								ps7.setString(4, entry.getKey());
								ps7.setInt(5, dataSourceId);
								
								ps7.addBatch();
							}
						}
						
						ps7.executeBatch();
					}
					
					
					swotIdentifiers = SWOTIndentifierService.getOpinionBySWOTType(responses, SWOT_TYPE.WEEKNESS.getType());
					
					if(swotIdentifiers != null && swotIdentifiers.size()>0){
						
						for(Map.Entry<String, List<String>> entry: swotIdentifiers.entrySet()){
							
							for(String word: entry.getValue()){
								ps8.setString(1, currentTime);
								ps8.setString(2, word);
								ps8.setInt(3, sentenceId);
								ps8.setString(4, entry.getKey());
								ps8.setInt(5, dataSourceId);
								
								ps8.addBatch();
							}
						}
						
						ps8.executeBatch();
					}
					
					swotIdentifiers = SWOTIndentifierService.getOpinionBySWOTType(responses, SWOT_TYPE.OPPORTUNITY.getType());
					
					if(swotIdentifiers != null && swotIdentifiers.size()>0){
						
						for(Map.Entry<String, List<String>> entry: swotIdentifiers.entrySet()){
							
							for(String word: entry.getValue()){
								ps9.setString(1, currentTime);
								ps9.setString(2, word);
								ps9.setInt(3, sentenceId);
								ps9.setString(4, entry.getKey());
								ps9.setInt(5, dataSourceId);
								
								ps9.addBatch();
							}
						}
						
						ps9.executeBatch();
					}
					
					swotIdentifiers = SWOTIndentifierService.getOpinionBySWOTType(responses, SWOT_TYPE.THREAT.getType());
					
					if(swotIdentifiers != null && swotIdentifiers.size()>0){
						
						for(Map.Entry<String, List<String>> entry: swotIdentifiers.entrySet()){
							
							for(String word: entry.getValue()){
								ps10.setString(1, currentTime);
								ps10.setString(2, word);
								ps10.setInt(3, sentenceId);
								ps10.setString(4, entry.getKey());
								ps10.setInt(5, dataSourceId);
								
								ps10.addBatch();
							}
						}
						
						ps10.executeBatch();
					}
					
					dbConnect.commit();
				 }
			}
	
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(ps1 != null){
				try{
					ps1.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(ps2 != null){
				try{
					ps2.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(ps3 != null){
				try{
					ps3.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(ps4 != null){
				try{
					ps4.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(ps5 != null){
				try{
					ps5.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(ps6 != null){
				try{
					ps6.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			
			if(ps7 != null){
				try{
					ps7.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(ps8 != null){
				try{
					ps8.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(ps9 != null){
				try{
					ps9.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(ps10 != null){
				try{
					ps10.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
		
	
		}
		
	}
	

	@Override
	public List<SentimentTableBean> getSentimentByCount(int count) {

		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		List<SentimentTableBean> beans = null;
		SentimentTableBean bean = null;
		
		try 
		{
			beans = new LinkedList<SentimentTableBean>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			statement = dbConnect.prepareStatement(sentimemtTableSQL);
			statement.setInt(1, count);
			
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				bean = new SentimentTableBean();
				
				bean.setSentenceId(resultSet.getInt(1));
				bean.setDataSourceName(resultSet.getString(2));
				bean.setShortText(resultSet.getString(3));
				bean.setOverallSentiment(resultSet.getString(4));
				
				beans.add(bean);
			}
				
				
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
			
		
		return beans;
	}


//	@Override
//	public List<EntityDetails> getEntitiesByCount(int count) {
//		
//		ResultSet resultSet = null;
//		PreparedStatement statement = null;
//		Connection dbConnect = null;
//		
//		List<EntityDetails> beans = null;
//		EntityDetails bean = null;
//		
//		try 
//		{
//			beans = new LinkedList<EntityDetails>();
//			
//			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
//			statement = dbConnect.prepareStatement(entityTOPSQL);
//			statement.setInt(1, count);
//			
//			resultSet = statement.executeQuery();
//			
//			while(resultSet.next()){
//				bean = new EntityDetails();
//				
//				bean.setEntityName(resultSet.getString(1));
//				bean.setEntityType(resultSet.getString(2));
//				
//				beans.add(bean);
//			}
//				
//				
//			
//		}catch (Exception ex){
//			ex.printStackTrace();
//		
//		}finally{
//			
//			if(resultSet != null){
//				try{
//					resultSet.close();
//				}catch (Exception ex){
//					ex.printStackTrace();
//				}
//			}
//				
//
//			if(statement != null){
//				try{
//					statement.close();
//				}catch (Exception ex){
//					ex.printStackTrace();
//				}
//			}
//			
//			if(dbConnect != null){
//				try{
//					dbConnect.close();
//				}catch (Exception ex){
//					ex.printStackTrace();
//				}
//			}
//	
//		}
//		return beans;
//	}


	@Override
	public Map<String, Integer> getTopKFeatureBySentimentType(int count, int sentimentType) {
		
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		Map<String, Integer> beans = null;
	
		try 
		{
			beans = new LinkedHashMap<String, Integer>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			
			//Select Feature SQL
			if(sentimentType == SENTIMENT_TYPE.OVERALL.getSentimentType())
				statement = dbConnect.prepareStatement(featureTOPByOverallSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.POSITIVE.getSentimentType())
				statement = dbConnect.prepareStatement(featureTOPByPositiveSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.NEUTRAL.getSentimentType())
				statement = dbConnect.prepareStatement(featureTOPByNeutralSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.NEGATIVE.getSentimentType())
				statement = dbConnect.prepareStatement(featureTOPByNegativeSentimentSQL);
				
			statement.setInt(1, count);
			
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				beans.put(resultSet.getString(1), resultSet.getInt(2));
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
		
		return beans;
	}


	@Override
	public Map<String, Integer> getTopKOpinionBySentimentType(int count, int sentimentType) {
		
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		Map<String, Integer> beans = null;
	
		try 
		{
			beans = new LinkedHashMap<String, Integer>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			
			//Select Opinion SQL
			if(sentimentType == SENTIMENT_TYPE.OVERALL.getSentimentType())
				statement = dbConnect.prepareStatement(opinionTOPByOverallSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.POSITIVE.getSentimentType())
				statement = dbConnect.prepareStatement(opinionTOPByPositiveSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.NEUTRAL.getSentimentType())
				statement = dbConnect.prepareStatement(opinionTOPByNeutralSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.NEGATIVE.getSentimentType())
				statement = dbConnect.prepareStatement(opinionTOPByNegativeSentimentSQL);
			
			statement.setInt(1, count);
			
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				beans.put(resultSet.getString(1), resultSet.getInt(2));
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
		
		return beans;
	}

	@Override
	public Map<String, Integer> getTopKSWOTFeatureBySWOTType(int count, int swotType) {
		
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		Map<String, Integer> beans = null;
	
		try 
		{
			beans = new LinkedHashMap<String, Integer>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			
			if(swotType == SWOT_TYPE.STRENGTH.getType())
				statement = dbConnect.prepareStatement(strengthTOPSQL);
			else if(swotType == SWOT_TYPE.WEEKNESS.getType())
				statement = dbConnect.prepareStatement(weeknessTOPSQL);
			else if(swotType == SWOT_TYPE.OPPORTUNITY.getType())
				statement = dbConnect.prepareStatement(opportunityTOPSQL);
			else if(swotType == SWOT_TYPE.THREAT.getType())
				statement = dbConnect.prepareStatement(threatTOPSQL);
		
			statement.setInt(1, count);
			
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				beans.put(resultSet.getString(1), resultSet.getInt(2));
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
		
		return beans;
	}


	@Override
	public String getCompleteSentenceById(int sentenceId) {
		
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		String rawText = "";
	
		try 
		{
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			statement = dbConnect.prepareStatement(getCompleteSentenceSQL);
			statement.setInt(1, sentenceId);
			
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				rawText = resultSet.getString(1);
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
		
		return rawText;
	}


	@Override
	public Map<String, Integer> getOverallSentiment() {
		
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		Map<String, Integer> beans = null;
	
		try 
		{
			beans = new LinkedHashMap<String, Integer>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			statement = dbConnect.prepareStatement(getOverallSentimentSQL);
		
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				beans.put(resultSet.getString(1), resultSet.getInt(2));
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
		
		return beans;
	}


	@Override
	public Map<String, Integer> getTopKSourceBySentimentType(int count, int sentimentType) {

		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		Map<String, Integer> beans = null;
	
		try 
		{
			beans = new LinkedHashMap<String, Integer>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			
			//Select Source SQL
			if(sentimentType == SENTIMENT_TYPE.OVERALL.getSentimentType())
				statement = dbConnect.prepareStatement(sourceTOPByOverallSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.POSITIVE.getSentimentType())
				statement = dbConnect.prepareStatement(sourceTOPByPositiveSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.NEUTRAL.getSentimentType())
				statement = dbConnect.prepareStatement(sourceTOPByNeutralSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.NEGATIVE.getSentimentType())
				statement = dbConnect.prepareStatement(sourceTOPByNegativeSentimentSQL);
			
			statement.setInt(1, count);
			
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				beans.put(resultSet.getString(1), resultSet.getInt(2));
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
		
		return beans;
	}

	
	@Override
	public Map<String, Integer> getTopKEntityBySentimentType(int count, int sentimentType) {
		
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		Map<String, Integer> beans = null;
	
		try 
		{
			beans = new LinkedHashMap<String, Integer>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			
			//Select Source SQL
			if(sentimentType == SENTIMENT_TYPE.OVERALL.getSentimentType())
				statement = dbConnect.prepareStatement(entityTOPByOverallSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.POSITIVE.getSentimentType())
				statement = dbConnect.prepareStatement(entityTOPByPositiveSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.NEUTRAL.getSentimentType())
				statement = dbConnect.prepareStatement(entityTOPByNeutralSentimentSQL);
			else if(sentimentType == SENTIMENT_TYPE.NEGATIVE.getSentimentType())
				statement = dbConnect.prepareStatement(entityTOPByNegativeSentimentSQL);
			
			statement.setInt(1, count);
			
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				beans.put(resultSet.getString(1), resultSet.getInt(2));
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
		
		return beans;
	}
	
}

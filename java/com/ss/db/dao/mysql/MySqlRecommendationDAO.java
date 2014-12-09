package com.ss.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import com.ss.db.dao.RecommendationDAO;
import com.ss.ml.suggest.RecommendationTableBean;

public class MySqlRecommendationDAO implements  RecommendationDAO{

	public static String strengthTOPSQL = "SELECT dataSourceName, strengthFeatureText, COUNT(strengthFeatureText) FROM tblSentimentAnalysisStrengthIdentifier JOIN tblSentimentAnalysisDataSource ON tblSentimentAnalysisStrengthIdentifier.strengthSourceId  = tblSentimentAnalysisDataSource.dataSourceId GROUP BY dataSourceName, strengthFeatureText ORDER BY COUNT(strengthFeatureText) DESC LIMIT ?";
	public static String weeknessTOPSQL = "SELECT dataSourceName, weeknessFeatureText, COUNT(weeknessFeatureText) FROM tblSentimentAnalysisWeeknessIdentifier JOIN tblSentimentAnalysisDataSource ON tblSentimentAnalysisWeeknessIdentifier.weeknessSourceId  = tblSentimentAnalysisDataSource.dataSourceId GROUP BY dataSourceName, weeknessFeatureText ORDER BY COUNT(weeknessFeatureText) DESC LIMIT ?";
	public static String opportunityTOPSQL = "SELECT dataSourceName, opportunityFeatureText, COUNT(opportunityFeatureText) FROM tblSentimentAnalysisOpportunityIdentifier JOIN tblSentimentAnalysisDataSource ON tblSentimentAnalysisOpportunityIdentifier.opportunitySourceId  = tblSentimentAnalysisDataSource.dataSourceId GROUP BY dataSourceName, opportunityFeatureText ORDER BY COUNT(opportunityFeatureText) DESC LIMIT ?";
	public static String threatTOPSQL = "SELECT dataSourceName, threatFeatureText, COUNT(threatFeatureText) FROM tblSentimentAnalysisThreatIdentifier JOIN tblSentimentAnalysisDataSource ON tblSentimentAnalysisThreatIdentifier.threatSourceId  = tblSentimentAnalysisDataSource.dataSourceId GROUP BY dataSourceName, threatFeatureText ORDER BY COUNT(threatFeatureText) DESC LIMIT ?";
	
	
	@Override
	public List<RecommendationTableBean> getRecommendationInputByCount(int count) {
		
		ResultSet rs1 = null, rs2 = null, rs3 = null, rs4 = null;
		PreparedStatement ps1 = null, ps2 = null, ps3 = null, ps4 = null;
		Connection dbConnect = null;
		
		List<RecommendationTableBean> beans = null;
		RecommendationTableBean bean = null;
		
		try 
		{
			beans = new LinkedList<RecommendationTableBean>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			
			ps1 = dbConnect.prepareStatement(strengthTOPSQL);
			ps1.setInt(1, count);
			
			rs1 = ps1.executeQuery();
			
			while(rs1.next()){
				bean = new RecommendationTableBean();
				
				bean.setDataSource(rs1.getString(1));
				bean.setSwotText(rs1.getString(2));
				bean.setSwotCount(rs1.getInt(3));
				
				beans.add(bean);
			}
			
			
			ps2 = dbConnect.prepareStatement(weeknessTOPSQL);
			ps2.setInt(1, count);
			
			rs2 = ps2.executeQuery();
			
			while(rs2.next()){
				bean = new RecommendationTableBean();
				
				bean.setDataSource(rs2.getString(1));
				bean.setSwotText(rs2.getString(2));
				bean.setSwotCount(rs2.getInt(3));
				
				beans.add(bean);
			}
			
			
			ps3 = dbConnect.prepareStatement(opportunityTOPSQL);
			ps3.setInt(1, count);
			
			rs3 = ps3.executeQuery();
			
			while(rs3.next()){
				bean = new RecommendationTableBean();
				
				bean.setDataSource(rs3.getString(1));
				bean.setSwotText(rs3.getString(2));
				bean.setSwotCount(rs3.getInt(3));
				
				beans.add(bean);
			}
			
			
			ps4 = dbConnect.prepareStatement(threatTOPSQL);
			ps4.setInt(1, count);
			
			rs4 = ps4.executeQuery();
			
			while(rs4.next()){
				bean = new RecommendationTableBean();
				
				bean.setDataSource(rs4.getString(1));
				bean.setSwotText(rs4.getString(2));
				bean.setSwotCount(rs4.getInt(3));
				
				beans.add(bean);
			}
			
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(rs1 != null){
				try{
					rs1.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(rs2 != null){
				try{
					rs2.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(rs3 != null){
				try{
					rs3.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(rs4 != null){
				try{
					rs4.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

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

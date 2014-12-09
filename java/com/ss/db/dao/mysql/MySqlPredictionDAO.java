package com.ss.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import com.ss.db.dao.PredictionDAO;
import com.ss.ml.forecast.PredictionInputBean;
import com.ss.ml.forecast.PredictionOutputBean;

public class MySqlPredictionDAO implements PredictionDAO{

	public static String tblChurnCustomerDetailsInsertSQL = "INSERT INTO tblChurnCustomerDetails (customerId, tariffplanId, paymenmethodType, gender, age, activarea, activchan, valueAddedService1, valueAddedService2, isCustomerActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static String tblChurnCustomerQuarteryUsageDetailsInsertSQL = "INSERT INTO tblChurnCustomerQuarteryUsageDetails (customerId, quarterId, outCallCountPeakTariff) VALUES (?, ?, ?)";
	public static String tblChurnPredictionInsertSQL = "INSERT INTO tblChurnPrediction (customerId, actualChurnStatus, predictedChurnStatus) VALUES (?, ?, ?)";
	
	public static final int batchSize = 100;
	
	
	@Override
	public void insertPredictionCustomerDetails(PredictionOutputBean predictionBean) {
	
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		
		Connection dbConnect = null;
		
		int count = 0;
		
		try 
		{
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			
			dbConnect.setAutoCommit(false);
			
			ps1 = dbConnect.prepareStatement(tblChurnCustomerDetailsInsertSQL);
			ps2 = dbConnect.prepareStatement(tblChurnCustomerQuarteryUsageDetailsInsertSQL);
			ps3 = dbConnect.prepareStatement(tblChurnPredictionInsertSQL);
			
			
			for (PredictionInputBean inputBean: predictionBean.getInputBeans()) {
								
				ps1.setInt(1, new Double(inputBean.getProperty1()).intValue());
				ps1.setInt(2, new Double(inputBean.getProperty2()).intValue());
				ps1.setString(3, inputBean.getProperty3()); 
				ps1.setString(4, inputBean.getProperty4());
				ps1.setInt(5, new Double(inputBean.getProperty5()).intValue());
				ps1.setInt(6, new Double(inputBean.getProperty6()).intValue());
				ps1.setInt(7, new Double(inputBean.getProperty7()).intValue());
				ps1.setString(8, inputBean.getProperty8()); 
				ps1.setString(9, inputBean.getProperty9());
				ps1.setString(10, "Y");
				
				ps1.addBatch();
				
				ps2.setInt(1, new Double(inputBean.getProperty1()).intValue());
				ps2.setInt(2, 1);
				ps2.setInt(3, new Double(inputBean.getProperty10()).intValue());
			
				ps2.addBatch();
				
				ps3.setInt(1, new Double(inputBean.getProperty1()).intValue());
				ps3.setString(2, inputBean.getActualValue().equalsIgnoreCase("churner") ? "Y":"N"); 
				ps3.setString(3, inputBean.getPredictedValue().equalsIgnoreCase("churner") ? "Y":"N");
				
				ps3.addBatch();
				
				if(++count % batchSize == 0) {
			        
					ps1.executeBatch();
			        ps2.executeBatch();
			        ps3.executeBatch();
			        
			        dbConnect.commit();
			    }
			}
			
			ps1.executeBatch();
	        ps2.executeBatch();
	        ps3.executeBatch();
	        
			dbConnect.commit();
			
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
	public List<PredictionInputBean> getPredictionCustomerDetails() {
		
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		List<PredictionInputBean> beans = null;
		PredictionInputBean bean = null;
		
		try 
		{
			beans = new LinkedList<PredictionInputBean>();
			
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			statement = dbConnect.prepareStatement("SELECT tblChurnCustomerDetails.customerId,tariffplanId,paymenmethodType,gender,age,activarea,activchan,valueAddedService1,valueAddedService2,customerServiceCallCount,actualChurnStatus,predictedChurnStatus "+
			                                       "FROM tblChurnCustomerDetails JOIN tblChurnCustomerQuarteryUsageDetails ON tblChurnCustomerDetails.customerId = tblChurnCustomerQuarteryUsageDetails.customerId "+
					                               "JOIN tblChurnPrediction ON  tblChurnPrediction.customerId = tblChurnCustomerDetails.customerId WHERE tblChurnCustomerQuarteryUsageDetails.quarterId =1");
			
			resultSet = statement.executeQuery();
			 
			while(resultSet.next()){
				bean = new PredictionInputBean();
				
				bean.setProperty1(Integer.toString(resultSet.getInt(1)));
				bean.setProperty2(Integer.toString(resultSet.getInt(2)));
				bean.setProperty3(resultSet.getString(3));
				bean.setProperty4(resultSet.getString(4));
				bean.setProperty5(Integer.toString(resultSet.getInt(5)));
				bean.setProperty6(Integer.toString(resultSet.getInt(6)));
				bean.setProperty7(Integer.toString(resultSet.getInt(7)));
				bean.setProperty8(resultSet.getString(8));
				bean.setProperty9(resultSet.getString(9));
				
				bean.setProperty10(Integer.toString(resultSet.getInt(10)));
		
				bean.setActualValue(resultSet.getString(11));
				bean.setPredictedValue(resultSet.getString(12));
				
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

}

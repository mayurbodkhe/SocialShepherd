package com.ss.db.dao;

import java.util.List;
import com.ss.ml.forecast.PredictionInputBean;
import com.ss.ml.forecast.PredictionOutputBean;

public interface PredictionDAO {
	
	public void insertPredictionCustomerDetails(PredictionOutputBean predictionBean);
	
	public List<PredictionInputBean> getPredictionCustomerDetails();


}

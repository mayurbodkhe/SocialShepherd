package com.ss.db.dao.mysql;

import java.sql.Connection;
import com.ss.db.dao.DAOFactory;
import com.ss.db.dao.PredictionDAO;
import com.ss.db.dao.RecommendationDAO;
import com.ss.db.dao.SentimentAnalysisDAO;
import com.ss.db.dao.UserDAO;

public class MySqlDAOFactory extends DAOFactory{

	private Connection dbConnect;

	public MySqlDAOFactory(){
		dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
	}
	
	@Override
	public PredictionDAO getPredictionDAO() {
		// TODO Auto-generated method stub
		return new MySqlPredictionDAO();
	}

	@Override
	public SentimentAnalysisDAO getSentimentAnalysisDAO() {
		// TODO Auto-generated method stub
		return new MySqlSentimentAnalysisDAO();
	}

	@Override
	public RecommendationDAO getRecommendationDAO() {
		// TODO Auto-generated method stub
		return new MySqlRecommendationDAO();
	}

	@Override
	public UserDAO getUserDAO() {
		// TODO Auto-generated method stub
		return new MySqlUserDAO();
	}

	public Connection getDbConnect() {
		return dbConnect;
	}

	public void setDbConnect(Connection dbConnect) {
		this.dbConnect = dbConnect;
	}
	
}

package com.ss.services;

import com.ss.db.dao.DAOFactory;
import com.ss.db.dao.PredictionDAO;
import com.ss.db.dao.RecommendationDAO;
import com.ss.db.dao.SentimentAnalysisDAO;
import com.ss.db.dao.UserDAO;

public class DBService {

	private static DBService dbService;
	private DAOFactory daoFactory;
	
	private PredictionDAO predictionDAO;
	private RecommendationDAO recommendationDAO;
	private SentimentAnalysisDAO sentimentAnalysisDAO;
	private UserDAO userDAO;
	
	 
	private DBService (){
		this.daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
		
		this.predictionDAO = daoFactory.getPredictionDAO();
		this.recommendationDAO = daoFactory.getRecommendationDAO();
		this.sentimentAnalysisDAO = daoFactory.getSentimentAnalysisDAO();
		this.userDAO = daoFactory.getUserDAO();
	}
	
	public static DBService getInstance(){
		
		if(dbService == null)
			dbService = new DBService();
		
		return dbService;
	}
	
	public DAOFactory getDaoFactory() {
		return daoFactory;
	}

	public void setDaoFactory(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public PredictionDAO getPredictionDAO() {
		return predictionDAO;
	}

	public void setPredictionDAO(PredictionDAO predictionDAO) {
		this.predictionDAO = predictionDAO;
	}

	public RecommendationDAO getRecommendationDAO() {
		return recommendationDAO;
	}

	public void setRecommendationDAO(RecommendationDAO recommendationDAO) {
		this.recommendationDAO = recommendationDAO;
	}

	public SentimentAnalysisDAO getSentimentAnalysisDAO() {
		return sentimentAnalysisDAO;
	}

	public void setSentimentAnalysisDAO(SentimentAnalysisDAO sentimentAnalysisDAO) {
		this.sentimentAnalysisDAO = sentimentAnalysisDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	
}

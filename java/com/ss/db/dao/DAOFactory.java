package com.ss.db.dao;

import com.ss.db.dao.mysql.MySqlDAOFactory;

public abstract class DAOFactory {

	  public static final int MYSQL = 1;

	  public abstract PredictionDAO getPredictionDAO();
	  public abstract SentimentAnalysisDAO getSentimentAnalysisDAO();
	  public abstract RecommendationDAO getRecommendationDAO();
	  public abstract UserDAO getUserDAO();

	  public static DAOFactory getDAOFactory(int whichFactory) {
	  
	    switch (whichFactory) {
	      case MYSQL: 
	          return new MySqlDAOFactory();
	     default	: 
	          return null;
	    }
	  }
	}

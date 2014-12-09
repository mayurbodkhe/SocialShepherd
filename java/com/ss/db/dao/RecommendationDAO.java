package com.ss.db.dao;

import java.util.List;
import com.ss.ml.suggest.RecommendationTableBean;

public interface RecommendationDAO {
	
	public List<RecommendationTableBean> getRecommendationInputByCount(int count);

}

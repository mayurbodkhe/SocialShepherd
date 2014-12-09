package com.ss.services;

import com.ss.rss.reader.RSSExtractor;
import com.ss.social.media.api.FacebookExtractor;
import com.ss.social.media.api.TwitterExtractor;

public class DataPullerService {
	
	private static DataPullerService dataPullerService;
	
	private FacebookExtractor facebookExtractor;
	private TwitterExtractor twitterExtractor;
	private RSSExtractor rssExtractor;
	
	private DataPullerService() {
		this.facebookExtractor = new FacebookExtractor();
		this.twitterExtractor = new TwitterExtractor();
		this.rssExtractor = new RSSExtractor();
	}

	public static DataPullerService getInstance(){
		if(dataPullerService == null)
			dataPullerService = new DataPullerService();
		
		return dataPullerService;
	}

	public static DataPullerService getDataPullerService() {
		return dataPullerService;
	}

	public static void setDataPullerService(DataPullerService dataPullerService) {
		DataPullerService.dataPullerService = dataPullerService;
	}

	public FacebookExtractor getFacebookExtractor() {
		return facebookExtractor;
	}

	public void setFacebookExtractor(FacebookExtractor facebookExtractor) {
		this.facebookExtractor = facebookExtractor;
	}

	public TwitterExtractor getTwitterExtractor() {
		return twitterExtractor;
	}

	public void setTwitterExtractor(TwitterExtractor twitterExtractor) {
		this.twitterExtractor = twitterExtractor;
	}

	public RSSExtractor getRssExtractor() {
		return rssExtractor;
	}

	public void setRssExtractor(RSSExtractor rssExtractor) {
		this.rssExtractor = rssExtractor;
	}
	
	
}

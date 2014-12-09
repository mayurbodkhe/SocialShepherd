package com.ss.rss.reader;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;


public class RSSExtractor {
	
	private static final Logger logger = Logger.getLogger(RSSExtractor.class);
	
	static final String[] RSS_FEED_URL = {""};
	
	public List<FeedMessage> getPostByCommentCount(String searchText, int articleCount){
		
		RSSFeedParser rssFeedParser = null;
		Feed feed = null;
		List<FeedMessage> feedMessages = null;
		
		try{
			
			long rssExtractStartTime = System.currentTimeMillis();
			
			feedMessages = new LinkedList<FeedMessage>();
			
			for(String feedUrl: RSS_FEED_URL){
				rssFeedParser = new RSSFeedParser(feedUrl);
				feed = rssFeedParser.readFeed();
				   
				for (FeedMessage message : feed.getMessages()) {
					feedMessages.add(message);
				}
			}
			
			DecimalFormat df = new DecimalFormat("#.##");
			logger.info("Finished RSS Article search in "+Double.valueOf(df.format((double) (System.currentTimeMillis() - rssExtractStartTime)/1000.0))+"(s)");
			
		}catch(Exception ex){
			logger.info("Error while extracting RSS Feeds", ex);
		}
		
		return feedMessages;
	}

}

package com.ss.social.media.api;

import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConnection {

	private static final Logger logger = Logger.getLogger(TwitterConnection.class);
	  
	  private static TwitterConnection connection;
	  private static TwitterFactory twitterFactory;
	  
	  private static String OAUTH_CONSUMER_KEY = "e2Y40YLcf8Zxxl19PeXjgQKSu";  
	  private static String OAUTH_CONSMER_SECRET = "JkB0htMCjEO1FiLoLIAIvAqnbLotCLGYJCNUnHjTjf46G1lVlb";
	  private static String OAUTH_ACCESS_TOKEN = "100997084-5lfwseFGFHWQJqqR62yYr3fLWJbHZkH15aMgkQQ";
	  private static String OAUTH_ACCESS_TOKEN_SECRET = "Tgan8L1hILwGGbOslr6nrEbB7Yk3uUzJ5RQVzhn9PWYSj";
	  
	  private TwitterConnection()
	  {
		
		Properties prop = null;
	    ClassLoader loader = null;    
		InputStream stream = null;
		
		ConfigurationBuilder configurationBuilder = null;
	
		try
	    {
			prop = new Properties();
			loader = Thread.currentThread().getContextClassLoader();
			stream = loader.getResourceAsStream("configuration.properties");
			prop.load(stream);
			
			//Read Configurartion Properties
			OAUTH_CONSUMER_KEY = prop.getProperty("twitterOAuthConsumerKey");
			OAUTH_CONSMER_SECRET = prop.getProperty("twitterOAuthConsumerSecret");
			OAUTH_ACCESS_TOKEN = prop.getProperty("twitterOAuthAccessToken");
			OAUTH_ACCESS_TOKEN_SECRET = prop.getProperty("twitterOAuthAccessTokenSecret");
			
			
			
			configurationBuilder = new ConfigurationBuilder();
			
			configurationBuilder.setDebugEnabled(true)
			  .setOAuthConsumerKey(OAUTH_CONSUMER_KEY)
			  .setOAuthConsumerSecret(OAUTH_CONSMER_SECRET)
			  .setOAuthAccessToken(OAUTH_ACCESS_TOKEN)
			  .setOAuthAccessTokenSecret(OAUTH_ACCESS_TOKEN_SECRET);
			
			twitterFactory = new TwitterFactory(configurationBuilder.build());
	    	logger.info("Succesful Connection to Twitter");
	      
	    } catch (Exception ex)
	    {
	      logger.error("Error Connection to Twitter",ex);
	    }
	  }
	   
	  public static TwitterConnection getInstance()
	  {
	    if(connection == null)
	    {
	    	connection = new TwitterConnection();
	    }
	 
	    return connection;
	  }
	   
	  
	  public Twitter getConnection()
	  {
		return twitterFactory.getInstance();   
	  }
}

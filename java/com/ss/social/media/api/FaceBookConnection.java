package com.ss.social.media.api;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.conf.ConfigurationBuilder;


public class FaceBookConnection {

	private static final Logger logger = Logger.getLogger(FaceBookConnection.class);
	  
	  private static FaceBookConnection connection;
	  private static FacebookFactory facebookFactory;
	  
	  private static String APP_ID = "1503303319955144";  
	  private static String APP_SECRET = "11ad5690a7b3b54925fbb39cc29b8426";
	  

	  private FaceBookConnection()
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
			APP_ID = prop.getProperty("facebookAppId");
			APP_SECRET = prop.getProperty("facebookAppSecret");
	
			configurationBuilder = new ConfigurationBuilder();
			
			configurationBuilder.setDebugEnabled(true)
			  .setOAuthAppId(APP_ID)
			  .setOAuthAppSecret(APP_SECRET)
			  .setOAuthAccessToken("CAACEdEose0cBAM8Mf9ktZCgn9kTRBgicRQ6XcYaq8Ht2fbSlQId3jzUWuFfYTnqlA64E5wfOnQQlA671y1OHos1FwjEijG1251Esw0jom2ZAv1H5UZAFqupBum3jXqopgDKK7OpMwxDI7YsPUzn9xlhGC3ribUzAujELSSdtGKdxUZCYtmFLb758ZA3G9mY4enk1oQbUSE1LQCBSpDGZC7")
			  .setOAuthPermissions("email,user_likes,publish_actions,publish_stream,email,user_subscriptions,status_update,manage_notifications,user_website,location,user_friends,user_location,manage_pages");
			
			facebookFactory = new FacebookFactory(configurationBuilder.build());
	    	logger.info("Succesful Connection to Facebook");
	      
	    } catch (Exception ex)
	    {
	      logger.error("Error Connection to Facebook",ex);
	    }
	  }
	   
	  public static FaceBookConnection getInstance()
	  {
	    if(connection == null)
	    {
	    	connection = new FaceBookConnection();
	    }
	 
	    return connection;
	  }
	   
	  
	  public Facebook getConnection()
	  {
		return facebookFactory.getInstance();   
	  }
}

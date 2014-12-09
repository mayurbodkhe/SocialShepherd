package com.ss.db.dao.mysql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import snaq.db.ConnectionPool;

public class MySqlDBConnectionPool
{
  
  private static final Logger logger = Logger.getLogger(MySqlDBConnectionPool.class);
  
  private static MySqlDBConnectionPool dbConnectionPool;
  private ConnectionPool dbConnPool;
  
  private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
  private static String DB_URL = "jdbc:mysql://localhost/socialshepherd";
  private static String DB_USER = "root";
  private static String DB_PASS = "admin";
  
  private static int MIN_POOL = 5;
  private static int MAX_POOL = 10;
  private static int MAX_SIZE = 30;
  private static int IDEL_TIMEOUT = 900;
  
 
  private MySqlDBConnectionPool()
  {
	
	Properties prop = null;
    ClassLoader loader = null;    
	InputStream stream = null;
	
	try
    {
		prop = new Properties();
		loader = Thread.currentThread().getContextClassLoader();
		stream = loader.getResourceAsStream("configuration.properties");
		prop.load(stream);
		
		//Read Configurartion Properties
		JDBC_DRIVER = prop.getProperty("jdbcDriverClassName");
		DB_URL = prop.getProperty("dbURL");
		DB_USER = prop.getProperty("dbUserName");
		DB_PASS = prop.getProperty("dbPassword");
		MIN_POOL = Integer.parseInt(prop.getProperty("dbConnMinPool"));
		MAX_POOL = Integer.parseInt(prop.getProperty("dbConnMaxPool"));
		MAX_SIZE = Integer.parseInt(prop.getProperty("dbConnMaxSize"));
		IDEL_TIMEOUT = Integer.parseInt(prop.getProperty("dbConnIdleTimeOut"));
		
		
		@SuppressWarnings("rawtypes")
		Class c = Class.forName(JDBC_DRIVER);
    	Driver driver = (Driver)c.newInstance();
    	DriverManager.registerDriver(driver);
    	
    	dbConnPool = new ConnectionPool("ssDBConnPool", MIN_POOL, MAX_POOL, MAX_SIZE, IDEL_TIMEOUT, DB_URL, DB_USER, DB_PASS);
   	
    	logger.info("Succesful DB ConnectionPool Creation");
      
    } catch (Exception ex)
    {
      logger.error("Error creation DB Connection Pool",ex);
    }
  }
   
  public static MySqlDBConnectionPool getInstance()
  {
    if(dbConnectionPool == null)
    {
    	dbConnectionPool = new MySqlDBConnectionPool();
    	
    }
 
    return dbConnectionPool;
  }
   
  
  public Connection getConnection()
  {
	Connection dbConnect = null;
	try
    {
      dbConnect = dbConnPool.getConnection();
    }
    catch (SQLException ex)
    {
    	logger.error("Error getConnection",ex);
    }
 
    return dbConnect;   
  }
  
}
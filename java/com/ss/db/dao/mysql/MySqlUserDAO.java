package com.ss.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ss.db.bean.UserBean;
import com.ss.db.dao.UserDAO;
import com.ss.ui.domain.User;

public class MySqlUserDAO implements UserDAO{

	
	public static String userAuthenticationSQL = "SELECT userLoginId FROM tblUserLogin WHERE userLoginId=? AND userLoginPassword=?";
	public static String getUserDetailsSQL = "SELECT firstName, lastName, email, phone, location, userId, userRoleId FROM tblUserDetails JOIN tblUserLogin ON tblUserDetails.userId = tblUserLogin.userLoginId WHERE tblUserDetails.userId=?";
	
	public MySqlUserDAO(){}
		
	
	@Override
	public boolean isAuthorizedUser(String userId, String password) {
		
		boolean isValidUser = false;
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		try 
		{
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			statement = dbConnect.prepareStatement(userAuthenticationSQL);
			statement.setString(1, userId);
			statement.setString(2, password);
			
			resultSet = statement.executeQuery();
			
			if(resultSet.next())
				isValidUser = true;
				
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
			
		return isValidUser;
	}

	@Override
	public void updateUserDetails(UserBean userBean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addUser(UserBean userBean) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public User getUserDetails(String userId) {
		
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		Connection dbConnect = null;
		
		User user = null;
		try 
		{
			dbConnect = MySqlDBConnectionPool.getInstance().getConnection();
			statement = dbConnect.prepareStatement(getUserDetailsSQL);
			statement.setString(1, userId);
			
			resultSet = statement.executeQuery();
			
			if(resultSet.next())
				user = new User();
				
				user.setFirstName(resultSet.getString(1));
				user.setLastName(resultSet.getString(2));
				user.setEmail(resultSet.getString(3));
				user.setPhone(resultSet.getString(4));
				user.setLocation(resultSet.getString(5));
				user.setUserId(resultSet.getString(6));
				user.setRole(resultSet.getInt(7));
						
			
		}catch (Exception ex){
			ex.printStackTrace();
		
		}finally{
			
			if(resultSet != null){
				try{
					resultSet.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
				

			if(statement != null){
				try{
					statement.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			
			if(dbConnect != null){
				try{
					dbConnect.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
	
		}
			
		return user;
	}

}

package com.ss.db.dao;

import com.ss.db.bean.UserBean;
import com.ss.ui.domain.User;

public interface UserDAO {
	
	public boolean isAuthorizedUser(String userId, String password); 
	
	public void updateUserDetails(UserBean userBean);
	
	public void addUser(UserBean userBean);
	
	public User getUserDetails(String userId);

}

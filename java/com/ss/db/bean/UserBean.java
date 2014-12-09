package com.ss.db.bean;

import java.io.Serializable;

public class UserBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String userLoginId;
	private String userLoginPassword;
	private int userRoleId;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String location;
	
	public UserBean(){}

	public String getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public String getUserLoginPassword() {
		return userLoginPassword;
	}

	public void setUserLoginPassword(String userLoginPassword) {
		this.userLoginPassword = userLoginPassword;
	}

	public int getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(int userRoleId) {
		this.userRoleId = userRoleId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}

package com.brewery.model;

public class ProfilePassword {

	private String username;
    private String password;
    private String newPassword;
    
    
	public ProfilePassword() {
		super();
	}

	public ProfilePassword (String username  ) {
		super();
		this.username = username;
		this.password = "";
		this.newPassword = "";
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@Override
	public String toString() {
		return "ProfilePassword [ username=" + username + ", password=" + password + ", newPassword=" + newPassword + "]";
	}
	
}

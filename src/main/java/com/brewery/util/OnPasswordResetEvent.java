package com.brewery.util;

import org.springframework.context.ApplicationEvent;

import com.brewery.model.Password;


public class OnPasswordResetEvent extends ApplicationEvent {

	private String serverUrl;
	private String appPath;
    private Password password;

    public OnPasswordResetEvent(Password password, String serverUrl, String appPath ) {
        super(password);

        this.password = password;
        this.serverUrl = serverUrl;
        this.appPath = appPath;
        
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
    
    
}

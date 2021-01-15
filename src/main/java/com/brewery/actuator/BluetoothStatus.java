package com.brewery.actuator;

import org.springframework.stereotype.Component;

@Component
public class BluetoothStatus {
	boolean up;
	String message;
	public boolean isUp() {
		return up;
	}
	public void setUp(boolean up) {
		this.up = up;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "BluetoothStatus [up=" + up + ", message=" + message + "]";
	}

	
}

package com.brewery.model;

public class Message {

	private String target;
	private String data;
	
	public Message() {
		// TODO Auto-generated constructor stub
	}

	public Message(String target, String data) {
		super();
		this.target = target;
		this.data = data;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Message [target=" + target + ", data=" + data + "]";
	}

}

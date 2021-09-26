package com.brewery.model;

public class Info {
	
	private String header;
	private String message;
	private String hrefLink;
	private String hrefText;
	
	
	public Info() {
		super();
		header = "";
		message = "";
	}

	public Info(String header, String message) {
		super();
		this.header = header;
		this.message = message;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHrefLink() {
		return hrefLink;
	}

	public void setHrefLink(String hrefLink) {
		this.hrefLink = hrefLink;
	}

	public String getHrefText() {
		return hrefText;
	}

	public void setHrefText(String hrefText) {
		this.hrefText = hrefText;
	}

	@Override
	public String toString() {
		return "Info [header=" + header + ", message=" + message + ", hrefLink=" + hrefLink + ", hrefText=" + hrefText
				+ "]";
	}

}

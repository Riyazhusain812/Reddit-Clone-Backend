package com.RedditClone.exceptions;

public class PostNotFoundException extends RuntimeException {
	public PostNotFoundException(String exMessage, Exception exception) {
		super(exMessage, exception);
	}

	public PostNotFoundException(String exMessage) {
		super(exMessage);
	}

}

package com.RedditClone.exceptions;

public class SubredditNotFoundException extends RuntimeException {
    public SubredditNotFoundException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public SubredditNotFoundException(String exMessage) {
        super(exMessage);
    }
}
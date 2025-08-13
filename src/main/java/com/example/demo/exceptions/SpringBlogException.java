package com.example.demo.exceptions;

public class SpringBlogException extends RuntimeException {
    public SpringBlogException(String exMessage, Exception e) {
        super(exMessage, e);
    }

    public SpringBlogException(String exMessage) {
        super(exMessage);
    }
}

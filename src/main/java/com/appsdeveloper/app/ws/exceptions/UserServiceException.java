package com.appsdeveloper.app.ws.exceptions;

public class UserServiceException extends RuntimeException{

    private static final long serialVersionUID = 3337713970742862216L;

    public UserServiceException(String message){
        super(message);
    }
}

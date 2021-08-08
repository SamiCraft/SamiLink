package com.samifying.link.error;

public class LoginRejectedException extends RuntimeException {

    public LoginRejectedException(String message) {
        super(message);
    }
}

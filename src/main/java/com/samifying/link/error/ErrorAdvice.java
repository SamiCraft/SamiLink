package com.samifying.link.error;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErrorAdvice {

    @ExceptionHandler(LoginRejectedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorObject handleLoginRejectedException(LoginRejectedException e, @NotNull HttpServletRequest request) {
        return new ErrorObject(e, request.getServletPath());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorObject handleException(Exception e, @NotNull HttpServletRequest request) {
        return new ErrorObject(e, request.getServletPath());
    }
}


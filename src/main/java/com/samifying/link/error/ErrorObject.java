package com.samifying.link.error;

public class ErrorObject {

    private String name;
    private String message;
    private String path;

    public ErrorObject(Exception e, String path) {
        this.name = e.getClass().getName();
        this.message = e.getMessage();
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

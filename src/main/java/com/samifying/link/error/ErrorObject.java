package com.samifying.link.error;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ErrorObject {

    private String name;
    private String message;
    private String path;

    public ErrorObject(@NotNull Exception e, String path) {
        this.name = e.getClass().getName();
        this.message = e.getMessage();
        this.path = path;
    }
}

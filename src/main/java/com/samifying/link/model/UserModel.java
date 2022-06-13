package com.samifying.link.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel {

    private final String id;
    private final String name;
    private final String nickname;
    private final String avatar;
    private Boolean supporter;
    private Boolean moderator;

}

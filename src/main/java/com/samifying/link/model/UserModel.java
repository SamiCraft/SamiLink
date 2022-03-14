package com.samifying.link.model;

import lombok.Data;

@Data
public class UserModel {

    private final String id;
    private final String name;
    private final String nickname;
    private final String avatar;
    private boolean supporter;
    private boolean moderator;

}

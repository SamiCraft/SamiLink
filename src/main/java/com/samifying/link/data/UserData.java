package com.samifying.link.data;

import lombok.Data;

@Data
public class UserData {

    private final String id;
    private final String name;
    private final String nickname;
    private final String avatar;
    private boolean supporter;
    private boolean moderator;

}

package com.samifying.link.data;

public class UserData {

    private final String id;
    private final String name;
    private final String avatar;
    private boolean supporter;
    private boolean moderator;

    public UserData(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.supporter = false;
        this.moderator = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public boolean isSupporter() {
        return supporter;
    }

    public void setSupporter(boolean supporter) {
        this.supporter = supporter;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }
}

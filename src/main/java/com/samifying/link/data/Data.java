package com.samifying.link.data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "data")
public class Data {

    @Id
    @Column(name = "data_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "discord_id")
    private String discordId;

    private String uuid;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Date createdAt;

    public Data() {
    }

    public Data(int id, String discordId, String uuid, Date createdAt) {
        this.id = id;
        this.discordId = discordId;
        this.uuid = uuid;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

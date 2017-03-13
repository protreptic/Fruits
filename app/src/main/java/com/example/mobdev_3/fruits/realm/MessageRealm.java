package com.example.mobdev_3.fruits.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageRealm extends RealmObject {

    @PrimaryKey
    private String id;
    private String createdAt;
    private int status;
    private String author;
    private String message;

    public MessageRealm() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}

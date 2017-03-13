package com.example.mobdev_3.fruits.service.chat.model;

import java.sql.Date;
import java.util.UUID;

public final class Message {

    private String id;
    private String createdAt;
    private int status;
    private String author;
    private String message;

    public Message(String author, String message) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date(System.currentTimeMillis()).toString();
        this.status = 0;
        this.author = author;
        this.message = message;
    }

    public String getAuthor() {
        return author.substring(0, 8);
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusRaw() {
        return status;
    }

    public String getStatus() {
        switch (status) {
            case 0: {
                return "Не отправлено";
            }
            case 1: {
                return "Отправлено";
            }
            case 2: {
                return "Доставлено";
            }
            case 3: {
                return "Прочитано";
            }
            default: {
                return status + "";
            }
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (id != null ? !id.equals(message.id) : message.id != null) return false;
        return createdAt != null ? createdAt.equals(message.createdAt) : message.createdAt == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }
}

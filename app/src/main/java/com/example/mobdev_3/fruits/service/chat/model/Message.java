package com.example.mobdev_3.fruits.service.chat.model;

public final class Message {

    private String author;
    private String date;
    private String message;

    public Message(String author, String date, String message) {
        this.author = author;
        this.date = date;
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

}

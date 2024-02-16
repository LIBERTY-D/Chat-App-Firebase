package com.daniel.chatapp.Models;

public class Message {


    String message;

    String from;
    public Message() {

    }
    public Message(String message, String from) {
        this.message = message;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }
}



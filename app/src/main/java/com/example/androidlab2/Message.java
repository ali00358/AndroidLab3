package com.example.androidlab2;

public class Message {

    private long id;
    private String message;
    private boolean isSent = true;

    Message(long id, String message, int outgoing){
        this.id = id;
        this.message = message;

        //ternary operator to assign boolean to outgoing
        this.isSent = (outgoing == 1)? true : false;
    }

    Message(long id, String message){
        this(id, message, 1);
    }

    Message(String message, Boolean outgoing){
        this.message = message;
        this.isSent = outgoing;
    }

    Message(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOutgoing() {
        return isSent ;
    }

    public void setOutgoing(boolean outgoing) {
        this.isSent = outgoing;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

package com.example.breezil.chatty.Activity;

/**
 * Created by breezil on 12/5/2017.
 */

public class Chat_Model {

    public boolean seen;
    public long timestamp;


    public Chat_Model() {
    }

    public Chat_Model(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

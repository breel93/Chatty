package com.example.breezil.chatty.Activity.utils;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class MessageId {


    @Exclude
    public String MessageId;

    public <T extends MessageId> T withId(@NonNull final String id) {
        this.MessageId = id;
        return (T) this;
    }
}

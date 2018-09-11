package com.example.breezil.chatty.Activity.database.model;

/**
 * Created by breezil on 8/7/2017.
 */

public class Friends {
    private String date;
   // private String UserName

    public Friends() {
    }

    public Friends(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

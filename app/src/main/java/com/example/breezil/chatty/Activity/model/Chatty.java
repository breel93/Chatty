package com.example.breezil.chatty.Activity.model;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by breezil on 8/3/2017.
 */

public class Chatty extends Application {
    private FirebaseAuth mAuth;
    private DatabaseReference mDataRef;

    @Override
    public void onCreate() {
        super.onCreate();

       //offline capability
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



        //-----Picasso---//
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);






        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            mDataRef = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(mAuth.getCurrentUser().getUid());

            mDataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        mDataRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                        // mDataRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
                        //mDataRef.child("online").setValue(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
}

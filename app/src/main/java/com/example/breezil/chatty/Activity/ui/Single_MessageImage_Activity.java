package com.example.breezil.chatty.Activity.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.breezil.chatty.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Single_MessageImage_Activity extends AppCompatActivity {

    ImageView mSingleImage;
    String messageId;
    String fromWho;
    DatabaseReference mMessageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__message_image_);

        mSingleImage = (ImageView) findViewById(R.id.single_message_image);

        messageId = getIntent().getStringExtra("message_id");
        fromWho = getIntent().getStringExtra("from_id");

        mMessageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(fromWho).child(messageId);

        mMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("message").getValue().toString();

                showImage(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void showImage(final String image) {

        Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_avatar).into(mSingleImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.default_avatar).into(mSingleImage);
            }
        });
    }
}

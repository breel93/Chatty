package com.example.breezil.chatty.Activity.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountSettingsActivity extends AppCompatActivity {

    private LinearLayout mLogout;
    DatabaseReference mUserRef;

    FirebaseAuth mAuth;

    TextView mUsername, mFullname, mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getCurrentUser().getUid();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);


        mUsername = (TextView) findViewById(R.id.accountSettingsUserNameText);
        mFullname = (TextView) findViewById(R.id.accountSettingsFullNameText);
        mEmail = (TextView) findViewById(R.id.accountSettingsEmailText);


        loadUserData();

        mLogout = (LinearLayout) findViewById(R.id.logOutText);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendToStart();

            }
        });
    }

    private void sendToStart() {
        FirebaseAuth.getInstance().signOut();
        Intent startIntent = new Intent(AccountSettingsActivity.this,LoginActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        finish();
    }


    private void loadUserData(){
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
//                String email = dataSnapshot.child("email").getValue().toString();
//                String fullnamme = dataSnapshot.child("full_name").getValue().toString();

                mUsername.setText(username);
//                mFullname.setText(fullnamme);
//                mEmail.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

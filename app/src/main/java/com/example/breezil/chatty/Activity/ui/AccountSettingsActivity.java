package com.example.breezil.chatty.Activity.ui;

import android.content.Intent;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.breezil.chatty.Activity.adapters.Edit_Name_Settings;
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

    Toolbar mToolBar;

    FirebaseAuth mAuth;

    TextView mUsername, mFullname, mEmail;

    LinearLayout mUserNameLayout, mFullNameLayout;

    String fullnamme;
    String username;

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

        mToolBar = (Toolbar) findViewById(R.id.accountsettings_appbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        mUserNameLayout = findViewById(R.id.accountsettingUserName);
        mFullNameLayout = findViewById(R.id.accountsettingFullName);




        loadUserData();

        mLogout = (LinearLayout) findViewById(R.id.logOutText);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendToStart();

            }
        });


        mUserNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDialog();
            }
        });

        mFullNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDialog();
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
                username = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                if(dataSnapshot.hasChild("full_name")){
                    fullnamme = dataSnapshot.child("full_name").getValue().toString();
                    mFullname.setVisibility(View.VISIBLE);
                    mFullname.setText(fullnamme);
                }

                mUsername.setText(username);

                mEmail.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void gotoDialog() {
        Bundle args = new Bundle();
        if(fullnamme != null){
            args.putString("full_name",fullnamme);
        }

        args.putString("user_name",username);
        Edit_Name_Settings edit_name_settings = new Edit_Name_Settings();
        edit_name_settings.setArguments(args);
        edit_name_settings.show(getFragmentManager(),"Dialog");

    }
}

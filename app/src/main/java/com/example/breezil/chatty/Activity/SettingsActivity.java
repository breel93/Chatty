package com.example.breezil.chatty.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private TextView mLogout;
    private Toolbar mToolbar;

    private CardView mUserInfo;
    private CircleImageView mUserImage;
    private TextView mUserName, mUserStatus;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mToolbar = (Toolbar) findViewById(R.id.settingBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Settings");

        mUserInfo = (CardView) findViewById(R.id.userInfoCard);
        mUserImage = (CircleImageView) findViewById(R.id.userSettingsImage);
        mUserName = (TextView) findViewById(R.id.userSettingsName);
        mUserStatus = (TextView) findViewById(R.id.userSettingStatus);


        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getCurrentUser().getUid();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);


        userInfo();


        mUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,SettupActivity.class));
            }
        });







        mLogout = (TextView) findViewById(R.id.logOutText);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendToStart();

            }
        });



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.chat:

                        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
                        break;
                    case R.id.search:
                        startActivity(new Intent(SettingsActivity.this,AllUsersActivity.class));
                        break;
                    case R.id.user:

                        startActivity(new Intent(SettingsActivity.this,SettupActivity.class));

                        break;
                    case R.id.settings:
                        break;


                }



                return false;
            }
        });

    }

    private void userInfo() {

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();


                mUserName.setText(name);
                mUserStatus.setText(status);

                if(!thumb_image.equals("default")){
                    Picasso.with(SettingsActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(mUserImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(thumb_image).placeholder(R.drawable.default_avatar).into(mUserImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Creating the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //set the menu layout
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    //Option menu selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //if logout item is selected


        if(item.getItemId() == R.id.main_logout) {
            sendToStart();
        }


        return true;
    }

    private void sendToStart() {
        FirebaseAuth.getInstance().signOut();
        Intent startIntent = new Intent(SettingsActivity.this,LoginActivity.class);
        startActivity(startIntent);
        finish();
    }


}

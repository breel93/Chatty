package com.example.breezil.chatty.Activity.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.breezil.chatty.Activity.adapters.MyPagerAdapter;
import com.example.breezil.chatty.Activity.utils.BottomNavigationHelper;
import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
   //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDataRef;
    private DatabaseReference mDataRef;

    private FirebaseUser currentUser;



    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private MyPagerAdapter myPagerAdapter;

    private TabLayout mTablayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            mUserDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        mDataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        checkUserExist();
        //Toolbar

        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.tabpager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setCurrentItem(1,false);

        mTablayout = (TabLayout) findViewById(R.id.mainTabView);
        mTablayout.setupWithViewPager(mViewPager);
        mTablayout.getTabAt(0).setIcon(R.mipmap.ic_req);
        mTablayout.getTabAt(1).setText(R.string.app_name);
        mTablayout.getTabAt(2).setIcon(R.mipmap.ic_friend);
        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL);





        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.chat:
                        break;
                    case R.id.search:
                        startActivity(new Intent(MainActivity.this,AllUsersActivity.class));
                        break;
                    case R.id.user:
                        startActivity(new Intent(MainActivity.this,SettupActivity.class));
                        break;
                    case R.id.settings:
                        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                        break;


                }



                return false;
            }
        });



    }

    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null ){

            final String user_id = mAuth.getCurrentUser().getUid();
            mDataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user_id)){
                        Intent otherSettup = new Intent(MainActivity.this,OtherSettup.class);
                        otherSettup.putExtra("user_id",user_id);
                        otherSettup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(otherSettup);
                    }else {
                        //online
                        mUserDataRef.child("online").setValue("true");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       //Check if user is signed in and update UI accordingly

        //If currentuser is null the user is not signed In
        if(currentUser == null){
            sendToStart();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //offline
        if(currentUser != null) {
            mUserDataRef.child("online").setValue(ServerValue.TIMESTAMP);
            //firebase time stamp
           // mDataRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(startIntent);
        finish();
    }


}

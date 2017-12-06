package com.example.breezil.chatty.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
   //Firebase
    private FirebaseAuth mAuth;
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
            mDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.mainActtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("chatty");
        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.tabpager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setCurrentItem(1,false);

        mTablayout = (TabLayout) findViewById(R.id.mainTabView);
        mTablayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
       //Check if user is signed in and update UI accordingly

        //If currentuser is null the user is not signed In
        if(currentUser == null){
            sendToStart();
        }else {
            //online
            mDataRef.child("online").setValue("true");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //offline
        if(currentUser != null) {
            mDataRef.child("online").setValue(ServerValue.TIMESTAMP);
            //firebase time stamp
           // mDataRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
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

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if(item.getItemId() == R.id.main_setting) {
            Intent settingIntent = new Intent(MainActivity.this,SettupActivity.class);
            //Intent settingIntent = new Intent(MainActivity.this,FriendsFragment.class);
            startActivity(settingIntent);

        }
        if(item.getItemId() == R.id.allUsers){
            Intent allUserIntent = new Intent (MainActivity.this,AllUsersActivity.class);
            startActivity(allUserIntent);

        }


//        switch (item.getItemId()){
//            case (R.id.main_logout):
//                mAuth.signOut();
//                sendToStart();
//            case(R.id.main_setting):
//                Intent settingIntent = new Intent(MainActivity.this,SettupActivity.class);
//                startActivity(settingIntent);
//                finish();
////            case(R.id.allUsers):
////                Intent allUserIntent = new Intent (MainActivity.this,AllUsersActivity.class);
////                startActivity(allUserIntent);
////                finish();

       // }

        return true;
    }
}

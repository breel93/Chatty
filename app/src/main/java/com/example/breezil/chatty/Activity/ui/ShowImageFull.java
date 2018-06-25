package com.example.breezil.chatty.Activity.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

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

public class ShowImageFull extends AppCompatActivity {

    private DatabaseReference mUserRef;
    private ImageView mFullImage;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_full);

        mAuth = FirebaseAuth.getInstance();

        final String currentUser = mAuth.getCurrentUser().getUid();


        mFullImage = (ImageView) findViewById(R.id.fullImage);

        //get intent from the previous ,i.e the click item id as it is in database.
        final String user_id = getIntent().getStringExtra("user_id");

        mToolbar = (Toolbar) findViewById(R.id.fullImageBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("thumb_image").getValue().toString();
                String currentUserName = dataSnapshot.child("name").getValue().toString();

                if(currentUser.equals(user_id)){
                    getSupportActionBar().setTitle("Me");
                }else {
                    getSupportActionBar().setTitle(currentUserName);
                }



                showImage(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void showImage(final String image) {

        Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_avatar).into(mFullImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.default_avatar).into(mFullImage);
            }
        });
    }

    //Creating the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //set the menu layout
        getMenuInflater().inflate(R.menu.full_image_menu_bar,menu);
        return true;
    }
    //Option menu selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //if logout item is selected


        if(item.getItemId() == R.id.saveImage) {
            Toast.makeText(ShowImageFull.this,"Save",Toast.LENGTH_LONG).show();
        }


        return true;
    }
}

package com.example.breezil.chatty.Activity.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breezil.chatty.Activity.utils.BottomNavigationHelper;
import com.example.breezil.chatty.Activity.model.Users;
import com.example.breezil.chatty.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {


    private RecyclerView mUserlist;

    //searchBar
    private Toolbar mSearchBar;
    private EditText mSearcTextbar;
    private ImageButton mSearchImageBtnBar;





    //firebase ref
    private DatabaseReference mUserRef;

    private DatabaseReference mUserDataRef;

    private FirebaseAuth mAuth;

    private FirebaseUser current_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        //Firebase

        mAuth = FirebaseAuth.getInstance();


        mSearchBar = (Toolbar) findViewById(R.id.searchBar);

        setSupportActionBar(mSearchBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.search_bar,null);
        actionBar.setCustomView(actionbarView);



        mSearcTextbar = (EditText) findViewById(R.id.searchTextBar);
        mSearchImageBtnBar = (ImageButton) findViewById(R.id.searchBtnBar);





        /**
        * firebase database that points to the user node
         */
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef.keepSynced(true);



        /*
        * Get the current user assigned to current_user variable
         */
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        /*
        * set a child node to the Users,(user id) as to store the user profiles
         */
        mUserDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());




        mUserlist = (RecyclerView) findViewById(R.id.userList);
       // mUserlist.setHasFixedSize(true);
        mUserlist.setLayoutManager(new LinearLayoutManager(this));


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.chat:
                        startActivity(new Intent(AllUsersActivity.this,MainActivity.class));
                        break;
                    case R.id.search:

                        break;
                    case R.id.user:
                        startActivity(new Intent(AllUsersActivity.this,SettupActivity.class));
                        break;
                    case R.id.settings:
                        startActivity(new Intent(AllUsersActivity.this,SettingsActivity.class));
                        break;


                }



                return false;
            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();
        //Firebase recycler Adapter
        //from firebase ui, firebase default recycler adapter here we set a firebase adapter object
        // firebaseRecycleradapter) thattake the model class ,the custom single layout ,
        // the view holderclass and the database reference
        //as arguments


        mSearchImageBtnBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearcTextbar.getText().toString();
                if(!TextUtils.isEmpty(searchText)){
                    search(searchText);
                }else{
                    Toast.makeText(AllUsersActivity.this,"Enter User Name",Toast.LENGTH_LONG).show();
                }
            }
        });


        mUserDataRef.child("online").setValue("true");

    }

    private void search(String searchText) {

        Query searchQuery = mUserRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        //Query searchQuery = mUserRef.orderByChild("name").equalTo(searchText);

        FirebaseRecyclerAdapter<Users, UsersViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,    //modelclass
                R.layout.usersinglelayout, //layout design in the R file
                UsersViewHolder.class,    //viewHolder class
                searchQuery                  //firebase reference to user node.


        ) {

            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                //set data for the view holders

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setThumb_image(model.getThumb_image(),getApplicationContext());


                //get users position on when its clicked
                final String user_id = getRef(position).getKey();


                //onclick on the user lists
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(AllUsersActivity.this,UserProfile.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        //attach the recycler view to the firebase adapter
        mUserlist.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(current_user != null) {
           mUserDataRef.child("online").setValue(ServerValue.TIMESTAMP);
      }
    }

    //ViewHolder Adapter
    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        //method get the user name from firebase and sets in the textview
        public void setName(String name){
            TextView userNametext = (TextView) mView.findViewById(R.id.userNametext);
            userNametext.setText(name);
        }
        //method get the user status from firebase and sets in the textview
        public void setStatus(String status){
            TextView userStatustext = (TextView) mView.findViewById(R.id.userStatustext);
            userStatustext.setText(status);
        }
        //method get the user image as thumbnail from firebase and sets in the circular image view
        public void setThumb_image(final String thumb_image, final Context cntxt){
            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.userSingleImage);
            Picasso.with(cntxt).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_avatar).into(userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(cntxt).load(thumb_image)
                            .placeholder(R.drawable.default_avatar).into(userImageView);
                }
            });
        }
    }
}

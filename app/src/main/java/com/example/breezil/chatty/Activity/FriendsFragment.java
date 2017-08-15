package com.example.breezil.chatty.Activity;


import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.breezil.chatty.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import static android.R.attr.thumb;
import static android.R.attr.visibility;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendList;

    //Firebase
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUsersDb;
    private FirebaseAuth mAuth;

    private String mCurrentUserId;
    private  View mView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       mView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendList = (RecyclerView) mView.findViewById(R.id.FriendsList);
        //firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //firebase current user instance
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        //Firebase database reference to the friends node
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);
        mFriendDatabase.keepSynced(true);
        //Firebase user node reference to retrieve the remaining users field
        mUsersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDb.keepSynced(true);

       mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
         //model class,viewlayout, ViewHolder Class and the Database query(firebase)
          Friends.class,
          R.layout.usersinglelayout,
          FriendsViewHolder.class,
          mFriendDatabase

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, final Friends model, int position) {

                //set date
                viewHolder.setDate(model.getDate());

                //to retrieve the remaining field on the position
                final String list_user_id = getRef(position).getKey();
                mUsersDb.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                        //String userOnline = dataSnapshot.child("online").getValue().toString();

                        //online
                        if(dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }

                        //set the name
                        viewHolder.setName(userName);
                        viewHolder.setImage(userThumbImage,getContext());

                        //when a view is clicked
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options [] = new CharSequence[]{"Open Profile", "Send Message"};
                                AlertDialog.Builder  builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Click event for selected item
                                        if(which == 0){
                                            Intent profileIntent = new Intent(getContext(),UserProfile.class);
                                            profileIntent.putExtra("user_id",list_user_id);
                                            startActivity(profileIntent);
                                        }
                                        if(which == 1){
                                            Intent chatIntent = new Intent (getContext(),ChatActivity.class);
                                            chatIntent.putExtra("user_id",list_user_id);
                                            chatIntent.putExtra("user_name",userName);
                                            startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mFriendList.setAdapter(firebaseRecyclerAdapter);
    }
    //View Holder Class
    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
          View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        //View Holder to set each fields
        public void setDate(String date){
            TextView userNameView = (TextView) mView.findViewById(R.id.userStatustext);
            userNameView.setText(date);
        }
        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.userNametext);
            userNameView.setText(name);
        }
        //set circle image view
        public void setImage (final String Image, final Context cntxt){
            final CircleImageView userImage = (CircleImageView) mView.findViewById(R.id.userSingleImage);
            Picasso.with(cntxt).load(Image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_avatar).into(userImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(cntxt).load(Image)
                            .placeholder(R.drawable.default_avatar).into(userImage);
                }
            });


        }

        //userOnline
        public void setUserOnline(String online_status){
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.userOnlineIcon);
            if (online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }


    }
}

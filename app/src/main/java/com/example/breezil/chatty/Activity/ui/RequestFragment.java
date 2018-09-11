package com.example.breezil.chatty.Activity.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.breezil.chatty.Activity.database.model.Users;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView requestList;

    private DatabaseReference mRequestDb;
    private DatabaseReference mUserDb;

    private FirebaseAuth mAuth;

    private String mCurrent_Uid;

    private View mView;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_request, container, false);

        requestList = (RecyclerView) mView.findViewById(R.id.Request_List);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_Uid = mAuth.getCurrentUser().getUid();

        mRequestDb = FirebaseDatabase.getInstance().getReference().child("Friend_request").child(mCurrent_Uid);
        mRequestDb.keepSynced(true);

        mUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDb.keepSynced(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        requestList.setLayoutManager(layoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,RequestHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, RequestHolder>(
                Users.class,
                R.layout.usersinglelayout,
                RequestHolder.class,
                mRequestDb
        ) {
            @Override
            protected void populateViewHolder(final RequestHolder viewHolder, Users model, int position) {

                final String list_Uid = getRef(position).getKey();
                mUserDb.child(list_Uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userStat = dataSnapshot.child("status").getValue().toString();
                        String userThumbImage = dataSnapshot.child("thumb_image").getValue().toString();


                        viewHolder.setName(userName);
                        viewHolder.setImage(userThumbImage,getContext());
                        viewHolder.setStatus(userStat);

                        if(dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setOnline(userOnline);
                        }

                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profileIntent = new Intent(getContext(),UserProfile.class);
                                profileIntent.putExtra("user_id",list_Uid);
                                startActivity(profileIntent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        requestList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestHolder extends RecyclerView.ViewHolder{

        View view;
        public RequestHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name){

            TextView nameText = (TextView) itemView.findViewById(R.id.userNametext);
            nameText.setText(name);

        }

        public void setStatus(String status){
            TextView statusTest = (TextView) itemView.findViewById(R.id.userStatustext);
            statusTest.setText(status);

        }

        public void setImage (final String Image, final Context cntxt){
            final CircleImageView userImage = (CircleImageView) itemView.findViewById(R.id.userSingleImage);
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
        public void setOnline(String online_stat){
            ImageView setOnline = (ImageView) itemView.findViewById(R.id.userOnlineIcon);
            if (online_stat.equals("true")){
                setOnline.setVisibility(View.VISIBLE);
            }else {
                setOnline.setVisibility(View.INVISIBLE);
            }

        }

    }
}

package com.example.breezil.chatty.Activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private RecyclerView mChatList;

    private DatabaseReference mChatRef;
    private DatabaseReference mMessagedb;
    private DatabaseReference mUserdb;

    private FirebaseAuth mAuth;

    private String mCurrent_Uid;

    private View mView;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);

        mChatList = (RecyclerView) mView.findViewById(R.id.Chat_List);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_Uid = mAuth.getCurrentUser().getUid();

        mChatRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_Uid);
        mChatRef.keepSynced(true);

        mUserdb = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessagedb = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_Uid);
        mUserdb.keepSynced(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(layoutManager);





        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query chatQuery = mChatRef.orderByChild("date");

        FirebaseRecyclerAdapter<Chat_Model, ChatHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat_Model, ChatHolder>(
                Chat_Model.class,
                R.layout.usersinglelayout,
                ChatHolder.class,
                chatQuery
        ) {
            @Override
            protected void populateViewHolder(final ChatHolder viewHolder, final Chat_Model model, int position) {

                final String list_Uid = getRef(position).getKey();



                final Query lastMessageQuery = mMessagedb.child(list_Uid).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String messageType = dataSnapshot.child("type").getValue().toString();

                        if(messageType.equals("image")){

                            viewHolder.setMessage("Image",model.isSeen());

                        }else if(messageType.equals("text")){
                            String data = dataSnapshot.child("message").getValue().toString();
                            viewHolder.setMessage(data,model.isSeen());
                        }
                        else {
                            viewHolder.setMessage("Media",model.isSeen());
                        }



                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mUserdb.child(list_Uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String user_name = dataSnapshot.child("name").getValue().toString();
                        String user_thumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                            String user_online = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setOnline(user_online);
                        }
                        viewHolder.setName(user_name);
                        viewHolder.setUserImage(user_thumb,getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("user_id", list_Uid);
                                chatIntent.putExtra("user_name", user_name);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };


        mChatList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setMessage(String message, boolean isSeen){
            TextView userMessage = (TextView) mView.findViewById(R.id.userStatustext);
            userMessage.setText(message);

            if(!isSeen){
                userMessage.setTypeface(userMessage.getTypeface(), Typeface.BOLD);
                userMessage.getResources().getColor(R.color.colorblue);
            }else{
                userMessage.setTypeface(userMessage.getTypeface(),Typeface.NORMAL);
            }
        }
        public void setName(String name){
            TextView userName = (TextView) mView.findViewById(R.id.userNametext);
            userName.setText(name);
        }
        public void setUserImage (final String Image, final Context cntxt){
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
            ImageView setOnline = (ImageView) mView.findViewById(R.id.userOnlineIcon);
            if (online_stat.equals("true")){
                setOnline.setVisibility(View.VISIBLE);
            }else {
                setOnline.setVisibility(View.INVISIBLE);
            }

        }

    }
}

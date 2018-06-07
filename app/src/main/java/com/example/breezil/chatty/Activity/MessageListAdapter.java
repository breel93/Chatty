package com.example.breezil.chatty.Activity;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Messages> messagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDataBase;


    public MessageListAdapter(List<Messages> messagesList){

        this.messagesList = messagesList;
    }
    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();

        String current_user_id = mAuth.getCurrentUser().getUid();


        Messages c = messagesList.get(position);


        String from_user = c.getFrom();
        String message_type = c.getType();

        if (from_user != null){

            if (from_user.equals(current_user_id) ) {
                //if sent by the current user
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                //message received from a friend
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

        return position;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent,parent,false);
            return new SentMessageHolder(view);
        }else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_recieved,parent,false);
            return new ReceivedMessageHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messages message = messagesList.get(position);
        switch (holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder)holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder)holder).bind(message);

        }


    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView imageMessageBody;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            imageMessageBody = (ImageView) itemView.findViewById(R.id.text_body_image);
        }
        void bind(Messages message){
            // messageText.setText(message.getMessage());
            String message_type = message.getType();

            if(message_type.equals("text")){
                messageText.setText(message.getMessage());
                imageMessageBody.setVisibility(View.INVISIBLE);
            }else {
                messageText.setVisibility(View.INVISIBLE);
                imageMessageBody.setMaxHeight(200);
                imageMessageBody.setMaxWidth(150);


                Picasso.with(imageMessageBody.getContext()).load(message.getMessage())
                        .placeholder(R.drawable.default_avatar).into(imageMessageBody);
            }

        }
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText, nameText;
        CircleImageView profImage;
        ImageView imageMessageBody;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.message_username);
            profImage = (CircleImageView) itemView.findViewById(R.id.message_profile_Image);
            imageMessageBody = (ImageView) itemView.findViewById(R.id.text_body_image);
        }
        void bind(Messages message){
            //messageText.setText(message.getMessage()).;

            String from_user = message.getFrom();

            mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

            mUserDataBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    nameText.setText(name);
                    Picasso.with(profImage.getContext()).load(image)
                            .placeholder(R.drawable.default_avatar).into(profImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            String message_type = message.getType();

            if(message_type.equals("text")){
                messageText.setText(message.getMessage());
                imageMessageBody.setVisibility(View.INVISIBLE);
            }else {
                messageText.setVisibility(View.INVISIBLE);
                imageMessageBody.setVisibility(View.VISIBLE);
                imageMessageBody.setMaxHeight(40);
                imageMessageBody.setMaxWidth(40);

                Picasso.with(imageMessageBody.getContext()).load(message.getMessage())
                        .placeholder(R.drawable.default_avatar).into(imageMessageBody);
            }
        }

    }
}

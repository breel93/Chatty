package com.example.breezil.chatty.Activity.adapters;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.icu.text.TimeZoneFormat;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breezil.chatty.Activity.model.Messages;
import com.example.breezil.chatty.Activity.ui.Single_MessageImage_Activity;
import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ConstraintLayout constraintLayout;

    private List<Messages> messagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDataBase;
    private Context context;

    String MessageId;


    public MessageListAdapter(List<Messages> messagesList,Context context){

        this.messagesList = messagesList;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();

        String current_user_id = mAuth.getCurrentUser().getUid();


        Messages c = messagesList.get(position);

        constraintLayout = new ConstraintLayout(context);



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

        MessageId =messagesList.get(position).MessageId;

        switch (holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder)holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder)holder).bind(message);
                break;

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
            final String fromWho = message.getFrom();


            if(message_type.equals("text")){
                messageText.setText(message.getMessage());
                imageMessageBody.setVisibility(View.INVISIBLE);
                try{
                    long millisecond = message.getTime();
                    String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
                    timeText.setText(dateString);

                }catch (Exception e){
                    Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else {
                messageText.setVisibility(View.INVISIBLE);
                imageMessageBody.setVisibility(View.VISIBLE);
                imageMessageBody.setMaxHeight(300);
                imageMessageBody.setMaxWidth(300);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.text_message_time,ConstraintSet.LEFT,R.id.text_body_image,ConstraintSet.RIGHT,5);
                constraintSet.connect(R.id.text_message_time,ConstraintSet.BOTTOM,R.id.text_body_image,ConstraintSet.TOP,5);
                constraintSet.applyTo(constraintLayout);


                Picasso.with(imageMessageBody.getContext()).load(message.getMessage())
                        .placeholder(R.drawable.default_avatar).into(imageMessageBody);

                try{
                    long millisecond = message.getTime();
                    String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
                    timeText.setText(dateString);

                }catch (Exception e){
                    Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                imageMessageBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent imgTransition = new Intent(context, Single_MessageImage_Activity.class);
//                        imgTransition.putExtra("message_id",MessageId);
//                        imgTransition.putExtra("from_id",fromWho);
//
//                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
//                                .makeSceneTransitionAnimation((Activity) context,
//                                        imageMessageBody,
//                                        ViewCompat.getTransitionName(imageMessageBody));
//                        context.startActivity(imgTransition,optionsCompat.toBundle());
                    }
                });
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

                try{
                    long millisecond = message.getTime();
                    String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
                    timeText.setText(dateString);

                }catch (Exception e){
                    Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }else {
                messageText.setVisibility(View.INVISIBLE);
                imageMessageBody.setVisibility(View.VISIBLE);
                imageMessageBody.setMaxHeight(300);
                imageMessageBody.setMaxWidth(300);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.text_message_time,ConstraintSet.RIGHT,R.id.text_body_image,ConstraintSet.RIGHT,5);
                constraintSet.connect(R.id.text_message_time,ConstraintSet.BOTTOM,R.id.text_body_image,ConstraintSet.TOP,5);
                constraintSet.applyTo(constraintLayout);


                try{
                    long millisecond = message.getTime();
                    String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
                    timeText.setText(dateString);

                }catch (Exception e){
                    Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Picasso.with(imageMessageBody.getContext()).load(message.getMessage())
                        .placeholder(R.drawable.default_avatar).into(imageMessageBody);

            }
        }

//        public void setTime(String date){
//            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
//            timeText.setText(d);
//        }

    }
}

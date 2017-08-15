package com.example.breezil.chatty.Activity;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by breezil on 8/11/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> messagesList;
    private FirebaseAuth mAuth;

    private DatabaseReference mUserDataBase;



    public MessageAdapter(List<Messages> messagesList){

        this.messagesList = messagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);

        return new MessageViewHolder(v);
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profImage;
        public TextView displayName;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.messageSingleText);
            profImage = (CircleImageView) itemView.findViewById(R.id.messageSingleProfileImg);
            displayName = (TextView) itemView.findViewById(R.id.chatDisplayNametxt);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();

        String current_user_id = mAuth.getCurrentUser().getUid();


        Messages c = messagesList.get(position);


        String from_user = c.getFrom();

        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.displayName.setText(name);
                Picasso.with(holder.profImage.getContext()).load(image)
                        .placeholder(R.drawable.default_avatar).into(holder.profImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        holder.messageText.setText(c.getMessage());
        //holder.profImage.setImageResource(c.ge);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}

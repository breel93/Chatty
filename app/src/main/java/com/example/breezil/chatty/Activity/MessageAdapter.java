package com.example.breezil.chatty.Activity;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by breezil on 8/11/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> messagesList;
    private FirebaseAuth mAuth;



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

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.messageSingleText);
            profImage = (CircleImageView) itemView.findViewById(R.id.messageSingleProfileImg);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();

        String current_user_id = mAuth.getCurrentUser().getUid();


        Messages c = messagesList.get(position);


        String from_user = c.getFrom();

//        if(from_user.equals(current_user_id)){
//            holder.messageText.setBackgroundColor(Color.WHITE);
//            holder.messageText.setTextColor(Color.BLUE);
//        }else {
//            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
//            holder.messageText.setTextColor(Color.WHITE);
//        }



        holder.messageText.setText(c.getMessage());
        //holder.profImage.setImageResource(c.ge);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}

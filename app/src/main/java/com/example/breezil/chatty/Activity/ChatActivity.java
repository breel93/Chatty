package com.example.breezil.chatty.Activity;

import android.content.Context;
import android.icu.text.DateFormat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mChatToolbar;


    private String chatUser;
    private String chatUserName;
    private TextView mUserDisplayName;
    private TextView mLastSeen;
    private CircleImageView mUserImage;

    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;

    private String currentUserId;

    //Chat Fields
    private ImageButton chatAddBtn;
    private ImageButton chatsendBtn;
    private EditText chatMessageText;


    //message List recyclerView

     private RecyclerView messagesList;


    private final List<Messages> messList = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;

    private LinearLayoutManager mLayout;

    private MessageAdapter messageAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private  int mCurrentPage = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatUser = getIntent().getStringExtra("user_id");
        chatUserName = getIntent().getStringExtra("user_name");

        mChatToolbar = (Toolbar) findViewById(R.id.userChatbar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        //firebase
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();


        //Chat fields
        chatAddBtn = (ImageButton) findViewById(R.id.userchatAddBtn);
        chatsendBtn = (ImageButton) findViewById(R.id.userChatsend);
        chatMessageText = (EditText) findViewById(R.id.userChatText);



        messageAdapter = new MessageAdapter(messList);

        //recycler List
        messagesList = (RecyclerView) findViewById(R.id.messageList);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeMessage);

        mLayout =new LinearLayoutManager(this);

        messagesList.setHasFixedSize(true);
        messagesList.setLayoutManager(mLayout);

        messagesList.setAdapter(messageAdapter);
        
        //method to load the message
        loadMessages();





        getSupportActionBar().setTitle(chatUserName);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView = layoutInflater.inflate(R.layout.chatcustombar,null);
        actionBar.setCustomView(actionbarView);



        /* Custom Action bar item */

        mUserImage = (CircleImageView) findViewById(R.id.custombarimage);
        mUserDisplayName = (TextView) findViewById(R.id.chatUserDisplayname);
        mLastSeen = (TextView) findViewById(R.id.lastSeen);

        mUserDisplayName.setText(chatUserName);

        mRootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                if(online.equals("true")){
                   mLastSeen.setText("Online");
                }else {
                    TImeSeen tImeSeen = new TImeSeen();

                    long lastSeen = Long.parseLong(online);

                    String lastSeenTime = tImeSeen.TimeSeen(lastSeen,getApplicationContext());

                    mLastSeen.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //firebase ref for a new chat child node
        mRootRef.child("Chat").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(chatUser)){
                    Map chatMap = new HashMap();
                    chatMap.put("seen",false);
                    chatMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + currentUserId + "/" + chatUser, chatMap);
                    chatUserMap.put("Chat/" + chatUser + "/" + currentUserId,chatMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                           if(databaseError != null){
                               Log.d("Chat_log ",databaseError.getMessage().toString());

                           }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        chatsendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                messList.clear();
                loadMessages();
            }
        });



    }


    //loadMessages Method
    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(currentUserId).child(chatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages mess = dataSnapshot.getValue(Messages.class);
                messList.add(mess);
                messageAdapter.notifyDataSetChanged();

                messagesList.scrollToPosition(messList.size()-1);

                refreshLayout.setRefreshing(false);
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


    }


    //send message method
    private void sendMessage() {
        String message = chatMessageText.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String currentUserRef = "messages/" + currentUserId + "/" + chatUser;
            String chatUserRef = "messages/" + chatUser + "/" + currentUserId;


            //Firebase ref
            DatabaseReference userMessagePush = mRootRef.child("messages")
                    .child(currentUserId)
                    .child(chatUser).push();

            String pushId = userMessagePush.getKey();



            Map messageMap = new HashMap();
            messageMap.put( "message", message);
            messageMap.put( "seen",false);
            messageMap.put( "type","text");
            messageMap.put( "time",ServerValue.TIMESTAMP);
            messageMap.put("from", currentUserId);


            Map userMessagemap = new HashMap();
            userMessagemap.put(currentUserRef + "/" + pushId, messageMap );
            userMessagemap.put(chatUserRef + "/" + pushId, messageMap);

            chatMessageText.setText("");

            mRootRef.updateChildren(userMessagemap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("Chat_log ",databaseError.getMessage().toString());

                    }
                }
            });

        }
    }
}

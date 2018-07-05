package com.example.breezil.chatty.Activity.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breezil.chatty.Activity.adapters.MessageListAdapter;
import com.example.breezil.chatty.Activity.model.Messages;
import com.example.breezil.chatty.Activity.utils.TImeSeen;
import com.example.breezil.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

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

    //Arraylist object holding the message model obj.
    private final List<Messages> messList = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;

    private LinearLayoutManager mLayout;

    private ProgressDialog mProgress;

    //variable of the Message adapter class

    private MessageListAdapter messageListAdapter;

    //the number of message to load on the first instance => TOTAL_ITEMS_TO_LOAD
    private static final int TOTAL_ITEMS_TO_LOAD = 10;


    private  int mCurrentPage = 1;

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private StorageReference mImageStorage;

    BottomSheetDialog mBottomSheet;



    //sol
    private int itemPos = 0;

    //lastkey
    private String mLastkey = "";
    private String mPrevKey ="";
    long mRevTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //a string intent pass from the friends activity
        chatUser = getIntent().getStringExtra("user_id");

        //user name string passed from intent of the friend activity
        chatUserName = getIntent().getStringExtra("user_name");

        mChatToolbar = (Toolbar) findViewById(R.id.userChatbar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        //firebase reference to the root
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        //get current users id as object
        currentUserId = mAuth.getCurrentUser().getUid();


        mRevTime = -1 * new Date().getTime();


        //Chat fields
        chatAddBtn = (ImageButton) findViewById(R.id.userchatAddBtn);
        chatsendBtn = (ImageButton) findViewById(R.id.userChatsend);
        chatMessageText = (EditText) findViewById(R.id.userChatText);


       //declear the message adapter object as a variable that takes an arraylist obj(messlist) arg
        //messageAdapter = new MessageAdapter(messList);
        messageListAdapter = new MessageListAdapter(messList);

        //recycler List
        messagesList = (RecyclerView) findViewById(R.id.messageList);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeMessage);

        mLayout =new LinearLayoutManager(this);

        messagesList.setHasFixedSize(true);
        messagesList.setLayoutManager(mLayout);
        messagesList.setItemAnimator(new DefaultItemAnimator());


        //messagesList.setAdapter(messageAdapter);
        messagesList.setAdapter(messageListAdapter);






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

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ChatActivity.this,UserProfile.class);
                profileIntent.putExtra("user_id",chatUser);
                startActivity(profileIntent);
            }
        });



//
        mRootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                final String image = dataSnapshot.child("thumb_image").getValue().toString();
                if(online.equals("true")){
                   mLastSeen.setText("Online");
                }else {
                    TImeSeen tImeSeen = new TImeSeen();

                    long lastSeen = Long.parseLong(online);

                    String lastSeenTime = tImeSeen.TimeSeen(lastSeen,getApplicationContext());

                    mLastSeen.setText(lastSeenTime);
                }

                Picasso.with(ChatActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(mUserImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mUserImage);
                    }
                });
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

        chatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttachmentOptions();

            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                //messList.clear();
                itemPos = 0;
                loadMoreMessages();
            }
        });

    }

    void showAttachmentOptions(){
        mBottomSheet = new BottomSheetDialog(this);
        View bottomSheetView = this.getLayoutInflater().inflate(R.layout.buttom_sheet,null);

        mBottomSheet.setContentView(bottomSheetView);
        mBottomSheet.show();



        LinearLayout selectGalary = (LinearLayout) bottomSheetView.findViewById(R.id.select_gallery);
        LinearLayout selectCamera = (LinearLayout) bottomSheetView.findViewById(R.id.select_camera);
        LinearLayout selectDocument = (LinearLayout) bottomSheetView.findViewById(R.id.select_document);
        selectGalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                if(galleryIntent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(Intent.createChooser(galleryIntent,"Choose Image"),GALLERY_REQUEST_CODE);

                }

            }
        });

        selectCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(cameraIntent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBottomSheet.dismiss();
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){

            mProgress.setMessage("sending");
            mProgress.show();

            Uri imageUri = data.getData();





            final String current_user = "messages/" + currentUserId + "/" + chatUser;
            final String chat_user = "messages/" + chatUser + "/" + currentUserId;

            DatabaseReference user_messages_push = mRootRef.child("messages")
                    .child(currentUserId).child(chatUser).push();



            final String push_id = user_messages_push.getKey();

            StorageReference filepath = mImageStorage.child("message_images")
                    .child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        //noinspection VisibleForTests
                        final String download_url = task.getResult().getDownloadUrl().toString();





                        Map messageMap = new HashMap();
                        messageMap.put("message",download_url);
                        messageMap.put("seen",false);
                        messageMap.put("type","image");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from",currentUserId);
//                        messageMap.put("Reverse_Time",mRevTime);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user + "/" + push_id, messageMap);


                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null){

                                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                                        mProgress.dismiss();
                                    }

                                    mProgress.dismiss();
                                }
                        });

                    }
                }
            });


        }else if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap cameraBitmap = (Bitmap) extras.get("data");
            mProgress.setMessage("sending");
            mProgress.show();

            Uri cameraUri = data.getData();



            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            cameraBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] camera_byte = byteArrayOutputStream.toByteArray();

            final String current_user = "messages/" + currentUserId + "/" + chatUser;
            final String chat_user = "messages/" + chatUser + "/" + currentUserId;

            DatabaseReference user_messages_push = mRootRef.child("messages")
                    .child(currentUserId).child(chatUser).push();



            final String push_id = user_messages_push.getKey();

            StorageReference storagePath = mImageStorage.child("message_images")
                    .child(push_id + ".jpg");

            UploadTask uploadTask = storagePath.putBytes(camera_byte);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    String camera_url = task.getResult().getDownloadUrl().toString();
                    if(task.isSuccessful()){
                        Map messageMap = new HashMap();
                        messageMap.put("message",camera_url);
                        messageMap.put("seen",false);
                        messageMap.put("type","image");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from",currentUserId);


                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user + "/" + push_id, messageMap);


                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){

                                    Log.d("CHAT_LOG",databaseError.getMessage().toString());
                                    mProgress.dismiss();
                                }

                                mProgress.dismiss();
                            }
                        });
                    }
                }
            });

        }
    }

    //loadMessages Method
    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(currentUserId).child(chatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages mess = dataSnapshot.getValue(Messages.class);


                itemPos++;
                if(itemPos == 1){
                    String messagekey = dataSnapshot.getKey();
                    mLastkey = messagekey;
                    mPrevKey = messagekey;
                }

                messList.add(mess);
//                messageAdapter.notifyDataSetChanged();
                messageListAdapter.notifyDataSetChanged();

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

    private void loadMoreMessages() {
        DatabaseReference messageRef = mRootRef.child("messages").child(currentUserId).child(chatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastkey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages mess = dataSnapshot.getValue(Messages.class);

                String messagekey = dataSnapshot.getKey();



                if(!mPrevKey.equals(messagekey)){
                    messList.add(itemPos++, mess);
                }else {
                    mPrevKey = mLastkey;
                }

                if(itemPos == 1){

                    mLastkey = messagekey;
                }


//                messageAdapter.notifyDataSetChanged();
                messageListAdapter.notifyDataSetChanged();

                refreshLayout.setRefreshing(false);

                mLayout.scrollToPositionWithOffset(10,0);
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
            messageMap.put("Reverse_Time",mRevTime);


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

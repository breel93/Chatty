package com.example.breezil.chatty.Activity.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breezil.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    //fields
    private ImageView mUserProfileImage;
    private TextView mUserProfileName,mUserProfileStatus;//mUserProfileFriends
    private Button mUserProfilesendRequest, mUserProfiledeclineRequest;

    private ProgressDialog mProgress;

    private String current_state;



   //toolbar
   // private Toolbar mToolbar;

    //Firebase
    private DatabaseReference mUserDataref;
    private DatabaseReference mFriendsReqDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mMessageDb;

    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //get intent from the previous ,i.e the click item id as it is in database.
        final String user_id = getIntent().getStringExtra("user_id");



        //Firebase to pupulate users view for user profile
        mUserDataref = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mUserDataref.keepSynced(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        //firebase for friends request i.e set a child not to create friends reaquest
        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request");
       //firebase friends node
       mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        //Firebase notification
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");

        mMessageDb = FirebaseDatabase.getInstance().getReference().child("messages");



        //firebase current users instance
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        //fields
        mUserProfileImage = (ImageView) findViewById(R.id.userProfileImage);
        mUserProfileName = (TextView) findViewById(R.id.userProfileDisplayname);
        mUserProfileStatus = (TextView) findViewById(R.id.userprofilestatus);
        //mUserProfileFriends = (TextView) findViewById(R.id.userprofilefriends);


        mUserProfilesendRequest = (Button) findViewById(R.id.userprofilesendrequest);
        mUserProfiledeclineRequest = (Button) findViewById(R.id.declinefriendrequestbtn);

        //when current state is not friends
        current_state = "not_friend";

        //decline btn
        mUserProfiledeclineRequest.setVisibility(View.INVISIBLE);
        mUserProfiledeclineRequest.setEnabled(false);

        //progress dialog
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("please wait loading users data");
        mProgress.setCanceledOnTouchOutside(true);
        mProgress.show();



        //addValue event listener is one of the three firebase method used to retrieve data
        mUserDataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get each of the item and store them in a string
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String Image = dataSnapshot.child("image").getValue().toString();

                //set the string to the field
               mUserProfileName.setText(display_name);
               mUserProfileStatus.setText(status);

                Picasso.with(UserProfile.this).load(Image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(mUserProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(UserProfile.this).load(Image).placeholder(R.drawable.default_avatar).into(mUserProfileImage);
                    }
                });

                mUserProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewImageIntent = new Intent(UserProfile.this,ShowImageFull.class);
                        viewImageIntent.putExtra("user_id",user_id);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(UserProfile.this,
                                        mUserProfileImage,
                                        ViewCompat.getTransitionName(mUserProfileImage));
                        startActivity(viewImageIntent,options.toBundle());
                    }
                });


                //------------ FRIENDS LIST / REQUEST FEATURE FIREBASE-------
                mFriendsReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       //if the snapshot of the page view has the userid get thr request type
                       if(dataSnapshot.hasChild(user_id)){
                           String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                          //if it says received then set Button to accept request
                           if(req_type.equals("received")){
                               current_state = "req_received";
                               mUserProfilesendRequest.setText("Accept Friend Request");

                               mUserProfiledeclineRequest.setVisibility(View.VISIBLE);
                               mUserProfiledeclineRequest.setEnabled(true);
                           }else if(req_type.equals("sent") ){
                               current_state = "req_sent";
                               mUserProfilesendRequest.setText("Cancel Request");

                               mUserProfiledeclineRequest.setVisibility(View.INVISIBLE);
                               mUserProfiledeclineRequest.setEnabled(false);

                           }
                           mProgress.dismiss();
                           //if friends already, if user profile clicked already exist in the firebase friends node
                       }else {

                           mFriendsDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                 if(dataSnapshot.hasChild(user_id)){
                                     current_state = "Friends";
                                     mUserProfilesendRequest.setText("UnFriend");

                                     mUserProfiledeclineRequest.setVisibility(View.INVISIBLE);
                                     mUserProfiledeclineRequest.setEnabled(false);
                                 }
                                   mProgress.dismiss();
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   mProgress.dismiss();
                               }
                           });

                       }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //event handled when user send request
        mUserProfilesendRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mUserProfilesendRequest.setEnabled(false);

                //------ NOT FRIENDS
                //if state is not friend
                if(current_state.equals("not_friend")){

                    DatabaseReference newNotificationRef = mRootRef.child("Notifications").child(user_id).push();
                    String newNotificationID = newNotificationRef.getKey();


                    HashMap<String, String> notiData = new HashMap<>();
                    notiData.put("from",mCurrentUser.getUid());
                    notiData.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_request/" + mCurrentUser.getUid() + "/" + user_id + "/request_type","sent");
                    requestMap.put("Friend_request/" + user_id + "/" + mCurrentUser.getUid() + "/request_type","received");
                    requestMap.put("Notifications/" + user_id + "/" + newNotificationID,notiData );


                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                           if(databaseError != null){

                               Toast.makeText(UserProfile.this,"There was some error",Toast.LENGTH_LONG).show();
                           }else {
                               mUserProfilesendRequest.setEnabled(true);
                               current_state = "req_sent";
                               mUserProfilesendRequest.setText("Cancel Friend Request");
                               Toast.makeText(UserProfile.this,"Request sent",Toast.LENGTH_LONG).show();
                           }



                        }
                    });
                }
                //-----cancel friends request
                if(current_state.equals("req_sent")){
                    mFriendsReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendsReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mUserProfilesendRequest.setEnabled(true);
                                            current_state = "not_friend";
                                            mUserProfilesendRequest.setText("Send Friend Request");

                                            mUserProfiledeclineRequest.setVisibility(View.INVISIBLE);
                                            mUserProfiledeclineRequest.setEnabled(false);

                                        }
                                    }
                                });
                            }
                        }
                    });

                }

                //---- REQUEST RECEIVED STATE

                if(current_state.equals("req_received")){
                    final String fDate = DateFormat.getDateTimeInstance().format(new Date());
                   mFriendsDatabase.child(mCurrentUser.getUid()).child(user_id).child("date").setValue(fDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             mFriendsDatabase.child(user_id).child(mCurrentUser.getUid()).child("date").setValue(fDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                    mFriendsReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendsReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                     mUserProfilesendRequest.setEnabled(true);
                                                     current_state = "Friends";
                                                    mUserProfilesendRequest.setText("UnFriend");


                                                    mUserProfiledeclineRequest.setVisibility(View.INVISIBLE);
                                                    mUserProfiledeclineRequest.setEnabled(false);

                                                }
                                            });
                                        }
                                    });
                                 }
                             });
                         }
                       }
                   });

                }
                //--------To Unfriend User
                if(current_state.equals("Friends")){
                    mFriendsDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendsDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            mMessageDb.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        mMessageDb.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    mUserProfilesendRequest.setEnabled(true);
                                                                    current_state = "not_friend";
                                                                    mUserProfilesendRequest.setText("Send Friend Request");

                                                                }
                                                            }
                                                        });
                                                    }

                                                }
                                            });




                                        }
                                    }
                                });
                            }
                        }
                    });
                }



            }
        });

        //-------------decline request
        mUserProfiledeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_state.equals("req_received")){
                    Map declineFriendMap = new HashMap();
                    declineFriendMap.put("Friend_request/"+ mCurrentUser.getUid() + "/" + user_id,null);
                    declineFriendMap.put("Friend_request/"+ user_id + "/" + mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(declineFriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            mUserProfiledeclineRequest.setVisibility(View.INVISIBLE);
                            mUserProfiledeclineRequest.setEnabled(false);
                            if(databaseError == null){
                                current_state = "not friends";
                                mUserProfilesendRequest.setText("Send Request");
                            }
                            mUserProfilesendRequest.setEnabled(true);
                        }
                    });
                }
            }
        });

    }
}

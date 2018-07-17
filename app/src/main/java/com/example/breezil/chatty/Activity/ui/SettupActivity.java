package com.example.breezil.chatty.Activity.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breezil.chatty.Activity.utils.BottomNavigationHelper;
import com.example.breezil.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettupActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseref;
    private FirebaseUser mCurrentUser;
    //Layout
    private TextView userName, userStatus;
    private CircleImageView profImage;
    private Button editStatusbtn;
    private ImageView mChangeImageIcon;

    private static final int GALLERY_REQUEST = 1;

    private ProgressDialog mProgress;


    //Firebase
    private StorageReference mProfImageStorage;
    private DatabaseReference mUserRef;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settup);

        //firebase storage
        mProfImageStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        userName = (TextView) findViewById(R.id.settingDisplayName);
        userStatus = (TextView) findViewById(R.id.settingHeyMessge);
        profImage = (CircleImageView) findViewById(R.id.settingImage);

        mChangeImageIcon = (ImageView) findViewById(R.id.changeImageIcon);

        editStatusbtn = (Button) findViewById(R.id.settingEditStatus);
        //changeDisplayImagebtn = (Button) findViewById(R.id.settingsChangeProfImage);


        mProgress = new ProgressDialog(this);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        @SuppressWarnings("ConstantConditions") final String Uid = mCurrentUser.getUid();
        mDatabaseref = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        mDatabaseref.keepSynced(true);



        //Firebase database method to retrieve data from the database
        mDatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(SettupActivity.this,dataSnapshot.toString(),Toast.LENGTH_LONG).show();
                //Retrieve all data element from each fields of the snapshot of users
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                //set datasnapshot to each fields
                userName.setText(name);
                userStatus.setText(status);
                //Picaso ref to set image
                //also if user does not have image set the default image
                if(!image.equals("default")){
                   // Picasso.with(SettupActivity.this).load(image).placeholder(R.drawable.default_avatar).into(profImage);
                    Picasso.with(SettupActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(profImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(SettupActivity.this).load(image).placeholder(R.drawable.default_avatar).into(profImage);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Intent to change status activity
        editStatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = userStatus.getText().toString().trim();
                String user_name = userName.getText().toString().trim();


                Bundle args = new Bundle();
                args.putString("statusValue", status_value);
                args.putString("user_name", user_name);

                StatusDialog dialog = new StatusDialog();
                dialog.setArguments(args);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(),"Dialog");



            }
        });

        //Intent to Choose profile image pic
        mChangeImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent to select display Image
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_REQUEST);

                // start picker to get image for cropping and then use the image in cropping activity
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettupActivity.this);

            }
        });

        profImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgTransition = new Intent(SettupActivity.this,ShowImageFull.class);

                imgTransition.putExtra("user_id",Uid);

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SettupActivity.this,
                                profImage,
                                ViewCompat.getTransitionName(profImage));

                startActivity(imgTransition,options.toBundle());
            }
        });



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.chat:

                        startActivity(new Intent(SettupActivity.this,MainActivity.class));
                        break;
                    case R.id.search:
                        startActivity(new Intent(SettupActivity.this,AllUsersActivity.class));
                        break;
                    case R.id.user:

                        break;
                    case R.id.settings:
                        startActivity(new Intent(SettupActivity.this,SettingsActivity.class));
                        break;


                }



                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserRef.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCurrentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    //tricky part Activity result from the gallery result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check if request code is same as intent and if okay start crop activity
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

            //Toast.makeText(SettupActivity.this,imageUri,Toast.LENGTH_LONG).show();
        }

        //copied from Aurthur Edmondo github for crop action
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgress.setTitle("Uploading Image");
                mProgress.setMessage("please wait...");
                mProgress.show();
                mProgress.setCanceledOnTouchOutside(false);

                //Store the new cropped image in a uri variable
                Uri resultUri = result.getUri();
                //set a file path
                File thumb_filepath = new File(resultUri.getPath());

                //get current user id to set image in the firebase storage
                String current_user_id = mCurrentUser.getUid();

                    //we get bitmap refernce from our file path
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //from firebase doc upload bitmap method
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();






                //path to store in firebase storage
                StorageReference filePath = mProfImageStorage.child("profileImages").child(current_user_id + ".jpg");
                //storage reference for the firebase storage

                final StorageReference thumb_path = mProfImageStorage.child("profileImages").child("thumbs").child(current_user_id+".jpg");
                //put the image in the file and add on completelistener
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                           //if the storage is successful get the download path of the result
                            //so as to set in the firebase reference
                           final String download_url = task.getResult().getDownloadUrl().toString();

                           //from firebase doc uploadtask
                            UploadTask uploadTask = thumb_path.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                  String thumb_downloadurl = thumb_task.getResult().getDownloadUrl().toString();
                                   if(thumb_task.isSuccessful()) {

                                       Map update_hashmap = new HashMap();
                                       update_hashmap.put("image",download_url);
                                       update_hashmap.put("thumb_image",thumb_downloadurl);

                                       //set url in the image child of the firebase database and also add on complete listener
                                       //to show successfully uploaded and set...
                                       mDatabaseref.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   mProgress.dismiss();
                                                   Toast.makeText(SettupActivity.this, "Successful", Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       });
                                   }else {
                                       Toast.makeText(SettupActivity.this, "Error in Uploading thumbnails", Toast.LENGTH_LONG).show();
                                       mProgress.dismiss();
                                   }
                                }
                            });



                            //Toast.makeText(SettupActivity.this, "working", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(SettupActivity.this, "Error in Uploading", Toast.LENGTH_LONG).show();
                           mProgress.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }
}

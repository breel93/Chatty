package com.example.breezil.chatty.Activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.breezil.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText regEmailtext,regPassword,regUsername;
    private Button regRegBtn;
    private ProgressDialog progDialog;


    //Firebase!!!
    private FirebaseAuth mAuth;
    private DatabaseReference mdataref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //firebase Auth Instance
        mAuth = FirebaseAuth.getInstance();




        //get text for each field
        regEmailtext = (EditText) findViewById(R.id.regEmailtext);
        regPassword = (EditText)findViewById(R.id.regPasswordtext);
        regUsername = (EditText) findViewById(R.id.regUsernametext);
        regRegBtn = (Button) findViewById(R.id.regRegBtn);
        progDialog = new ProgressDialog(this);

        //create user account method
        regRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get each field string
                String emailtext = regEmailtext.getText().toString().trim();
                String passwdtext = regPassword.getText().toString().trim();
                String userNametext = regUsername.getText().toString().trim();
                //check if fields are empty
                registerUser(emailtext,passwdtext,userNametext);


            }
        });
    }

    private void registerUser(String emailtext, String passwdtext, final String userNametext) {
        /*
        * TextUtils checks if require fields are empty or not
        * and then toast error message if empty
        * if its not empty we call the firebase user creation method with email and password
         */
        if(!TextUtils.isEmpty(emailtext) || !TextUtils.isEmpty(passwdtext)|| !TextUtils.isEmpty(userNametext)){
            //call the firebase account create
            progDialog.setMessage("Creating Account...");
            progDialog.setTitle("Please wait");
            progDialog.setCanceledOnTouchOutside(false);
            progDialog.show();
            /*
            * here createuser with email and password is called
             */
            mAuth.createUserWithEmailAndPassword(emailtext,passwdtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //if user is successfully created, we create a user database
                    //that stores other informations of the user like username , profile image , status
                    if(task.isSuccessful()){

                        //get current user inorder to use it reference storing the user informations
                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        String Uid = current_user.getUid();

                        //firebase Token, this an instance of device token of a user so as to
                        //save its last session for firebase, if app is closed
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        //set database reference for the current user using the Uid of the current user
                        mdataref = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);


                        //here we create a data structure hashmap to save the key and value as string
                        //in firebase database
                        HashMap<String,String> userMap = new HashMap<>();
                        userMap.put("name", userNametext );
                        userMap.put("status","Hey there...Im using chatty");
                        userMap.put("thumb_image","default");
                        userMap.put("image","default Image");
                        userMap.put("deviceToken",deviceToken);

                        //here call the set value function to save the data structure in database
                        // attach oncomplete listener if successful call intent to the mainActivity
                        // and clear previous flags so it cant go back.
                        mdataref.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progDialog.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        });



                    }else{
                        progDialog.dismiss();
                        //user account creation error
                        Toast.makeText(RegisterActivity.this,"Registration Error pls try again..",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            Toast.makeText(RegisterActivity.this,"Please fill the fields..",Toast.LENGTH_LONG).show();
        }
    }
}

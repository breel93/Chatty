package com.example.breezil.chatty.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.breezil.chatty.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class OtherSettup extends AppCompatActivity {

    private EditText mOtherDisplayname;
    private Button mSavebtn;
    private ProgressBar mOtherProgress;

    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_settup);


        String Uid = getIntent().getStringExtra("user_id");
        //firebase Token, this an instance of device token of a user so as to
        //save its last session for firebase, if app is closed
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);


        mOtherDisplayname = (EditText) findViewById(R.id.otherUserNametext);
        mSavebtn = (Button) findViewById(R.id.otherSavebtn);
        mOtherProgress = (ProgressBar) findViewById(R.id.saveOtherProgress);



        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mOtherDisplayname.getText().toString();

                saveText(userName);
            }
        });




    }

    private void saveText(String userName) {
        if(!TextUtils.isEmpty(userName)){
            mOtherProgress.setVisibility(View.VISIBLE);
            mSavebtn.setVisibility(View.INVISIBLE);
        }else {
            Toast.makeText(this ,"Please Enter Display Name",Toast.LENGTH_LONG).show();
        }
    }
}

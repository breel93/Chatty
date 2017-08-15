package com.example.breezil.chatty.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breezil.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    //Textfields
    private EditText logEmailField,logPasswordField;
    private Button logLoginBtn;
    private TextView gotoSignUp;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUsertokenref;


    private Toolbar mToolBar;

    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //firebaseInstance
        mAuth = FirebaseAuth.getInstance();
        mUsertokenref = FirebaseDatabase.getInstance().getReference().child("Users");

        //toolbar
        mToolBar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);

        //InputFields
        logEmailField = (EditText) findViewById(R.id.loginEmailtext);
        logPasswordField = (EditText) findViewById(R.id.loginPasswordtext);
        logLoginBtn = (Button) findViewById(R.id.loginLoginBtn);
        gotoSignUp = (TextView) findViewById(R.id.gotoSignuptext);

        //go and create new account
        gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        logLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = logEmailField.getText().toString().trim();
                String passwordtext = logPasswordField.getText().toString().trim();
                login(emailText,passwordtext);
            }
        });





    }

    //Login method with email and password
    private void login(String emailText, String passwordtext) {
        if(!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordtext)){
            mProgress.setTitle("Please wait");
            mProgress.setMessage("Signing in now");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();
            //Toast.makeText(this,"you can login", Toast.LENGTH_LONG).show();
            //with the auth instance call the firebase login function
            mAuth.signInWithEmailAndPassword(emailText,passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mProgress.dismiss();

                        //token id for notifications
                        String current_user = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                        mUsertokenref.child(current_user).child("deviceToken").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){
                                   Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                                   // mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   startActivity(mainIntent);
                                   finish();
                               }
                            }
                        });




                    }else{
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this ,"Login Error Please try again",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}

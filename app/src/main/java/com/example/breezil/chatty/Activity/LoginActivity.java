package com.example.breezil.chatty.Activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;

import com.example.breezil.chatty.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    RelativeLayout mBackground;
    AnimationDrawable animationDrawable;
    //Textfields
    private EditText logEmailField,logPasswordField;
    private Button logLoginBtn;
    private TextView gotoSignUp;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUsertokenref;

    private SignInButton mGoogleBtn;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;

    private static final String TAG = "LoginActivity";

    private FirebaseUser currentUser;

    private DatabaseReference mUserRef;






    private ProgressBar mLoginProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBackground = (RelativeLayout) findViewById(R.id.myBackground);
        animationDrawable = (AnimationDrawable) mBackground.getBackground();
        animationDrawable.setEnterFadeDuration(4500);
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();

        //firebaseInstance.
        mAuth = FirebaseAuth.getInstance();
        mUsertokenref = FirebaseDatabase.getInstance().getReference().child("Users");

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //toolbar



        //InputFields
        logEmailField = (EditText) findViewById(R.id.loginEmailtext);
        logPasswordField = (EditText) findViewById(R.id.loginPasswordtext);
        logLoginBtn = (Button) findViewById(R.id.loginLoginBtn);
        gotoSignUp = (TextView) findViewById(R.id.gotoSignuptext);
        mLoginProgress = (ProgressBar) findViewById(R.id.LoginProgress);
        mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);


        currentUser = mAuth.getCurrentUser();




        //go and create new account
        gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSignUpIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                gotoSignUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(gotoSignUpIntent);
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

        //----Google sign in ------//
        //configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //Google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        //Add Error toast
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



    }

    private void signIn() {
        Intent signInInent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInInent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mLoginProgress.setVisibility(View.VISIBLE);
            mGoogleBtn.setVisibility(View.INVISIBLE);
            logLoginBtn.setVisibility(View.INVISIBLE);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{
                Toast.makeText(this,"Please Try Again",Toast.LENGTH_LONG).show();
                mLoginProgress.setVisibility(View.INVISIBLE);
                mGoogleBtn.setVisibility(View.VISIBLE);
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG,"FirebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()){
                            mLoginProgress.setVisibility(View.INVISIBLE);

                            checkUserExist();

//                            if(currentUser != null){
//                               // mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
//                                Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
//                                // mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(mainIntent);
//                                finish();
//
//                            }else{
//
//                                String Uid = mAuth.getCurrentUser().getUid();
//                                Intent otherSettup = new Intent(LoginActivity.this,OtherSettup.class);
//                                otherSettup.putExtra("user_id",Uid);
//                                otherSettup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(otherSettup);
//
//                            }








                        }else{
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mGoogleBtn.setVisibility(View.VISIBLE);
                            mLoginProgress.setVisibility(View.INVISIBLE);

                        }
                    }
                });

    }

    private void checkUserExist() {

        //check if user already exist with getCurrentUser method
        if (mAuth.getCurrentUser() != null){

            //then get the UserId stored in a variable...
            final String user_id = mAuth.getCurrentUser().getUid();

            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user_id)){
                        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                        // mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }else{
                        Intent otherSettup = new Intent(LoginActivity.this,OtherSettup.class);
                        otherSettup.putExtra("user_id",user_id);
                        otherSettup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(otherSettup);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    //Login method with email and password
    private void login(String emailText, String passwordtext) {
        if(!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordtext)){
            logLoginBtn.setVisibility(View.INVISIBLE);
            mLoginProgress.setVisibility(View.VISIBLE);
            //Toast.makeText(this,"you can login", Toast.LENGTH_LONG).show();
            //with the auth instance call the firebase login function
            mAuth.signInWithEmailAndPassword(emailText,passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mLoginProgress.setVisibility(View.INVISIBLE);

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
                        mLoginProgress.setVisibility(View.INVISIBLE);
                        logLoginBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this ,"Login Error Please try again",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}

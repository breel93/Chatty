package com.example.breezil.chatty.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.breezil.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar statusToolbar;
    private EditText statusText;
    private Button postStatusbtn;
    //firebase
    private DatabaseReference mDatarefstatus;
    private FirebaseUser mUser;
    //progressDialog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //firebase
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = mUser.getUid();
        mDatarefstatus = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);


        //Toolbar
        statusToolbar = (Toolbar) findViewById(R.id.statusAppBar);
        setSupportActionBar(statusToolbar);
        getSupportActionBar().setTitle("Edit Status...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        statusText = (EditText) findViewById(R.id.statusInput);
        postStatusbtn = (Button) findViewById(R.id.statusPostbtn);

        String status_value = getIntent().getStringExtra("statusValue");
        statusText.setText(status_value);


        postStatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setCanceledOnTouchOutside(true);
                mProgress.setMessage("saving");
                mProgress.show();
                String status = statusText.getText().toString().trim();
                mDatarefstatus.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        }else {
                            Toast.makeText(getApplicationContext(),"Error Please Try Again",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        });




    }
}

package com.example.breezil.chatty.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.breezil.chatty.R;

public class StartActivity extends AppCompatActivity {
    private Button startRegBtn;
    private Button startLoginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startRegBtn = (Button) findViewById(R.id.start_regBtn);
        startLoginbtn = (Button) findViewById(R.id.start_loginBtn);

        startRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(StartActivity.this,RegisterActivity.class));
            }
        });

        startLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,LoginActivity.class));
            }
        });
    }
}

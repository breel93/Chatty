package com.example.breezil.chatty.Activity.ui;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breezil.chatty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Edit_Name_Settings extends DialogFragment {

    EditText mName;
    EditText mFullName;
    Button mSaveBtn;
    Button mCancelBtn;

    private DatabaseReference mDatabaseRef;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;

    String user_name;
    String full_name;

    public Edit_Name_Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.edit_user_name,container, false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = mUser.getUid();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);

        Bundle mArgs = getArguments();
        if(mArgs != null){
            user_name = mArgs.getString("user_name");
            full_name = mArgs.getString("full_name");
        }


        mName = (EditText) view.findViewById(R.id.settingEditUserName);
        mFullName = (EditText) view.findViewById(R.id.settingEditFullName);

        mSaveBtn = (Button) view.findViewById(R.id.saveNameBtn);

        mCancelBtn = (Button) view.findViewById(R.id.cancelNameBtn);

        if(full_name != null){
            mFullName.setText(full_name);
        }

        mName.setText(user_name);


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(getActivity());
                //mProgress.setCanceledOnTouchOutside(true);
                mProgress.setMessage("saving");
                mProgress.show();
                String full_name = mFullName.getText().toString();
                String name = mName.getText().toString();

                save(name,full_name);
                mProgress.dismiss();
                getDialog().dismiss();

            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


       return  view;
    }

    private void save(String name, String full_name) {

        Map statusMap = new HashMap();
        statusMap.put("full_name",full_name);
        statusMap.put("name",name);
        mDatabaseRef.updateChildren(statusMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null){
                    Log.d("Chat_log ",databaseError.getMessage().toString());
                }else {
                    mProgress.dismiss();
                }
            }
        });
    }

}

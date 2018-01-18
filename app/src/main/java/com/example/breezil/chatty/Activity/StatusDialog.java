package com.example.breezil.chatty.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by breezil on 1/18/2018.
 */

public class StatusDialog extends DialogFragment{

    EditText mName;
    EditText mStatus;
    Button mSaveBtn;


    private DatabaseReference mDatabaseRef;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.status_layout,container,false);


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = mUser.getUid();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);

        Bundle mArgs = getArguments();
        String user_name = mArgs.getString("user_name");
        String statusValue = mArgs.getString("statusValue");

        mStatus = (EditText) view.findViewById(R.id.dialogStatus);
        mName = (EditText) view.findViewById(R.id.dialogStatusName);

        mSaveBtn = (Button) view.findViewById(R.id.saveStatusBtn);


        mStatus.setText(statusValue);
        mName.setText(user_name);


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(getActivity());
                //mProgress.setCanceledOnTouchOutside(true);
                mProgress.setMessage("saving");
                mProgress.show();
                String status = mStatus.getText().toString();
                String name = mName.getText().toString();
                if(!TextUtils.isEmpty(status) && !TextUtils.isEmpty(name)){
                    save(name,status);
                    mProgress.dismiss();
                    getDialog().dismiss();
                }else {
                    Toast.makeText(getActivity(),"Type status",Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;
    }

    private void save(String name, String status) {

        Map statusMap = new HashMap();
        statusMap.put("status",status);
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

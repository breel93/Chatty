package com.example.breezil.chatty.Activity;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.breezil.chatty.R;

public class SettingListAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] settingNameList;
    private Integer[] settingIconList;



    public SettingListAdapter(@NonNull Context context, int resource, String[] settingName,Integer[] settingIcon) {
        super(context, resource);

        this.context = context;
        this.settingNameList = settingName;
        this.settingIconList = settingIcon;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.settings_list_item,parent,false);
        TextView settingNameText = (TextView) view.findViewById(R.id.settingName);
        ImageView imageView = (ImageView) view.findViewById(R.id.settingImage);
        settingNameText.setText(settingNameList[position]);
        imageView.setImageResource(settingIconList[position]);
        return view;

    }
}

package com.example.breezil.chatty.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by breezil on 7/25/2017.
 */

class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return  null;
        }


    }

    @Override
    public int getCount() {
        return 3;
    }
//    public CharSequence getPageTitle(int Position){
//        switch (Position){
//            case 0:
//                return "Request";
//            case 1:
//                return "Chat";
//            case 2:
//                return "Friends";
//            default:
//                return null;
//        }
//    }
}

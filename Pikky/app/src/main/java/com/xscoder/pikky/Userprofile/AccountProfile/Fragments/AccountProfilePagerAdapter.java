package com.xscoder.pikky.Userprofile.AccountProfile.Fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class AccountProfilePagerAdapter extends FragmentPagerAdapter {

    int totalTabs;
    private Context myContext;

    public AccountProfilePagerAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                About about = new About();
                return about;

            case 1:
                Fallowing fallowing = new Fallowing();
                return fallowing;
            //Popular sportFragment = new Popular();
            // return sportFragment;

            case 2:
                Media media = new Media();
                return media;
            //Popular sportFragment = new Popular();
            // return sportFragment;

            default:
                return null;
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
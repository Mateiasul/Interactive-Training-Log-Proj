package com.example.android.fireapp;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private  List<Fragment> fragmentList;
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int pos) {
        return fragmentList.get(pos);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}

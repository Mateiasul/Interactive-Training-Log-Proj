/*
package com.example.android.fireapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

public class MainTabActivity  extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    private ViewPager viewPager;
    private TabHost tabHost;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_activity);

        InitViewPager();
        InitTabHost();



    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int selectedPage) {
        tabHost.setCurrentTab(selectedPage);

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onTabChanged(String s) {
        int selectedPage = tabHost.getCurrentTab();
        viewPager.setCurrentItem(selectedPage);

    }

    public class FakeContent implements TabHost.TabContentFactory
    {
        Context mcontext;
        public FakeContent(Context context) {
        mcontext = context;
        }

        @Override
        public View createTabContent(String s) {
            View fakeView = new View(mcontext);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumWidth(0);

            return fakeView;
        }
    }

    private void InitTabHost() {
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        String[] tabNames = {"Tab1", "Tab2"};

        for (int i = 0; i<tabNames.length; i++)
        {
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(tabNames[i]);
            tabSpec.setIndicator(tabNames[i]);
            tabSpec.setContent(new FakeContent(getApplicationContext()));
            tabHost.addTab(tabSpec);

        }
        tabHost.setOnTabChangedListener(this);
    }

    private void InitViewPager() {

        viewPager = findViewById(R.id.view_pager);
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new Fragment1());
        fragmentList.add(new Fragment2());

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }
}
*/

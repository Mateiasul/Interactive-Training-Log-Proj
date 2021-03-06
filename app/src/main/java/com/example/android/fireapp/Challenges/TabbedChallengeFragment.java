package com.example.android.fireapp.Challenges;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.example.android.fireapp.R;
import com.example.android.fireapp.Adapters.MyFragmentPagerAdapter;

import java.util.List;
import java.util.Vector;

public class TabbedChallengeFragment extends Fragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    private ViewPager viewPager;
    private TabHost tabHost;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    int i = 0;
    private View v;
    private FloatingActionButton FAB_addChallenge;
    private BottomNavigationView mBottomNav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       v = inflater.inflate(R.layout.fragment_tabbed, container, false);
        final View navBar = inflater.inflate(R.layout.activity_bottom_nav, container, false);
        mBottomNav = navBar.findViewById(R.id.main_nav_bar);
        mBottomNav.setVisibility(View.GONE);
        FAB_addChallenge = v.findViewById(R.id.fab_add_activity);
        FAB_addChallenge.hide();
        FAB_addChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
     /*           AddFragment fragment = new AddFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                Intent mainIntent = new Intent(getActivity(),AddNewChallengeActivity.class);
                startActivity(mainIntent);

            }
        });


        this.InitViewPager();
        this.InitTabHost(savedInstanceState);

       return  v;
    }

    private void InitTabHost(Bundle args) {
        tabHost = (TabHost) v.findViewById(R.id.tabhost);
        tabHost.setup();

        String[] tabNames = {"All challenges", "My challenges"};

        for (int i = 0; i<tabNames.length; i++)
        {
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(tabNames[i]);
            tabSpec.setIndicator(tabNames[i]);
            tabSpec.setContent(new FakeContent(getActivity()));
            tabHost.addTab(tabSpec);

        }
        tabHost.setOnTabChangedListener(this);
    }

    private void InitViewPager() {
        List<Fragment> fragmentList = new Vector<Fragment>();

        fragmentList.add(new ChallengesFragment());
        fragmentList.add(new MyChallengesFragment());

        this.myFragmentPagerAdapter = new MyFragmentPagerAdapter(
                getChildFragmentManager(),fragmentList);
        this.viewPager = v.findViewById(R.id.view_pager);
        this.viewPager.setAdapter(this.myFragmentPagerAdapter);
        this.viewPager.addOnPageChangeListener(this);
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
        Context context;
        public FakeContent(Context context) {
            this.context = context;
        }

        @Override
        public View createTabContent(String s) {
            View fakeView = new View(context);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumWidth(0);

            return fakeView;
        }
    }

}

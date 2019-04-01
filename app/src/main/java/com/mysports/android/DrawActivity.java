package com.mysports.android;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class DrawActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"关注","动态","附近","消息"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);


        tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpage);
        fragments.add(new Fragment());
        //tabLayout.setupWithViewPager(viewPager,false);

        for(int i=0;i<titles.length;i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
        }
    }
}

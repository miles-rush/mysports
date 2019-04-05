package com.mysports.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mysports.android.Community.CommunityFragmentAdapter;
import com.mysports.android.SmallActivity.PostActivity;
import com.mysports.android.bomb.Community;
import com.mysports.android.fragment.CommunityFragment;
import com.mysports.android.fragment.ExecriseFragment;
import com.mysports.android.fragment.OwnFragment;
import com.mysports.android.fragment.PostFragment;

import java.util.ArrayList;

public class IndexActivity extends AppCompatActivity {
    private Button startRun;
    private Button recordList;
    private Button tip;
    private Button exit;
    private Button goCommunity;

    private TextView username;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;
    private void init() {
//        startRun = (Button) findViewById(R.id.start_run);
//        recordList = (Button) findViewById(R.id.record_list);
//        tip = (Button) findViewById(R.id.toast);
//
//        username = (TextView) findViewById(R.id.username);
//        exit = (Button) findViewById(R.id.exit);
//
//          goCommunity = (Button) findViewById(R.id.go_community);
//
//        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"训练","发现","社区","我的"};
    private CommunityFragmentAdapter pagerAdapter;

    private void initTab() {
        mainTabLayout = (TabLayout) findViewById(R.id.main_tab);
        mainViewPager = (ViewPager) findViewById(R.id.main_viewpage);

        fragments.add(new ExecriseFragment());
        fragments.add(new Fragment());
        fragments.add(new CommunityFragment());
        fragments.add(new OwnFragment());

        pagerAdapter = new CommunityFragmentAdapter(fragments,titles,getSupportFragmentManager());

        mainViewPager.setAdapter(pagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);
        mainTabLayout.getTabAt(0).setIcon(R.mipmap.runer);
        mainTabLayout.getTabAt(1).setIcon(R.mipmap.seemore);
        mainTabLayout.getTabAt(2).setIcon(R.mipmap.communit);
        mainTabLayout.getTabAt(3).setIcon(R.mipmap.myconfig);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_index);
//        init();
        initTab();
//        init();

//        username.setText("欢迎:"+getIntent().getStringExtra("name"));
//        startRun.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(IndexActivity.this,RecordActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        recordList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(IndexActivity.this,RecordListActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        tip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(IndexActivity.this,"info:2019.4.3:20:52:Jrh",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editor = preferences.edit();
//                editor.clear();
//                editor.apply();
//                Intent intent = new Intent(IndexActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        goCommunity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(IndexActivity.this,CommunityActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    //图片的按钮事件
    //返回主界面
    public void home(View view) {
        this.finish();
    }
    //跳转到发布界面
    public void  writePost(View view) {
        Intent intent = new Intent(IndexActivity.this,PostActivity.class);
        startActivity(intent);
    }
}

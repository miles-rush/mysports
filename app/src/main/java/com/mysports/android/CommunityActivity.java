package com.mysports.android;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mysports.android.Community.CommunityItemAdapter;
import com.mysports.android.bomb.Community;
import com.mysports.android.bomb.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CommunityActivity extends AppCompatActivity {
    private EditText text;

    private Button release;

    private List<Community> communityList;

    private RecyclerView itemRecyclerView;

    private LinearLayoutManager manager;

    private CommunityItemAdapter communityItemAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private void init(){
        text = findViewById(R.id.community_text);
        release = findViewById(R.id.community_btn);

        itemRecyclerView = findViewById(R.id.community_list);
        itemRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        communityList = new ArrayList<Community>();
        swipeRefreshLayout = findViewById(R.id.community_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorBlack);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    private void refreshList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1500);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDynamic();
                        communityItemAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    private void getDynamic() {
        BmobQuery<Community> bmobQuery = new BmobQuery<Community>();
        bmobQuery.setLimit(20);
        bmobQuery.findObjects(new FindListener<Community>() {
            @Override
            public void done(List<Community> list, BmobException e) {
                if (e == null) {
                    Toast.makeText(CommunityActivity.this,"加载数据:"+list.size()+"条",Toast.LENGTH_LONG).show();
                    communityList.clear();
                    for (Community community : list) {
                        communityList.add(community);
                        Log.d("info", "done: "+community.getText());
                    }
                    Collections.reverse(communityList);
                    Log.d("info", "done: "+communityList.size());
                }else {
                    Toast.makeText(CommunityActivity.this,"发生错误:"+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setList() {
        getDynamic();
        manager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(manager);
        communityItemAdapter = new CommunityItemAdapter(communityList);
        itemRecyclerView.setAdapter(communityItemAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        init();

        setList();
        release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (text.getText().toString().length() > 0) {
                    Community community = new Community();
                    community.setText(text.getText().toString().trim());
                    community.setClickNum(0);
                    User user = BmobUser.getCurrentUser(User.class);
                    community.setName(user.getUsername());
                    community.setAccountID(user.getAccountID());
                    community.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Snackbar.make(v,"动态已发布",Snackbar.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(true);
                                getDynamic();
                                communityItemAdapter = new CommunityItemAdapter(communityList);

                                try {
                                    Thread.sleep(1500);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }

                                swipeRefreshLayout.setRefreshing(false);
                                itemRecyclerView.setAdapter(communityItemAdapter);

                                //Toast.makeText(CommunityActivity.this,"动态已发布",Toast.LENGTH_SHORT).show();
                            }else {
                                Snackbar.make(v,"发布失败"+e.getMessage(),Snackbar.LENGTH_LONG).show();
                                //Toast.makeText(CommunityActivity.this,"发布失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    swipeRefreshLayout.setRefreshing(true);
                    getDynamic();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            communityItemAdapter.notifyDataSetChanged();
                        }
                    });

                    swipeRefreshLayout.setRefreshing(false);
                }else {
                    Snackbar.make(v,"尚未添加内容",Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }
}

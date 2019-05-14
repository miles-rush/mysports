package com.mysports.android;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mysports.android.Community.CommunityFragmentAdapter;
import com.mysports.android.Community.CommunityItemAdapter;
import com.mysports.android.SmallActivity.PostActivity;
import com.mysports.android.bomb.Community;
import com.mysports.android.bomb.Post;
import com.mysports.android.bomb.PostImage;
import com.mysports.android.bomb.User;
import com.mysports.android.fragment.PostFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
//社区碎片
public class CommunityActivity extends AppCompatActivity {
    private EditText text;

    private Button release;

    private List<Post> postList;

    private RecyclerView itemRecyclerView;

    private LinearLayoutManager manager;

    private CommunityItemAdapter postItemAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Button addImage;

    private List<String> imagePaths;

    private ProgressBar progressBar;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<Fragment> fragments = new ArrayList<>();

    private String[] titles = new String[]{"关注","动态","附近","消息"};

    private CommunityFragmentAdapter pagerAdapter;

    private void init(){
        postList = new ArrayList<Post>();
        tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpage);
        fragments.add(new Fragment());
        PostFragment postFragment = new PostFragment();
        fragments.add(postFragment);
        fragments.add(new Fragment());
        fragments.add(new Fragment());
        pagerAdapter = new CommunityFragmentAdapter(fragments,titles,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }


    private void refreshList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    getDynamic();
                    Thread.sleep(5000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postItemAdapter.setList(postList);
                        postItemAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void getDynamic() {
        BmobQuery<Post> bmobQuery = new BmobQuery<Post>();
        bmobQuery.include("author");
        bmobQuery.setLimit(50);
        bmobQuery.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    Toast.makeText(CommunityActivity.this,"加载数据:"+list.size()+"条",Toast.LENGTH_LONG).show();
                    if (postList.size() > 0) {
                        postList.clear();
                    }
                    for (final Post post : list) {
                        Post p = new Post();
                        p.setObjectId(post.getObjectId());
                        BmobQuery<PostImage> imageQuery = new BmobQuery<PostImage>();
                        imageQuery.addWhereEqualTo("post",new BmobPointer(p));
                        imageQuery.findObjects(new FindListener<PostImage>() {
                            @Override
                            public void done(List<PostImage> list, BmobException e) {
                                for (PostImage postImage:list) {
                                    final BmobFile bmobFile = postImage.getPic();
                                    if (bmobFile!=null) {
                                        post.getPics().add(bmobFile.getFileUrl());
                                    }
                                }
                            }
                        });

                        //加载图片后存入
                        postList.add(post);
                        Log.d("info", "done: "+post.getContent());
                    }
                    Collections.reverse(postList);
                    Log.d("info", "done: "+postList.size());
                }else {
                    Toast.makeText(CommunityActivity.this,"发生错误:"+e.getMessage(),Toast.LENGTH_LONG).show();
                    Log.d("message", "done: "+e.getMessage());
                }
            }
        });
    }

    private void setList() {
        getDynamic();
        manager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(manager);
        postItemAdapter = new CommunityItemAdapter(postList);
        itemRecyclerView.setAdapter(postItemAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_community);
        init();
    }


}

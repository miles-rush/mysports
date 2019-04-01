package com.mysports.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mysports.android.Community.CommunityFragmentAdapter;
import com.mysports.android.Community.CommunityItemAdapter;
import com.mysports.android.CommunityActivity;
import com.mysports.android.R;
import com.mysports.android.bomb.Post;
import com.mysports.android.bomb.PostImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PostFragment extends Fragment {
    private RecyclerView recyclerView;
    private CommunityItemAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private StaggeredGridLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    public List<Post> postList = new ArrayList<Post>();
    final int REFRESH_POST_DATA = 1;
    final int FIRST_POST_DATA = 2;
    private int oldSize;
    private int newSize;
    private boolean loadData;
    public void setList(List<Post> list) {
        postList = list;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_POST_DATA:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getDynamic();
                                while (loadData) {
                                    Thread.sleep(1000);
                                }
                                Thread.sleep(2000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //adapter = new CommunityItemAdapter(postList);
                                    //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                                    //recyclerView.setLayoutManager(linearLayoutManager);
                                    //recyclerView.setAdapter(adapter);
                                    //adapter.notifyDataSetChanged();
                                    adapter.notifyItemRangeChanged(0,newSize-oldSize);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }

                    }).start();
                    break;
                case FIRST_POST_DATA:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            });
                            try {
                                getDynamic();
                                while (loadData) {
                                    Thread.sleep(1000);
                                }
                                Thread.sleep(2000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    adapter = new CommunityItemAdapter(postList);
//                                    recyclerView.setLayoutManager(linearLayoutManager);
//                                    recyclerView.setAdapter(adapter);
//                                    adapter.notifyDataSetChanged();
                                    adapter.notifyItemRangeChanged(0,newSize-oldSize);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }

                    }).start();
                    break;
            }
        }
    };
    //执行-2
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_post,container,false);
        //数据数组初始化
        postList = new ArrayList<Post>();
        loadData = true;
        oldSize = 0;
        newSize = 0;

        //初始化列表控件
        recyclerView = view.findViewById(R.id.community_list);
        linearLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //linearLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        //设置控件格式
        recyclerView.setLayoutManager(linearLayoutManager);
        //初始化适配器
        adapter = new CommunityItemAdapter(postList);
        //为控件设置适配器
        recyclerView.setAdapter(adapter);
        //
        adapter.notifyDataSetChanged();


        //初始化其它控件
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.community_swipe_refresh);
        progressBar = (ProgressBar) view.findViewById(R.id.post_wait);

        //设置刷新事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Message message = new Message();
                message.what = REFRESH_POST_DATA;
                handler.sendMessage(message);
            }
        });
        return view;
    }


    //执行-3
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (recyclerView == null) {
            recyclerView = getActivity().findViewById(R.id.community_list);
            linearLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new CommunityItemAdapter(postList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
//        recyclerView.setItemAnimator(null);
//        linearLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

    }

    @Override
    public void onStart() {
        super.onStart();
//        Message message = new Message();
//        message.what = FIRST_POST_DATA;
//        handler.sendMessage(message);

    }

    @Override
    public void onResume() {
        super.onResume();
        Message message = new Message();
        message.what = FIRST_POST_DATA;
        handler.sendMessage(message);

    }

    //最早执行-1
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.community_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        adapter = new CommunityItemAdapter(postList);
        recyclerView.setAdapter(adapter);
    }


    private void getDynamic() {
        BmobQuery<Post> bmobQuery = new BmobQuery<Post>();
        bmobQuery.include("author");
        bmobQuery.setLimit(20);
        //bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        //开始加载数据
        loadData = false;
        bmobQuery.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    oldSize = postList.size();
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
                    newSize = postList.size();
                    loadData = false;
                    Log.d("info", "done: "+postList.size());
                }else {
                    Log.d("message", "done: "+e.getMessage());
                    Toast.makeText(getContext(),"数据加载失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

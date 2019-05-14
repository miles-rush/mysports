package com.mysports.android.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.mysports.android.Community.CommunityFragmentAdapter;
import com.mysports.android.Community.CommunityItemAdapter;
import com.mysports.android.CommunityActivity;
import com.mysports.android.R;
import com.mysports.android.bomb.Post;
import com.mysports.android.bomb.PostImage;
import com.mysports.android.layout.MyRefreshFooter;
import com.mysports.android.layout.MyRefreshHead;
import com.mysports.android.media.GlideUtil;

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
    private SwipeToLoadLayout swipeToLoadLayout;
    public List<Post> postList = new ArrayList<Post>();
    final int REFRESH_POST_DATA = 1;
    final int FIRST_POST_DATA = 2;
    final int LOAD_MORE_DATA = 3;
    private int oldSize;
    private int newSize;
    private int skip; //用于加载更多数据时的跳跃
    private boolean loadData;
    private View view;
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
                            if (getActivity()==null){
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //adapter = new CommunityItemAdapter(postList);
                                    //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                                    //recyclerView.setLayoutManager(linearLayoutManager);
                                    //recyclerView.setAdapter(adapter);
                                    //adapter.notifyDataSetChanged();
                                    adapter.notifyDataSetChanged();
                                    //adapter.notifyItemRangeChanged(0,newSize-oldSize);
                                    //swipeRefreshLayout.setRefreshing(false);
                                    swipeToLoadLayout.setRefreshing(false);
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
                            if (getActivity()==null){
                                return;
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
                case LOAD_MORE_DATA:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LoadMoreDynamic();
                                while (loadData) {
                                    Thread.sleep(1000);
                                }
                                Thread.sleep(2000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            //处理数据尚未加载完毕时退出销毁了当前碎片
                            if (getActivity()==null){
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemRangeChanged(oldSize,newSize);
                                    swipeToLoadLayout.setLoadingMore(false);

                                }
                            });
                        }

                    }).start();
                    break;
                    default:
                        break;
            }
        }
    };
    //执行-2
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.frame_post,container,false);
            //数据数组初始化
            postList = new ArrayList<Post>();
            loadData = true;
            oldSize = 0;
            newSize = 0;
            skip = 0;
            //初始化列表控件
            //recyclerView = view.findViewById(R.id.community_list);
            recyclerView = view.findViewById(R.id.swipe_target);
            //初始化适配器
            adapter = new CommunityItemAdapter(postList);
            //为控件设置适配器
            recyclerView.setAdapter(adapter);

            linearLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            linearLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            //设置控件格式
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            adapter.notifyDataSetChanged();

            swipeToLoadLayout = view.findViewById(R.id.c_swipeToLoadLayout);
            //刷新头部设置
            //MyRefreshHead myRefreshHead = (MyRefreshHead) view.findViewById(R.id.swipe_refresh_header);
            MyRefreshHead myRefreshHead = new MyRefreshHead(getContext());
            myRefreshHead.setPadding(20,20,20,20);
            myRefreshHead.setGravity(Gravity.CENTER);
            myRefreshHead.setText("下拉刷新");
            myRefreshHead.setTextColor(Color.parseColor("#D81B60"));
            //刷新尾部设置
            MyRefreshFooter myRefreshFooter = new MyRefreshFooter(getContext());
            myRefreshFooter.setPadding(20,20,20,20);
            myRefreshFooter.setGravity(Gravity.CENTER);
            myRefreshFooter.setText("上拉加载更多");
            myRefreshFooter.setTextColor(Color.parseColor("#D81B60"));
            swipeToLoadLayout.setRefreshHeaderView(myRefreshHead);
            swipeToLoadLayout.setLoadMoreFooterView(myRefreshFooter);
            //下拉刷新
            swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Message message = new Message();
                    message.what = REFRESH_POST_DATA;
                    handler.sendMessage(message);
                }
            });
            //下拉加载更多
            swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    Message message = new Message();
                    message.what = LOAD_MORE_DATA;
                    handler.sendMessage(message);
                }
            });

            //初始化其它控件
            progressBar = (ProgressBar) view.findViewById(R.id.post_wait);

        }

        return view;
    }

    //TODO：在这里清除了缓存 没有测试过
    @Override
    public void onDestroy() {
        super.onDestroy();
        GlideUtil.clearMemoryCache(getContext());
        GlideUtil.clearFileCache(getContext());
    }

    public void setManager(StaggeredGridLayoutManager manager) {
        recyclerView.setLayoutManager(manager);
    }
    //执行-3
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (recyclerView == null) {
            //recyclerView = getActivity().findViewById(R.id.community_list);
            recyclerView = getActivity().findViewById(R.id.swipe_target);
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

    //修改了碎片的切换方式
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (postList.size()==0){
            Message message = new Message();
            message.what = FIRST_POST_DATA;
            handler.sendMessage(message);
        }

        //linearLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
    }

    //最早执行-1
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    private void init(View view) {
//        recyclerView = view.findViewById(R.id.community_list);
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
//        adapter = new CommunityItemAdapter(postList);
//        recyclerView.setAdapter(adapter);
//    }

    //加载更多数据
    private void LoadMoreDynamic() {
        BmobQuery<Post> bmobQuery = new BmobQuery<Post>();
        bmobQuery.include("author");
        bmobQuery.setLimit(10); //一次查询的数据条数
        bmobQuery.order("-createdAt");
        if (postList.size() == 10) { //刷新过了
            skip = 10;
        }else {
            skip += 10;
        }

        bmobQuery.setSkip(skip);
        //bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        //开始加载数据
        loadData = false;
        bmobQuery.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    oldSize = postList.size();
                    //Collections.reverse(postList);
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
                    //Collections.reverse(postList);
                    //newSize = postList.size();
                    loadData = false;
                    Log.d("info", "done: "+postList.size());
                }else {
                    Log.d("message", "done: "+e.getMessage());
                    Toast.makeText(getContext(),"数据加载失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //刷新数据和第一次显示数据
    private void getDynamic() {
        BmobQuery<Post> bmobQuery = new BmobQuery<Post>();
        bmobQuery.include("author");
        bmobQuery.setLimit(10); //一次查询的数据条数
        bmobQuery.order("-createdAt");
        //bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        //开始加载数据
        loadData = false;
        bmobQuery.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    oldSize = postList.size();
                    //Collections.reverse(postList);
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
                    //Collections.reverse(postList);
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

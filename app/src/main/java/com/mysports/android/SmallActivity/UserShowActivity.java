package com.mysports.android.SmallActivity;

import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.mysports.android.Community.CommunityItemAdapter;
import com.mysports.android.Community.RecordPostItemAdapter;
import com.mysports.android.R;
import com.mysports.android.bomb.Post;
import com.mysports.android.bomb.Record;
import com.mysports.android.bomb.User;
import com.mysports.android.layout.MyRefreshFooter;
import com.mysports.android.layout.MyRefreshHead;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserShowActivity extends AppCompatActivity {
    private String ID;

    private User localUser;

    private TextView name;
    private TextView time;

    private ImageView unlike;
    private ImageView message;

    private ImageView change_post;
    private ImageView change_record;

    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_user_show);

        ID = getIntent().getStringExtra("ID");
        init();
        downloadUser();
    }


    private void downloadUser() {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(ID, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    localUser = user;
                    dataSet();
                    downloadPost();
                    //downloadRecord();
                }else {
                    Toast.makeText(getApplicationContext(),"数据加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        name = findViewById(R.id.aus_name);
        time = findViewById(R.id.aus_time);

        unlike = findViewById(R.id.aus_unlike);
        message = findViewById(R.id.aus_message);

        change_post = findViewById(R.id.aus_post);
        change_record = findViewById(R.id.aus_record);

        swipeToLoadLayout = findViewById(R.id.aus_swipeToLoadLayout);
        recyclerView = findViewById(R.id.swipe_target);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        //刷新头部设置
        MyRefreshHead myRefreshHead = new MyRefreshHead(this);
        myRefreshHead.setPadding(20,20,20,20);
        myRefreshHead.setGravity(Gravity.CENTER);
        myRefreshHead.setText("下拉刷新");
        myRefreshHead.setTextColor(Color.parseColor("#D81B60"));
        //刷新尾部设置
        MyRefreshFooter myRefreshFooter = new MyRefreshFooter(this);
        myRefreshFooter.setPadding(20,20,20,20);
        myRefreshFooter.setGravity(Gravity.CENTER);
        myRefreshFooter.setText("上拉加载更多");
        myRefreshFooter.setTextColor(Color.parseColor("#D81B60"));


        swipeToLoadLayout.setRefreshHeaderView(myRefreshHead);
        swipeToLoadLayout.setLoadMoreFooterView(myRefreshFooter);

        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToLoadLayout.setRefreshing(false);
            }
        });

        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (nowPost) {
                    postSkip += 5;
                    downloadPost();
                }else {
                    recordSkip += 5;
                    downloadRecord();
                }
            }
        });

        change_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowPost == false) {
                    postSkip = 5;
                    downloadPost();
                }
            }
        });

        change_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowRecord == false) {
                    recordSkip = 5;
                    downloadRecord();
                }
            }
        });

        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLike();

            }
        });
    }

    //取消关注
    private void doUnLike() {
        User my = new User();
        my.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());
        User other = new User();
        other.setObjectId(ID);
        BmobRelation relation = new BmobRelation();
        relation.remove(my);
        other.setLikes(relation);
        other.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(),"取关成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void doneLike() {
        User user = new User();
        user.setObjectId(localUser.getObjectId());
        BmobRelation relation = new BmobRelation();
        User my = new User();
        my.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());
        relation.add(my);
        user.setLikes(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(),"关注成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"关注失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean check = false;
    private int likeNum = 0;
    private void checkLike() {
        check = false;
        BmobQuery<User> query = new BmobQuery<>();
        User user = new User();
        user.setObjectId(localUser.getObjectId());
        query.addWhereRelatedTo("likes", new BmobPointer(user));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    check = false;
                    for (User u : list) {
                        likeNum ++ ;
                        if (u.getObjectId().equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                            check = true;
                        }
                    }
                    if (check) {
                        doUnLike();
                    }else {
                        doneLike();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"关注信息拉取失败",Toast.LENGTH_SHORT).show();
                    check = false;
                    //doneLike();
                }
            }
        });


    }

    private void dataSet(){
        name.setText(localUser.getUsername());
        time.setText(localUser.getCreatedAt());
    }


    private CommunityItemAdapter communityItemAdapter;
    private void changeToPost() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                communityItemAdapter = new CommunityItemAdapter(postList);
                recyclerView.setAdapter(communityItemAdapter);
                communityItemAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(postSkip-5);
            }
        });

    }


    private RecordPostItemAdapter postItemAdapter;
    private void changeToRecord() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                postItemAdapter = new RecordPostItemAdapter(recordList);
                recyclerView.setAdapter(postItemAdapter);
                postItemAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(recordSkip-5);
            }
        });

    }

    private boolean nowPost = false;
    private int postSkip = 5;
    private List<Post> postList = new ArrayList<>();
    private void downloadPost() {
        BmobQuery<Post> query = new BmobQuery<>();
        User user = new User();
        user.setObjectId(ID);
        query.addWhereEqualTo("author", user);
        query.setLimit(postSkip);
        query.order("-createdAt");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    postList.clear();
                    for (Post p : list) {
                        postList.add(p);
                    }
                    if (postList.size() > 0) {
                        changeToPost();
                        nowPost = true;
                        nowRecord = false;
                    }
                    swipeToLoadLayout.setLoadingMore(false);
                }else {
                    Toast.makeText(getApplicationContext(),"数据加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean nowRecord = false;
    private int recordSkip = 5;
    private List<Record> recordList = new ArrayList<>();
    private void downloadRecord() {
        BmobQuery<Record> query = new BmobQuery<>();
        User user = new User();
        user.setObjectId(ID);
        query.addWhereEqualTo("author", user);
        query.setLimit(recordSkip);
        query.findObjects(new FindListener<Record>() {
            @Override
            public void done(List<Record> list, BmobException e) {
                if (e == null) {
                    recordList.clear();
                    recordList = list;
                    changeToRecord();
                    nowPost = false;
                    nowRecord = true;
                    swipeToLoadLayout.setLoadingMore(false);
                } else {
                    Toast.makeText(getApplicationContext(),"数据加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

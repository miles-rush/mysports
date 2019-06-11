package com.mysports.android.SmallActivity;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.mysports.android.Community.MessageAdapter;
import com.mysports.android.R;
import com.mysports.android.bomb.Message;
import com.mysports.android.bomb.User;
import com.mysports.android.layout.MyRefreshHead;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

//私信模块
public class ChatActivity extends AppCompatActivity {
    private String ID;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private MessageAdapter adapter;

    private EditText text;

    private ImageView send;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);
        ID = getIntent().getStringExtra("ID");

        init();

        loadMessage();
    }
    private void init() {
        swipeToLoadLayout = findViewById(R.id.ac_swipeToLoadLayout);
        recyclerView = findViewById(R.id.swipe_target);

        text = findViewById(R.id.message_edit);
        send = findViewById(R.id.message_post);

        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        //刷新头部设置
        MyRefreshHead myRefreshHead = new MyRefreshHead(this);
        myRefreshHead.setPadding(20,20,20,20);
        myRefreshHead.setGravity(Gravity.CENTER);
        myRefreshHead.setText("下拉刷新");
        myRefreshHead.setTextColor(Color.parseColor("#D81B60"));
        swipeToLoadLayout.setRefreshHeaderView(myRefreshHead);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMessage();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }


    private List<Message> messageList = new ArrayList<>();
    private void loadMessage() {
        User my = new User(); //登陆用户
        my.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());
        User recevier = new User(); //关注用户
        recevier.setObjectId(ID);

        BmobQuery<Message> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("sender", my);
        BmobQuery<Message> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("recevier", recevier);

        BmobQuery<Message> eq3 = new BmobQuery<>();
        eq3.addWhereEqualTo("sender", recevier);
        BmobQuery<Message> eq4 = new BmobQuery<>();
        eq4.addWhereEqualTo("recevier", my);

        List<BmobQuery<Message>> queries1 = new ArrayList<>();
        queries1.add(eq1);
        queries1.add(eq2);
        BmobQuery<Message> main1 = new BmobQuery<>();
        BmobQuery<Message> and1 = main1.and(queries1);

        List<BmobQuery<Message>> queries2 = new ArrayList<>();
        queries2.add(eq3);
        queries2.add(eq4);
        BmobQuery<Message> main2 = new BmobQuery<>();
        BmobQuery<Message> and2 = main2.and(queries2);

        List<BmobQuery<Message>> andQuerys = new ArrayList<>();
        andQuerys.add(and1);
        andQuerys.add(and2);

        BmobQuery<Message> query = new BmobQuery<>();
        query.or(andQuerys);

        query.include("sender,recevier");
        query.order("-createdAt");
        query.findObjects(new FindListener<Message>() {
            @Override
            public void done(List<Message> list, BmobException e) {
                if (e == null) {
                    messageList.clear();
                    for (Message message : list) {
                        messageList.add(message);
                    }
                    Collections.reverse(messageList);
                    Log.d("sizesize", "done: "+list.size());
                    adapter.notifyDataSetChanged();
                    swipeToLoadLayout.setRefreshing(false);
                }else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendMessage() {
        String content = text.getText().toString().trim();
        if (content.length() > 0) {
            Message message = new Message();
            User my = new User();
            my.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());
            User recevier = new User();
            recevier.setObjectId(ID);
            message.setContent(content);
            message.setSender(my);
            message.setRecevier(recevier);

            message.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();
                        loadMessage();
                    }else {
                        Toast.makeText(getApplicationContext(),"发送失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"输入内容不能为空",Toast.LENGTH_SHORT).show();
        }
    }


    public void acFinish(View v) {
        finish();
    }
}

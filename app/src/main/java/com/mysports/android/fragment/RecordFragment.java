package com.mysports.android.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.mysports.android.Community.RecordPostItemAdapter;
import com.mysports.android.R;
import com.mysports.android.bomb.Record;
import com.mysports.android.layout.MyRefreshFooter;
import com.mysports.android.layout.MyRefreshHead;
import com.mysports.android.media.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class RecordFragment extends Fragment {
    private View mView;

    private RecyclerView recordList; //内部list
    private SwipeToLoadLayout mainList; //总外部布局
    private LinearLayoutManager manager;
    private RecordPostItemAdapter adapter;

    private List<Record> records = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.frame_record,container,false);

            mainList = mView.findViewById(R.id.record_post_list);
            recordList = mView.findViewById(R.id.swipe_target);

            manager = new LinearLayoutManager(getContext());
            recordList.setLayoutManager(manager);
            adapter = new RecordPostItemAdapter(records);
            recordList.setAdapter(adapter);
            recordList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

            //刷新头部设置
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

            mainList.setRefreshHeaderView(myRefreshHead);
            mainList.setLoadMoreFooterView(myRefreshFooter);
            loadData();
            //刷新事件
            mainList.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loads = 2;
                    loadData();
                }
            });

            //加载时间
            mainList.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loads += 1;
                    loadData();
                }
            });

        }
        Log.d("fragment", "onCreateView: "+"碎片创建了");
        return mView;
    }


    private int loads = 2; //初始加载量
    private void loadData() {
        BmobQuery<Record> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(loads);
        query.include("author");
        query.findObjects(new FindListener<Record>() {
            @Override
            public void done(List<Record> list, BmobException e) {
                if (e == null) {
                    records.clear();
                    for (Record record : list) {
                        records.add(record);
                    }
                    adapter.notifyDataSetChanged();
                    mainList.setRefreshing(false);
                    mainList.setLoadingMore(false);
                }else {
                    Toast.makeText(getContext(),"数据加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mView) {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

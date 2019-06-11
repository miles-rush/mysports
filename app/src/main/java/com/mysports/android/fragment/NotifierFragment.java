package com.mysports.android.fragment;

import android.graphics.Color;
import android.icu.lang.UScript;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.mysports.android.Community.NotifierAdapter;
import com.mysports.android.Community.UserItemAdpater;
import com.mysports.android.R;
import com.mysports.android.bomb.Notifier;
import com.mysports.android.bomb.User;
import com.mysports.android.layout.MyRefreshHead;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class NotifierFragment extends Fragment {
    private View mView;
    private RecyclerView recordList;
    private SwipeToLoadLayout mainList;
    private LinearLayoutManager manager;

    private NotifierAdapter adapter;

    private List<Notifier> notifierList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.notifier_fragment,container,false);
            mainList = mView.findViewById(R.id.notifier_swipeToLoadLayout);
            recordList = mView.findViewById(R.id.swipe_target);

            manager = new LinearLayoutManager(getContext());
            recordList.setLayoutManager(manager);
            adapter = new NotifierAdapter(notifierList, this);
            recordList.setAdapter(adapter);
            recordList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));


            MyRefreshHead myRefreshHead = new MyRefreshHead(getContext());
            myRefreshHead.setPadding(20,20,20,20);
            myRefreshHead.setGravity(Gravity.CENTER);
            myRefreshHead.setText("下拉刷新");
            myRefreshHead.setTextColor(Color.parseColor("#D81B60"));
            downloadNotifier();
            mainList.setRefreshHeaderView(myRefreshHead);

            mainList.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    downloadNotifier();

                }
            });
        }


        return mView;
    }
    public void notifier() {
        downloadNotifier();
    }

    private void downloadNotifier() {
        BmobQuery<Notifier> query = new BmobQuery<>();
        User my = new User();
        my.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());
        query.addWhereEqualTo("Interactive", my);
        query.include("my");
        query.findObjects(new FindListener<Notifier>() {
            @Override
            public void done(List<Notifier> list, BmobException e) {
                if (e == null) {
                    notifierList.clear();
                    for (Notifier n : list) {
                        notifierList.add(n);
                    }
                    Log.d("aaaaaaaaa", "done: "+ list.size());
                    adapter.notifyDataSetChanged();
                    mainList.setRefreshing(false);
                }else {
                    Toast.makeText(getContext(),"通知加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //super.onDestroyView();
        if (null != mView) {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
    }
}

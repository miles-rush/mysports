package com.mysports.android.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.mysports.android.Community.UserItemAdpater;
import com.mysports.android.R;
import com.mysports.android.bomb.User;
import com.mysports.android.layout.MyRefreshHead;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LikeFragment extends Fragment {
    private View mView;
    private RecyclerView recordList;
    private SwipeToLoadLayout mainList;
    private LinearLayoutManager manager;
    private UserItemAdpater adpater;

    private List<User> users = new ArrayList<>();

    public LikeFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_like,container,false);
            mainList = mView.findViewById(R.id.like_swipeToLoadLayout);
            recordList = mView.findViewById(R.id.swipe_target);

            manager = new LinearLayoutManager(getContext());
            recordList.setLayoutManager(manager);
            adpater = new UserItemAdpater(users);
            recordList.setAdapter(adpater);
            recordList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

            MyRefreshHead myRefreshHead = new MyRefreshHead(getContext());
            myRefreshHead.setPadding(20,20,20,20);
            myRefreshHead.setGravity(Gravity.CENTER);
            myRefreshHead.setText("下拉刷新");
            myRefreshHead.setTextColor(Color.parseColor("#D81B60"));
            downloadData();
            mainList.setRefreshHeaderView(myRefreshHead);

            mainList.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    downloadData();
                }
            });

        }

        return mView;
    }


    private void downloadData() {
        BmobQuery<User> query = new BmobQuery<>();
        final User user = new User();
        user.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());
        query.addWhereRelatedTo("likes", new BmobPointer(user));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    users.clear();
                    for (User u : list){
                        users.add(u);
                    }
                    adpater.notifyDataSetChanged();
                    mainList.setRefreshing(false);
                } else {
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

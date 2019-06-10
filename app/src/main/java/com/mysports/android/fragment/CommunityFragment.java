package com.mysports.android.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mysports.android.Community.CommunityFragmentAdapter;
import com.mysports.android.R;
import com.mysports.android.bomb.Post;

import java.util.ArrayList;

public class CommunityFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<Fragment> fragments = new ArrayList<>();

    private String[] titles = new String[]{"足迹","动态","关注","消息"};

    private CommunityFragmentAdapter pagerAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_community,container,false);
        init(view);
        return view;
    }


    private void init(View view){
        tabLayout = (TabLayout) view.findViewById(R.id.tab);
        viewPager = (ViewPager) view.findViewById(R.id.viewpage);
        viewPager.setOffscreenPageLimit(2);
        PostFragment postFragment = new PostFragment();
        RecordFragment recordFragment = new RecordFragment();
        LikeFragment likeFragment = new LikeFragment();
        fragments.add(recordFragment);
        fragments.add(postFragment);
        fragments.add(likeFragment);
        fragments.add(new Fragment());
        pagerAdapter = new CommunityFragmentAdapter(fragments,titles,getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

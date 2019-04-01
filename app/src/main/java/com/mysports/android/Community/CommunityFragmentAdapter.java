package com.mysports.android.Community;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class CommunityFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = null;
    private String[] titles = null;

    public CommunityFragmentAdapter(List<Fragment> fragmentList,String[] titles ,FragmentManager fm) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.titles = titles;
    }

    public CommunityFragmentAdapter(List<Fragment> fragmentList, FragmentManager fm) {
        super(fm);
        this.mFragmentList = fragmentList;
    }
    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (titles!=null){
            return titles[position];
        }else {
            return null;
        }
    }


}

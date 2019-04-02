package com.mysports.android.layout;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

public class MyRefreshFooter extends AppCompatTextView implements SwipeLoadMoreTrigger,SwipeTrigger {
    public MyRefreshFooter(Context context) {
        super(context);

    }

    @Override
    public void onLoadMore() {
        setText("正在加载数据喵...");
    }

    public MyRefreshFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public void onPrepare() {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        setText("加载成功");
    }

    @Override
    public void onReset() {
        setText("上拉加载更多");
    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {

    }
}

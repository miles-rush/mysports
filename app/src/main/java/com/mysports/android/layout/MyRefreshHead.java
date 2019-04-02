package com.mysports.android.layout;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;
//刷新头部样式
public class MyRefreshHead extends AppCompatTextView implements SwipeRefreshTrigger,SwipeTrigger {
    public MyRefreshHead(Context context) {
        super(context);

    }

    public MyRefreshHead(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRefreshHead(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onRefresh() {
        setText("正在加载数据喵...");
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        setText("刷新成功");
    }

    @Override
    public void onReset() {
        setText("下拉刷新");
    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {

    }
}

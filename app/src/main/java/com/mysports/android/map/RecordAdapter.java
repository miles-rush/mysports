package com.mysports.android.map;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysports.android.R;

import java.util.List;

public class RecordAdapter extends BaseAdapter {
    private Context mContext;
    private List<PathRecord> mRecordList;

    //清空内容
    public void clear() {
        mRecordList.clear();
    }

    public RecordAdapter(Context context, List<PathRecord> list) {
        this.mContext = context;
        this.mRecordList = list;
    }

    @Override
    public int getCount() {
        return mRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.recorditem, null);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.record = (TextView) convertView.findViewById(R.id.record);
            holder.delete = (ImageView) convertView.findViewById(R.id.item_delete); //删除单项
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PathRecord item = mRecordList.get(position);
        holder.date.setText(item.getDate());
        holder.record.setText(item.toString());

        //删除
        holder.delete.setTag(position);
        holder.delete.setOnClickListener(onDeleteItem);
        return convertView;
    }

    private View.OnClickListener onDeleteItem; //删除的接口

    public void setOnDeleteItem(View.OnClickListener onDeleteItem) {
        this.onDeleteItem = onDeleteItem;
    }

    private class ViewHolder {
        TextView date;
        TextView record;
        ImageView delete;
    }
}

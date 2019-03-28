package com.mysports.android.Community;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mysports.android.R;
import com.mysports.android.bomb.Community;

import java.util.List;

public class CommunityItemAdapter extends RecyclerView.Adapter<CommunityItemAdapter.ViewHolder>{

    private List<Community> itemlist;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView text;
        TextView author;
        TextView date;
        TextView click;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            text = view.findViewById(R.id.c_item_text);
            author = view.findViewById(R.id.c_item_author);
            date = view.findViewById(R.id.c_item_date);
            click = view.findViewById(R.id.c_item_click);
//            author.setGravity(Gravity.RIGHT);
//            date.setGravity(Gravity.RIGHT);
//            click.setGravity(Gravity.RIGHT);
        }
    }
    public CommunityItemAdapter(List<Community> list) {
        itemlist = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.community_item,viewGroup,false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Community community = itemlist.get((Integer)v.getTag());
                Snackbar.make(v,"暂停只显示部分数据:"+community.getText(),Snackbar.LENGTH_LONG).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(i);
        Community community = itemlist.get(i);

        viewHolder.text.setText("    "+community.getText().trim());
        viewHolder.date.setText(community.getCreatedAt().trim());
        viewHolder.author.setText(community.getName().trim());
        viewHolder.click.setText(("浏览:"+community.getClickNum()).trim()+"次");
        Log.d("bind", "onBindViewHolder: 1");
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }
}

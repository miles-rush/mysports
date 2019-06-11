package com.mysports.android.Community;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mysports.android.R;
import com.mysports.android.SmallActivity.ChatActivity;
import com.mysports.android.SmallActivity.PostItemActivity;
import com.mysports.android.SmallActivity.PostRecordShowActivity;
import com.mysports.android.bomb.Notifier;
import com.mysports.android.bomb.Record;
import com.mysports.android.fragment.NotifierFragment;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class NotifierAdapter extends RecyclerView.Adapter<NotifierAdapter.ViewHolder>{
    private List<Notifier> notifierList;
    private NotifierFragment content;

    public NotifierAdapter(List<Notifier> list, NotifierFragment content) {
        notifierList = list;
        this.content = content;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView from;
        TextView time;
        ImageView delete;

        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            content = view.findViewById(R.id.ni_content);
            from = view.findViewById(R.id.ni_from);
            time = view.findViewById(R.id.ni_time);
            delete = view.findViewById(R.id.ni_delete);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Notifier notifier = notifierList.get(i);
        viewHolder.time.setText(notifier.getCreatedAt());
        viewHolder.from.setText("From:"+notifier.getMy().getUsername());
        switch (notifier.getType()) {
            case 1:
                viewHolder.content.setText("运动记录收到一个赞");
                break;
            case 2:
                viewHolder.content.setText("动态收到一个赞");
                break;
            case 3:
                viewHolder.content.setText("收到关注");
                break;
            case 4:
                viewHolder.content.setText("动态收到评论");
                break;
            case 5:
                viewHolder.content.setText("评论收到回复");
                break;
            case 6:
                viewHolder.content.setText("收到私信");
                break;
                default:
                    break;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notifier_item,viewGroup,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                Notifier notifier = notifierList.get(pos);
                switch (notifier.getType()) {
                    case 1:
                        Intent intent1 = new Intent(v.getContext(), PostRecordShowActivity.class);
                        intent1.putExtra("ID", notifier.getObjectID());
                        v.getContext().startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(v.getContext(), PostItemActivity.class);
                        intent2.putExtra("ID", notifier.getObjectID());
                        v.getContext().startActivity(intent2);
                        break;
                    case 3:
                        break;
                    case 4:
                        Intent intent4 = new Intent(v.getContext(), PostItemActivity.class);
                        intent4.putExtra("ID", notifier.getObjectID());
                        v.getContext().startActivity(intent4);
                        break;
                    case 5:
                        Intent intent5 = new Intent(v.getContext(), PostItemActivity.class);
                        intent5.putExtra("ID", notifier.getObjectID());
                        v.getContext().startActivity(intent5);
                        break;
                    case 6:
                        Intent intent6 = new Intent(v.getContext(), ChatActivity.class);
                        intent6.putExtra("ID", notifier.getMy().getObjectId());
                        v.getContext().startActivity(intent6);
                        break;
                    default:
                        break;
                }
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                Notifier notifier = notifierList.get(pos);
                notifier.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            content.notifier();
                        }else {

                        }
                    }
                });
            }
        });
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return notifierList.size();
    }
}

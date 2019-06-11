package com.mysports.android.Community;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysports.android.R;
import com.mysports.android.SmallActivity.UserShowActivity;
import com.mysports.android.bomb.Message;
import com.mysports.android.bomb.User;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.util.V;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> list) {
        messageList = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameLeft;
        TextView timeLeft;
        TextView nameRight;
        TextView timeRight;

        TextView content;

        ImageView left;
        ImageView right;

        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nameLeft = view.findViewById(R.id.mi_name);
            timeLeft = view.findViewById(R.id.mi_time);

            nameRight = view.findViewById(R.id.mi_name_right);
            timeRight = view.findViewById(R.id.mi_time_right);

            left = view.findViewById(R.id.mi_image);
            right = view.findViewById(R.id.mi_image_right);

            content = view.findViewById(R.id.mi_content);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Message message = messageList.get(i);
        String myID = BmobUser.getCurrentUser(User.class).getObjectId();
        viewHolder.content.setText(message.getContent());
        //信息是自己发送 使用右布局
        if (message.getSender().getObjectId().equals(myID)) {
            viewHolder.right.setVisibility(View.VISIBLE);
            viewHolder.nameRight.setVisibility(View.VISIBLE);
            viewHolder.timeRight.setVisibility(View.VISIBLE);
            viewHolder.nameRight.setText(message.getSender().getUsername());
            viewHolder.timeRight.setText(message.getCreatedAt());


            viewHolder.left.setVisibility(View.GONE);
            viewHolder.nameLeft.setVisibility(View.GONE);
            viewHolder.timeLeft.setVisibility(View.GONE);
        }else {
            viewHolder.left.setVisibility(View.VISIBLE);
            viewHolder.nameLeft.setVisibility(View.VISIBLE);
            viewHolder.timeLeft.setVisibility(View.VISIBLE);
            viewHolder.nameLeft.setText(message.getSender().getUsername());
            viewHolder.timeLeft.setText(message.getCreatedAt());



            viewHolder.right.setVisibility(View.GONE);
            viewHolder.nameRight.setVisibility(View.GONE);
            viewHolder.timeRight.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

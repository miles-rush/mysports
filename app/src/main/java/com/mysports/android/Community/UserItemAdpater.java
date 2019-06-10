package com.mysports.android.Community;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysports.android.R;
import com.mysports.android.SmallActivity.UserShowActivity;
import com.mysports.android.bomb.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class UserItemAdpater extends RecyclerView.Adapter<UserItemAdpater.ViewHolder> {
    private List<User> users;

    public UserItemAdpater(List<User> list) {
        users = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.lui_name);
            time = view.findViewById(R.id.lui_time);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        User user = users.get(i);
        viewHolder.name.setText(user.getUsername());
        viewHolder.time.setText(user.getCreatedAt());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.like_user_item,viewGroup,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                int pos = viewHolder.getAdapterPosition();
                Intent intent = new Intent(v.getContext(),UserShowActivity.class);
                intent.putExtra("ID",users.get(pos).getObjectId());
                v.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

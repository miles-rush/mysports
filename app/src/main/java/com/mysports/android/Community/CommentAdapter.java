package com.mysports.android.Community;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mysports.android.R;
import com.mysports.android.SmallActivity.PostItemActivity;
import com.mysports.android.bomb.Comment;
import com.mysports.android.bomb.Post;
import com.mysports.android.bomb.User;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> comments;
    private PostItemActivity content;

    public CommentAdapter(List<Comment> list,PostItemActivity content) {
        comments = list;
        this.content = content;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView content;
        TextView who;
        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.comment_item_name);
            time = view.findViewById(R.id.comment_item_time);
            content = view.findViewById(R.id.comment_item_content);
            who = view.findViewById(R.id.to_who);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Comment comment = comments.get(i);
        Log.d("comment", "onBindViewHolder: "+comment.getUser().getUsername());
        viewHolder.name.setText(comment.getUser().getUsername());
        viewHolder.time.setText(comment.getCreatedAt());
        viewHolder.content.setText(comment.getContent());
        if (comment.getToWho() != null) {
            viewHolder.who.setText(comment.getToWho());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        //评论点击事件处理
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText inputServer = new EditText(v.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("回复评论").setView(inputServer)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = inputServer.getText().toString().trim();
                        if (text.length() > 0) {
                            int pos = holder.getAdapterPosition();
                            Comment comment = comments.get(pos);
                            Post post = new Post();
                            post.setObjectId(comment.getPost().getObjectId());
                            User user = new User();
                            user.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());

                            Comment commentNew = new Comment();
                            commentNew.setUser(user);
                            commentNew.setPost(post);
                            commentNew.setContent(text);
                            commentNew.setToWho("To:"+comment.getUser().getUsername());

                            commentNew.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        Log.d("comment", "done: "+"success");
                                        Toast.makeText(content,"回复成功",Toast.LENGTH_SHORT).show();
                                        content.downloadComments();
                                    }else {
                                        Log.d("comment", "done: "+"error");
                                        Toast.makeText(content,"回复失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            return;
                        }
                    }
                }).show();
            }
        });
        return holder;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

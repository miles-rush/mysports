package com.mysports.android.Community;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mysports.android.R;
import com.mysports.android.SmallActivity.PostItemActivity;
import com.mysports.android.bomb.Community;
import com.mysports.android.bomb.Post;
import com.mysports.android.media.GlideUtil;
import com.mysports.android.media.ScaleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.util.V;

public class CommunityItemAdapter extends RecyclerView.Adapter<CommunityItemAdapter.ViewHolder>{

    private List<Post> itemlist;

    public void setList(List<Post> list) {
        this.itemlist = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView text;
        TextView author;
        //TextView date;
        TextView click;
        ImageView postImage;
        CardView cardView;

        List<ImageView> imageList;


        public ViewHolder(View view) {
            super(view);
            itemView = view;
            text = view.findViewById(R.id.c_item_text);
            author = view.findViewById(R.id.c_item_author);
            //date = view.findViewById(R.id.c_item_date);
            click = view.findViewById(R.id.c_item_click);
            postImage = view.findViewById(R.id.post_image); //主照片
            cardView = view.findViewById(R.id.post_item_card);

//            ImageView image1 = view.findViewById(R.id.item_image_1);
//            ImageView image2 = view.findViewById(R.id.item_image_2);
//            ImageView image3 = view.findViewById(R.id.item_image_3);
//            imageList = new ArrayList<ImageView>();
//            imageList.add(image1);
//            imageList.add(image2);
//            imageList.add(image3);

        }
    }
    public CommunityItemAdapter(List<Post> list) {
        itemlist = list;
    }
    private Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null) {
            context = viewGroup.getContext();
        }
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.community_item,viewGroup,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //TODO:这里添加社区界面2的点击事件
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = itemlist.get((Integer)v.getTag());
                String postID = post.getObjectId(); //动态的ID
                Intent intent = new Intent(v.getContext(),PostItemActivity.class);
                intent.putExtra("ID",postID);
                v.getContext().startActivity(intent);
                //Snackbar.make(v,"暂时只显示部分数据:"+post.getContent(),Snackbar.LENGTH_LONG).show();
            }
        });
        return viewHolder;
    }
    private double radio;
    private LinearLayout.LayoutParams rlp;
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(i);
        Post post = itemlist.get(i);

        viewHolder.text.setText(post.getContent().trim());
        //viewHolder.date.setText(post.getCreatedAt().trim());
        viewHolder.author.setText(post.getAuthor().getUsername());
        viewHolder.click.setText(" "+post.getPageView());

        viewHolder.postImage.setImageDrawable(null); //重置

        //final List<String> imagePath = post.getPics();


        //对新数据和旧数据的差异处理
        String url = "";
        if (post.getPicUrl() != null) {
            url = post.getPicUrl();
        }else {
            url = post.getPics().get(0);
        }


        rlp = (LinearLayout.LayoutParams) viewHolder.postImage.getLayoutParams();
        radio = 0;
        if (!url.equals("")) {
//            Glide.with(context).load(imagePath.get(0)).asBitmap().into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    radio = (resource.getHeight()*1.0) / resource.getWidth();
//                    rlp.width = viewHolder.postImage.getWidth();
//                    rlp.height = (int) (viewHolder.postImage.getWidth() * radio);
//                    Log.d("计算高宽", ""+rlp.height+" "+rlp.width);
//                    viewHolder.postImage.setLayoutParams(rlp);
//                    ViewGroup.LayoutParams params = viewHolder.postImage.getLayoutParams();
//                    params.width = rlp.width;
//                    params.height = rlp.height;
//                    viewHolder.postImage.setLayoutParams(params);
//                    Glide.with(context).load(imagePath.get(0)).override(rlp.width,rlp.height).into(viewHolder.postImage);
//                    //GlideUtil.initImageWithFileCache(context,imagePath.get(0),viewHolder.postImage);
//                    Log.d("图片高宽", ""+resource.getHeight()+" "+resource.getWidth());
//                    Log.d("图框高宽", ""+viewHolder.postImage.getHeight()+" "+viewHolder.postImage.getWidth());
//                }
//            });
            GlideUtil.initImageWithFileCache(context,url,viewHolder.postImage);

        }

    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }
}

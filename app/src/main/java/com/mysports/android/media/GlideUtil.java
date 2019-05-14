package com.mysports.android.media;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class GlideUtil {
    public static void initImageWithFileCache(Context context, String url, ImageView imageView){
        Glide.with(context)
                .load(url)

                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

                .into(imageView);
    }
    public static void initImageNoCache(Context context, String url, ImageView imageView){
        Glide.with(context)
                .load(url)
                .skipMemoryCache(true)
                .dontAnimate()
                .centerCrop()
                .into(imageView);
    }
    public static void clearMemoryCache(Context context){
        Glide.get(context).clearMemory();
    }
    public static void clearFileCache(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }
}

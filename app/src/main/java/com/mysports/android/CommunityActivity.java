package com.mysports.android;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mysports.android.Community.CommunityFragmentAdapter;
import com.mysports.android.Community.CommunityItemAdapter;
import com.mysports.android.bomb.Community;
import com.mysports.android.bomb.Post;
import com.mysports.android.bomb.PostImage;
import com.mysports.android.bomb.User;
import com.mysports.android.fragment.PostFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class CommunityActivity extends AppCompatActivity {
    private EditText text;

    private Button release;

    private List<Post> postList;

    private RecyclerView itemRecyclerView;

    private LinearLayoutManager manager;

    private CommunityItemAdapter postItemAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Button addImage;

    private List<String> imagePaths;

    private ProgressBar progressBar;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<Fragment> fragments = new ArrayList<>();

    private String[] titles = new String[]{"关注","动态","附近","消息"};

    private CommunityFragmentAdapter pagerAdapter;

    private void init(){
//        text = findViewById(R.id.community_text);
//        release = findViewById(R.id.community_btn);
//        addImage = findViewById(R.id.add_image);
//
//        itemRecyclerView = findViewById(R.id.community_list);
//        itemRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//
//        progressBar = findViewById(R.id.progress_bar);
//
          postList = new ArrayList<Post>();
//        swipeRefreshLayout = findViewById(R.id.community_swipe_refresh);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorBlack);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshList();
//            }
//        });
//
//        ImageView image1 = findViewById(R.id.post_image_1);
//        ImageView image2 = findViewById(R.id.post_image_2);
//        ImageView image3 = findViewById(R.id.post_image_3);
//        imageList = new ArrayList<ImageView>();
//        imageList.add(image1);
//        imageList.add(image2);
//        imageList.add(image3);
//
//        imagePaths = new ArrayList<String>();

        tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpage);
//        getDynamic();
//        PostFragment postFragment = new PostFragment();
//        postFragment.setList(postList);
        fragments.add(new Fragment());
        fragments.add(new PostFragment());
        fragments.add(new Fragment());
        fragments.add(new Fragment());

        pagerAdapter = new CommunityFragmentAdapter(fragments,titles,getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void refreshList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    getDynamic();
                    Thread.sleep(5000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postItemAdapter.setList(postList);
                        postItemAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    private void getDynamic() {
        BmobQuery<Post> bmobQuery = new BmobQuery<Post>();
        bmobQuery.include("author");
        bmobQuery.setLimit(50);
        bmobQuery.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    Toast.makeText(CommunityActivity.this,"加载数据:"+list.size()+"条",Toast.LENGTH_LONG).show();
                    if (postList.size() > 0) {
                        postList.clear();
                    }

                    for (final Post post : list) {

                        Post p = new Post();
                        p.setObjectId(post.getObjectId());
                        BmobQuery<PostImage> imageQuery = new BmobQuery<PostImage>();
                        imageQuery.addWhereEqualTo("post",new BmobPointer(p));
                        imageQuery.findObjects(new FindListener<PostImage>() {
                            @Override
                            public void done(List<PostImage> list, BmobException e) {
                                for (PostImage postImage:list) {
                                    final BmobFile bmobFile = postImage.getPic();
                                    if (bmobFile!=null) {
                                        post.getPics().add(bmobFile.getFileUrl());
                                    }
                                }
                            }
                        });

                        //加载图片后存入
                        postList.add(post);
                        Log.d("info", "done: "+post.getContent());
                    }
                    Collections.reverse(postList);
                    Log.d("info", "done: "+postList.size());
                }else {
                    Toast.makeText(CommunityActivity.this,"发生错误:"+e.getMessage(),Toast.LENGTH_LONG).show();
                    Log.d("message", "done: "+e.getMessage());
                }
            }
        });
    }

    private void setList() {
        getDynamic();
        manager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(manager);
        postItemAdapter = new CommunityItemAdapter(postList);
        itemRecyclerView.setAdapter(postItemAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_community);

        init();
//
//        setList();
//        release.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                if (text.getText().toString().length() > 0) {
//                    Post post = new Post();
//                    post.setContent(text.getText().toString());
//                    post.setPageView(0);
//                    post.setAuthor(BmobUser.getCurrentUser(User.class));
//
//                    post.save(new SaveListener<String>() {
//                        @Override
//                        public void done(final String s, BmobException e) {
//                            if (e == null) {
//                                for (String path:imagePaths) {
//                                    final BmobFile pic = new BmobFile(new File(path));
//                                    Log.d("path", "done: "+path);
//                                    pic.uploadblock(new UploadFileListener() {
//                                        @Override
//                                        public void done(BmobException e) {
//                                            if (e == null) {
//                                                Post p = new Post();
//                                                p.setObjectId(s);
//                                                PostImage postImage = new PostImage();
//                                                postImage.setPic(pic);
//                                                postImage.setPost(p);
//                                                postImage.save(new SaveListener<String>() {
//                                                    @Override
//                                                    public void done(String s, BmobException e) {
//                                                        if (e != null){
//                                                            Toast.makeText(CommunityActivity.this,"图片关联失败"+e.getMessage(),Toast.LENGTH_LONG).show();
//                                                        }
//                                                    }
//                                                });
//                                            }else {
//                                                Toast.makeText(CommunityActivity.this,"图片上传失败",Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//
//                                }
//                                Snackbar.make(v,"动态已发布",Snackbar.LENGTH_LONG).show();
//                                for (ImageView imageView:imageList) {
//                                    imageView.setImageDrawable(null);
//                                }
//                                refreshList();
//                            }else {
//                                Snackbar.make(v,"发布失败"+e.getMessage(),Snackbar.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//
//                    swipeRefreshLayout.setRefreshing(true);
//                    getDynamic();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            postItemAdapter.notifyDataSetChanged();
//                        }
//                    });
//                    swipeRefreshLayout.setRefreshing(false);
//                }else {
//                    Snackbar.make(v,"尚未添加内容",Snackbar.LENGTH_LONG).show();
//                }
//
//            }
//        });
//
//        addImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Boolean can = false;
//                for (ImageView view:imageList) {
//                    if (view.getDrawable()==null){
//                        can = true;
//                    }
//                }
//                if (can) {
//                    openAlbum();
//                }else {
//                    Toast.makeText(CommunityActivity.this,"最多选择三张图片",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }


    public static final int CHOOSE_PHTOT = 2;

    private Uri imageUri;

    private List<ImageView> imageList;

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHTOT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
//            case TAKE_PHTOT:
//                if (resultCode == RESULT_OK){
//                    try{
//                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        picture.setImageBitmap(bitmap);
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
//                }
//                break;
            case CHOOSE_PHTOT:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);

                    }else {
                        handleImageBeforeKitKat(data);
                    }
                    break;
                }
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {

            for (ImageView imageView:imageList) {
                if (imageView.getDrawable() == null){
                    imagePaths.add(imagePath);
                    Glide.with(this).load(imagePath).override(240,240).into(imageView);
                    break;
                }
            }
            //Glide.with(this).load(imagePath).override(240,240).into(image1);
            //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            //image1.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    //图片的按钮时间
    public void home(View view) {
        this.finish();
    }

    public void  writePost(View view) {
        Intent intent = new Intent(CommunityActivity.this,MainActivity.class);
        startActivity(intent);
    }
}

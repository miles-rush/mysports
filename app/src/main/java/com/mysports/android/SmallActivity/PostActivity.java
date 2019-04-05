package com.mysports.android.SmallActivity;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.bumptech.glide.Glide;
import com.mysports.android.MainActivity;
import com.mysports.android.R;
import com.mysports.android.bomb.Post;
import com.mysports.android.bomb.PostImage;
import com.mysports.android.bomb.User;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PostActivity extends AppCompatActivity implements AMapLocationListener{
    private EditText content;
    //private ImageButton addPhoto;
    private ImageView addPhoto;
    private ImageView postImage;
    private TextView location;
    private TextView send; //发布
    private String imagePath; //添加照片的路径
    private String locationText;
    private ImageView refresh;
    final private String [] choose = {"拍照","相册","取消"};
    private void init() {
        content = findViewById(R.id.community_text); //文本编辑框
        addPhoto = findViewById(R.id.add_photo); //添加照片
        location = findViewById(R.id.location_text); //定位信息
        postImage = findViewById(R.id.send_post_image);
        send = findViewById(R.id.send_post); //发布动态
        refresh = findViewById(R.id.image_refresh); //刷新定位
        imagePath = new String();
        imagePath = null;
        locationText = null;
        isLocation = false;
        locationText = "定位中...";
        location.setText(locationText);
        //开启定位
        getLoaction();
        //定位信息


        //添加照片事件
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder listDialog = new AlertDialog.Builder(PostActivity.this);
                listDialog.setItems(choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                openAlbum();
                                break;
                            case 2:
                                return;
                                default:
                                    break;
                        }
                    }
                });
                listDialog.show();
            }
        });
        //照片删除事件
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(PostActivity.this);
                dialog.setTitle("");
                dialog.setMessage("确认删除当前照片?");
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postImage.setImageDrawable(null);
                        postImage.setVisibility(View.GONE);
                        addPhoto.setVisibility(View.VISIBLE);
                        imagePath = null;
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.show();
            }
        });
        //发布
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (content.getText().toString().trim().length() > 0) {
                    if (imagePath == null) {
                        Snackbar.make(v,"你还没有添加照片哦...",Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    //上传数据-初始数据填入
                    Post post = new Post();
                    post.setContent(content.getText().toString().trim());
                    post.setPageView(0);
                    post.setAuthor(BmobUser.getCurrentUser(User.class));
                    post.save(new SaveListener<String>() {
                        @Override
                        public void done(final String s, BmobException e) {
                            if (e == null){
                                final BmobFile pic = new BmobFile(new File(imagePath));
                                pic.uploadblock(new UploadFileListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
                                            //照片上传后进行关联
                                            Post p = new Post();
                                            p.setObjectId(s);
                                            PostImage postImage = new PostImage();
                                            postImage.setPic(pic);
                                            postImage.setPost(p);
                                            //关联后上传数据
                                            postImage.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e != null){
                                                        Toast.makeText(PostActivity.this,"图片关联失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            Toast.makeText(PostActivity.this,"图片上传失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                Toast.makeText(PostActivity.this,"动态已经发布",Toast.LENGTH_LONG).show();
                                imagePath = null; //发布动态后清除数据
                                //回到社区
                                finish();
                            }else {
                                Log.d("动态发布失败", "done: "+e.getMessage());
                                Snackbar.make(v,"动态发布失败"+e.getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Snackbar.make(v,"你还没有添加内容呢...",Snackbar.LENGTH_LONG).show();
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Toast.makeText(PostActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //Bmob.initialize(this,"4ba682d489877d786b2139b0db2e4af9");
        setContentView(R.layout.activity_post);
        init();
    }

    //返回到社区
    public void imageFinish(View view) {
        if (content.getText().toString().trim().length() == 0 && imagePath == null) {
            this.finish();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(PostActivity.this);
            dialog.setTitle("");
            dialog.setMessage("你当前还有未发布的内容,确认退出吗?");
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            dialog.show();
        }

    }

    public static final int TAKE_PHTOT = 1;
    public static final int CHOOSE_PHTOT = 2;
    private Uri imageUri;
    private String takePhotoPath;
    //相册照片处理
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHTOT);
    }
    //使用当前时间作为拍摄照片的文件名
    private String getTimeString() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH))+1;
        String day = String.valueOf(cal.get(Calendar.DATE));
        String hour = "";
        if (cal.get(Calendar.AM_PM) == 0)
            hour = String.valueOf(cal.get(Calendar.HOUR));
        else
            hour = String.valueOf(cal.get(Calendar.HOUR)+12);
        String minute = String.valueOf(cal.get(Calendar.MINUTE));
        String second = String.valueOf(cal.get(Calendar.SECOND));
        return year+month+day+hour+minute+second;
    }
    //拍照
    private void takePhoto() {
        File outputImage = new File(getExternalCacheDir(),getTimeString());
        takePhotoPath = outputImage.getPath();
        try{
            if(outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(PostActivity.this,
                    "com.example.cameraalbumtest.fileprovider",outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHTOT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHTOT:
                if (resultCode == RESULT_OK){
                    Log.d("url", "onActivityResult: "+imageUri);
                    if (takePhotoPath != null){
                        Glide.with(PostActivity.this).load(takePhotoPath).override(235,235).into(postImage);
                        addPhoto.setVisibility(View.GONE);
                        postImage.setVisibility(View.VISIBLE);
                        imagePath = takePhotoPath; //确认照片拍摄成功后
                    }
//                    try{
//                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        postImage.setImageBitmap(bitmap);
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
                }
                break;
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
            this.imagePath = imagePath;
            //Glide.with(this).load(imagePath).into(addPhoto);
            Glide.with(this).load(imagePath).override(235,235).into(postImage);
            addPhoto.setVisibility(View.GONE);
            postImage.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }




    AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //更新定位信息文本
    public void getLoaction() {
        setLocation();
//        location.setText(locationText);
//        mLocationClient.stopLocation();
    }
    //定位参数设置
    public void setLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        // 同时使用网络定位和GPS定位,优先返回最高精度的定位结果,以及对应的地址描述信息
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。默认连续定位 切最低时间间隔为1000ms
        //mLocationOption.setInterval(5000);
        mLocationOption.setOnceLocation(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //关闭缓存机制 默认开启 ，在高精度模式和低功耗模式下进行的网络定位结果均会生成本地缓存,不区分单次定位还是连续定位。GPS定位结果不会被缓存。
        /*mLocationOption.setLocationCacheEnable(false);*/
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stopLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mLocationClient.stopLocation();
    }

    private boolean isLocation;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation!=null){
            if (aMapLocation.getErrorCode() ==0) {
                Log.d("location", "定位成功");
                String city = aMapLocation.getCity(); //城市信息
                String district = aMapLocation.getDistrict(); //城区信息
                String street = aMapLocation.getStreet(); //街道信息
                locationText = city + district + street;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        location.setText(locationText);
                    }
                });


            }else {
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }



}

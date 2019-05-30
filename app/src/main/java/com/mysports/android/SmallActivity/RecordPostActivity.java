package com.mysports.android.SmallActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mysports.android.R;
import com.mysports.android.bomb.Record;
import com.mysports.android.bomb.User;
import com.mysports.android.map.DbAdapter;
import com.mysports.android.map.PathRecord;
import com.mysports.android.map.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

//运动记录分享
public class RecordPostActivity extends AppCompatActivity implements AMapLocationListener {
    private TextView showTop;
    private TextView showEnd;
    private EditText editText;

    private TextView post;
    private TextView location;
    private PathRecord pathRecord;
    private int recordID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_record_post);

        init();

        final Record record = changeToRecord(pathRecord);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (record == null) {
                    Toast.makeText(RecordPostActivity.this,"运动记录读取错误",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    String text = editText.getText().toString().trim();
                    record.setText(text);
                    record.setAuthor(BmobUser.getCurrentUser(User.class));
                    record.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Toast.makeText(RecordPostActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(RecordPostActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        location = findViewById(R.id.record_location_text);
        locationText = "定位中...";
        location.setText(locationText);
        getLoaction();
    }

    private void init() {
        showTop = (TextView) findViewById(R.id.post_text_top);
        showEnd = (TextView) findViewById(R.id.post_text_end);

        editText = (EditText) findViewById(R.id.record_community_text);
        post = (TextView) findViewById(R.id.send_record_post);

        getPathRecord();
        setData();
    }

    //根据传入的ID获取数据库中的pathrecord
    private void getPathRecord() {
        Intent intent = getIntent();
        recordID = intent.getIntExtra("record_id",0);
        DbAdapter dbAdapter = new DbAdapter(this.getApplicationContext());
        dbAdapter.open();
        pathRecord = dbAdapter.queryRecordById((int)recordID);
        dbAdapter.close();
    }
    //显示部分运动数据信息
    private void setData() {
        if (pathRecord == null) {
            return;
        }
        //距离获取
        String distantText = "0";
        float distanceFloat = 0;
        int distance = 0; //m
        if (pathRecord.getDistance() != null) {
            distantText = pathRecord.getDistance().trim();
            distanceFloat = Float.parseFloat(distantText); //距离
            distance = (int)distanceFloat;
        }
        DecimalFormat distanceFormat = new DecimalFormat("0.00"); //距离格式
        //时间获取
        String timeText = "";
        float timeFloat = 0;
        int time = 0; //s
        if (pathRecord.getDuration() != null){
            timeText = pathRecord.getDuration().trim();
            timeFloat = Float.parseFloat(timeText);
            time = (int)timeFloat;
        }


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        double km = 1.0* distance / 1000; //公里数
        double kmTime = 1.0 * time / km;

        //特殊情况出现时间为0时的处理
        if (time == 0) {
            time = 1;
        }

        showTop.setText("完成 "+distanceFormat.format(1.0*distance/1000)+"公里跑 时长:"
                            +sdf.format(time*1000-8*60*60*1000));
        showEnd.setText("每公里耗时: "+format.format(kmTime*1000));


    }
    //转化pathrecord到可以保存在云服务器上的数据类型
    private Record changeToRecord(PathRecord pathRecord) {
        if (pathRecord == null){
            return null;
        }
        Record record = new Record();
        record.setDate(pathRecord.getDate());
        record.setDistance(pathRecord.getDistance());
        record.setDuration(pathRecord.getDuration());
        record.setSpeed(pathRecord.getAveragespeed());

        record.setLocations(Util.getPathLineString(pathRecord.getPathline()));
        record.setStartPoint(Util.amapLocationToString(pathRecord.getStartpoint()));
        record.setEndPoint(Util.amapLocationToString(pathRecord.getEndpoint()));
        return record;
    }

    private String locationText;
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

    public void recordFinish(View view) {
        this.finish();
    }
}

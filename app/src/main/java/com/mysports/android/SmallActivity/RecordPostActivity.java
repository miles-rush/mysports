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
public class RecordPostActivity extends AppCompatActivity {
    private TextView showTop;
    private TextView showEnd;
    private EditText editText;

    private TextView post;

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








    public void recordFinish(View view) {
        this.finish();
    }
}

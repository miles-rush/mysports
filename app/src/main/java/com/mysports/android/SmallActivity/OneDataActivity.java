package com.mysports.android.SmallActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.DescendantOffsetUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysports.android.R;
import com.mysports.android.map.DbAdapter;
import com.mysports.android.map.PathRecord;
import com.mysports.android.util.HttpUtil;


import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OneDataActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView backgroundPic;
    private TextView timeText; //本次运动的结束时间
    private TextView distanceText;
    private TextView costTimeText;
    private TextView kmSpeedText;
    private TextView avgSpeedText;
    private TextView calCostText;
    private TextView quoteText;
    private TextView quoteAuthorText;

    private String time;

    private void init() {
        //组件初始化
        drawerLayout = (DrawerLayout) findViewById(R.id.exercise_drawerlayout);
        backgroundPic = (ImageView) findViewById(R.id.one_data_pic);
        timeText = (TextView) findViewById(R.id.exercise_end_time);
        distanceText = (TextView) findViewById(R.id.distance_text);
        costTimeText = (TextView) findViewById(R.id.costtime_text);
        kmSpeedText = (TextView) findViewById(R.id.km_speed_text);
        avgSpeedText = (TextView) findViewById(R.id.avg_speed_text);
        calCostText = (TextView) findViewById(R.id.cost_cal_text);
        quoteText = (TextView) findViewById(R.id.quote_text);
        quoteAuthorText = (TextView) findViewById(R.id.quote_author);
        //数据加载
        loadQuote(); //加载句子
        setDate();
        setRecordText();
        //setQuote();

        drawerLayout.setVisibility(View.VISIBLE);

        loadBingPic(); //加载背景图
    }
    private void setQuote() {
        quoteText.setText(""+quote);
        quoteAuthorText.setText(""+author);
    }
    private int recordID;
    private PathRecord record; //传入的记录
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_one_data);

        Intent intent = getIntent();
        recordID = intent.getIntExtra("record_id",0);
        Log.d("idididid", ""+recordID);
        DbAdapter dbAdapter = new DbAdapter(this.getApplicationContext());
        dbAdapter.open();
        record = dbAdapter.queryRecordById((int)recordID);

        init();

    }

    private void setRecordText() {
        if (record == null){
            return;
        }
        //距离获取
        String distantText = "0";
        float distanceFloat = 0;
        int distance = 0; //m
        if (record.getDistance() != null) {
            distantText = record.getDistance().trim();
            distanceFloat = Float.parseFloat(distantText); //距离
            distance = (int)distanceFloat;
        }
        DecimalFormat distanceFormat = new DecimalFormat("0.0"); //距离格式
        //时间获取
        String timeText = "";
        float timeFloat = 0;
        int time = 0; //s
        if (record.getDuration() != null){
            timeText = record.getDuration().trim();
            timeFloat = Float.parseFloat(timeText);
            time = (int)timeFloat;
            Log.d("idididid", ""+time+"s");
        }


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        double km = 1.0* distance / 1000; //公里数
        double kmTime = 1.0 * time / km;
        int kcal = (int)kcal(70,distance);

        double avgSpeed = 1.0 * distance / time;
        Log.d("idididid", ""+distance+"m");
        Log.d("idididid", ""+avgSpeed+"m/s");

        distanceText.setText(distanceFormat.format(distance));
        costTimeText.setText(sdf.format(time*1000-8*60*60*1000)); //修正安卓手机的时间误差
        kmSpeedText.setText(format.format(kmTime*1000));
        avgSpeedText.setText(distanceFormat.format(avgSpeed));
        calCostText.setText(kcal + "千卡");
    }

    private double kcal(float weight,float distance) {
        return weight*1.036*distance/1000; //kcal
    }
    //时间设置
    private  void setDate() {
        Calendar c = Calendar.getInstance();
        int hour =  c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if (minute < 10){
            time = hour + ":0" + minute;
        }else {
            time = hour + ":" + minute;
        }
        timeText.setText(time);
    }
    //加载随机背景图片
    private void loadBingPic() {
        final String bingPic = "https://uploadbeta.com/api/pictures/random/?key=BingEverydayWallpaperPicture";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(OneDataActivity.this).load(bingPic).into(backgroundPic);
            }
        });
    }

    //每日一句
    private String quote;
    private String author;
    private void loadQuote() {
        String urlText = "https://v1.hitokoto.cn/?c=g&encode=text";
        String urlJson = "https://v1.hitokoto.cn/?c=g";
        HttpUtil.sendOkHttpRequest(urlJson, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resultText = response.body().string();
                Log.d("quote", resultText);
                parseJSONData(resultText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setQuote();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });

    }
    //解析数据 获得内容和作者
    private void parseJSONData(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            String content = jsonObject.getString("hitokoto");
            String author = jsonObject.getString("from");
            Log.d("quote", content + " "+ author);
            quote = content;
            this.author = author;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

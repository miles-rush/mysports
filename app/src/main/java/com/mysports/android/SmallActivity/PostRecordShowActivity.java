package com.mysports.android.SmallActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.mysports.android.R;
import com.mysports.android.RecordListActivity;
import com.mysports.android.bomb.Record;
import com.mysports.android.map.DbAdapter;
import com.mysports.android.map.PathRecord;
import com.mysports.android.map.PathSmoothTool;
import com.mysports.android.map.TraceRePlay;
import com.mysports.android.map.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

//显示 上传的运动记录 的轨迹
public class PostRecordShowActivity extends AppCompatActivity implements
        AMap.OnMapLoadedListener, TraceListener  {
    private MapView mapView;

    private TextView name;
    private TextView time;
    private TextView top;
    private TextView end;
    private TextView content;
    private TextView goods;
    private ImageView addGoods;

    private String ID;

    private Record downloadRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post_record_show);
        mapView = findViewById(R.id.apr_map);
        mapView.onCreate(savedInstanceState);

        ID = getIntent().getStringExtra("ID");
        init();

        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 3;
        mThreadPool = Executors.newFixedThreadPool(threadPoolSize);

        initMap();
        loadRecord();

    }

    //TODO:将record转化为pathrecord
    private void chengeRecord() {
        mRecord = new PathRecord();
        mRecord.setPathline(Util.parseLocations(downloadRecord.getLocations()));
        mRecord.setStartpoint(Util.parseLocation(downloadRecord.getStartPoint()));
        mRecord.setEndpoint(Util.parseLocation(downloadRecord.getEndPoint()));
    }
    public void onDestroy() {
        super.onDestroy();
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
            mAMap.setOnMapLoadedListener(this);
        }
    }

    private void loadRecord() {
        BmobQuery<Record> query = new BmobQuery<>();
        query.include("author");
        query.getObject(ID, new QueryListener<Record>() {
            @Override
            public void done(Record record, BmobException e) {
                if (e == null) {
                    downloadRecord = record;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setData();
                        }
                    });
                    goodNum = record.getGoods(); //设置当前点赞数
                    chengeRecord();
                    setupRecord();
                }else {
                    Toast.makeText(getApplicationContext(),"加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int goodNum = 0;
    private void init() {
        name = findViewById(R.id.apr_name);
        time = findViewById(R.id.apr_time);
        top = findViewById(R.id.apr_top);
        end = findViewById(R.id.apr_end);

        content = findViewById(R.id.apr_content);
        goods = findViewById(R.id.apr_goods);
        addGoods = findViewById(R.id.apr_add_good);

        //点赞操作
        addGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodNum += 1;
                downloadRecord.setGoods(goodNum);
                downloadRecord.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(),"点赞成功",Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    goods.setText(""+goodNum);
                                }
                            });
                        }else {
                            goodNum -= 1;
                            Toast.makeText(getApplicationContext(),"点赞失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void setData() {
        DecimalFormat distanceFormat = new DecimalFormat("0.00"); //距离格式
        SimpleDateFormat timeFormatTop = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat timeFormatEnd = new SimpleDateFormat("mm:ss");

        name.setText(downloadRecord.getAuthor().getUsername());
        time.setText(downloadRecord.getCreatedAt());
        String sDistance = downloadRecord.getDistance();
        String sTime = downloadRecord.getDuration();
        double distance = Double.parseDouble(sDistance);
        double time = Double.parseDouble(sTime);
        double km = 1.0* distance / 1000; //公里数
        double kmTime = 1.0 * time / km;

        top.setText("完成 "+distanceFormat.format(distance*1.0/1000)+
                "公里跑  时长:"+timeFormatTop.format(time*1000-8*60*60*1000));

        end.setText("每公里耗时: "+timeFormatEnd.format(kmTime*1000));

        content.setText(downloadRecord.getText());
        goods.setText(downloadRecord.getGoods()+"");
    }

    private List<LatLng> mOriginLatLngList;
    private PathRecord mRecord; //TODO:将下载的record转化为pathrecord
    private AMap mAMap;
    private Marker mOriginStartMarker, mOriginEndMarker, mOriginRoleMarker;
    private Marker mGraspStartMarker, mGraspEndMarker, mGraspRoleMarker;
    private Polyline mOriginPolyline, mGraspPolyline;

    private int mRecordItemId;
    private List<LatLng> mGraspLatLngList;
    private boolean mGraspChecked = false;
    private boolean mOriginChecked = true;
    private ExecutorService mThreadPool;
    private TraceRePlay mRePlay;

    private void setupRecord() {
        // 轨迹纠偏初始化
        LBSTraceClient mTraceClient = new LBSTraceClient(
                getApplicationContext());
        if (mRecord != null) {
            List<AMapLocation> recordList = mRecord.getPathline();
            AMapLocation startLoc = mRecord.getStartpoint();
            AMapLocation endLoc = mRecord.getEndpoint();
            if (recordList == null || startLoc == null || endLoc == null) {
                return;
            }
            LatLng startLatLng = new LatLng(startLoc.getLatitude(),
                    startLoc.getLongitude());
            LatLng endLatLng = new LatLng(endLoc.getLatitude(),
                    endLoc.getLongitude());
            mOriginLatLngList = Util.parseLatLngList(recordList);
            addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);

            List<TraceLocation> mGraspTraceLocationList = Util
                    .parseTraceLocationList(recordList);
            // 调用轨迹纠偏，将mGraspTraceLocationList进行轨迹纠偏处理
            mTraceClient.queryProcessedTrace(1, mGraspTraceLocationList,
                    LBSTraceClient.TYPE_AMAP, this);
        } else {
        }

    }

    private void addOriginTrace(LatLng startPoint, LatLng endPoint,
                                List<LatLng> originList) {
        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(
                Color.BLUE).addAll(pathOptimize(originList))); //对原始轨迹平滑处理

        mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.start)));
        mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(
                endPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.end)));

        try {
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(),
                    50));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mOriginRoleMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.walk))));
    }

    public List<LatLng> pathOptimize(List<LatLng> originlist){
        PathSmoothTool mpathSmoothTool = new PathSmoothTool();
        List<LatLng> pathoptimizeList = mpathSmoothTool.pathOptimize(originlist);
//        mkalmanPolyline = mAMap.addPolyline(
//                new PolylineOptions().addAll(pathoptimizeList).color(Color.parseColor("#FFC125")));
        return pathoptimizeList;
    }


    private LatLngBounds getBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (mOriginLatLngList == null) {
            return b.build();
        }
        for (int i = 0; i < mOriginLatLngList.size(); i++) {
            b.include(mOriginLatLngList.get(i));
        }
        return b.build();

    }

    private final static int AMAP_LOADED = 2;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AMAP_LOADED:
                    setupRecord();
                    break;
                default:
                    break;
            }
        }

    };
    @Override
    public void onMapLoaded() {
        Message msg = handler.obtainMessage();
        msg.what = AMAP_LOADED;
        handler.sendMessage(msg);
    }

    /**
     * 轨迹纠偏完成数据回调
     */
    @Override
    public void onFinished(int arg0, List<LatLng> list, int arg2, int arg3) {
        addGraspTrace(list, mGraspChecked);
        mGraspLatLngList = list;
    }

    @Override
    public void onRequestFailed(int arg0, String arg1) { //arg1 返回错误信息
        Toast.makeText(this.getApplicationContext(), "骑行轨迹优化失败",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTraceProcessing(int arg0, int arg1, List<LatLng> arg2) {

    }


    private void addGraspTrace(List<LatLng> graspList, boolean mGraspChecked) {
        if (graspList == null || graspList.size() < 2) {
            return;
        }
        LatLng startPoint = graspList.get(0);
        LatLng endPoint = graspList.get(graspList.size() - 1);
        mGraspPolyline = mAMap.addPolyline(new PolylineOptions()
                .setCustomTexture(
                        BitmapDescriptorFactory
                                .fromResource(R.drawable.grasp_trace_line))
                .width(40).addAll(graspList));
        mGraspStartMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.start)));
        mGraspEndMarker = mAMap.addMarker(new MarkerOptions()
                .position(endPoint).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.end)));
        mGraspRoleMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.walk))));
        if (!mGraspChecked) {
            mGraspPolyline.setVisible(false);
            mGraspStartMarker.setVisible(false);
            mGraspEndMarker.setVisible(false);
            mGraspRoleMarker.setVisible(false);
        }
    }


}

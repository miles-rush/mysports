package com.mysports.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.mysports.android.map.DbAdapter;
import com.mysports.android.map.PathRecord;
import com.mysports.android.map.PathSmoothTool;
import com.mysports.android.map.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordActivity extends AppCompatActivity implements LocationSource,AMapLocationListener,TraceListener {

    private final static int CALLTRACE = 0;
    private MapView mMapView;
    private AMap mAMap;
    private OnLocationChangedListener mListener;

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private PolylineOptions mPolyoptions, tracePolytion, smoothPolytion; //原始 纠偏 平滑
    private Polyline mpolyline;

    private PathRecord record;

    private long mStartTime;
    private long mEndTime;

    private ToggleButton btn;
    private DbAdapter DbHepler;

    private List<TraceLocation> mTracelocationlist = new ArrayList<TraceLocation>(); //每次清空
    private List<TraceOverlay> mOverlayList = new ArrayList<TraceOverlay>();
    private List<AMapLocation> recordList = new ArrayList<AMapLocation>();

    private int tracesize = 30;
    private int mDistance = 0;

    private TraceOverlay mTraceoverlay; //用于绘制轨迹纠偏接口回调的一条平滑轨迹
    private TextView mResultShow;
    private Marker mlocMarker;

    //悬浮显示数据
    private TextView speed; //速度
    private TextView hour; //运动时间
    private TextView cal; //卡路里消耗
    private TextView altitude; //海拔高度
    private TextView speed_hour; //每公里时速
    private double altitude_num;

    private Thread thread;
    private boolean threadFlag;

    private PathSmoothTool mpathSmoothTool;

    private CheckBox trachChooseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicmap_activity);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initpolyline();


    }


    public static  final int UPDATE_TEXT = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    mEndTime = System.currentTimeMillis();
                    //mOverlayList.add(mTraceoverlay);
                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
                    //LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
                    //mTraceClient.queryProcessedTrace(2, Util.parseTraceLocationList(record.getPathline()) , LBSTraceClient.TYPE_AMAP, RecordActivity.this);
                    float distance = getDistance(record.getPathline());
                    //刷新数据
                    mResultShow.setText("运动里程:"+ decimalFormat.format(distance) + "M");

                    hour.setText("时间:"+((mEndTime-mStartTime)/1000)+"S");
                    float speedn = distance/((mEndTime-mStartTime)/1000);
                    speed.setText("速度:"+decimalFormat.format(speedn)+"M/s");
                    cal.setText("卡路里消耗:"+decimalFormat.format(kcal(80,distance))+"kcal");
                    altitude.setText("海拔:"+altitude_num);
                    break;
                    default:
                        break;

            }
        }
    };

    private double kcal(float weight,float distance) {
        return weight*1.036*distance/1000; //kcal
    }

    //实时显示线程
    private class showThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (threadFlag) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = UPDATE_TEXT;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //部分初始化
    private void init() {

        mpathSmoothTool = new PathSmoothTool();
        mpathSmoothTool.setIntensity(4);

        trachChooseBtn = (CheckBox) findViewById(R.id.trace_choose);
        speed = (TextView) findViewById(R.id.speed);
        hour = (TextView) findViewById(R.id.hour);
        cal = (TextView) findViewById(R.id.cal);
        altitude = (TextView) findViewById(R.id.altitude);
        speed_hour = (TextView) findViewById(R.id.speed_hour);

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }

        btn = (ToggleButton) findViewById(R.id.locationbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.isChecked()) {
                    mAMap.clear(true);
                    if (record != null) {
                        record = null;
                    }
                    if (!mLocationClient.isStarted()) {
                        mLocationClient.startLocation();
                    }
                    //重启启动后对原有数据进行清空
                    mPolyoptions.getPoints().clear();
                    smoothPolytion.getPoints().clear();

                    //初始记录数据设置
                    record = new PathRecord();
                    mStartTime = System.currentTimeMillis();
                    record.setDate(getcueDate(mStartTime));
                    mResultShow.setText("运动里程:");

                    //启动线程
                    thread = new showThread();
                    threadFlag = true;
                    thread.start();

                } else {
                    if (mLocationClient.isStarted()) {
                        mLocationClient.stopLocation();
                    }
                    threadFlag = false; //关闭实时显示线程
                    thread.interrupt();

                    mEndTime = System.currentTimeMillis();
                    mOverlayList.add(mTraceoverlay);
                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
                    mResultShow.setText("运动里程"+ decimalFormat.format(getTotalDistance()) + "M");

                    if (trachChooseBtn.isChecked()) {
                        LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
                        //lineID=2
                        mTraceClient.queryProcessedTrace(2, Util.parseTraceLocationList(record.getPathline()) , LBSTraceClient.TYPE_AMAP, RecordActivity.this);
                    }

                    saveRecord(record.getPathline(), record.getDate());
                }
            }
        });

        mResultShow = (TextView) findViewById(R.id.show_all_dis);
        mTraceoverlay = new TraceOverlay(mAMap);

        trachChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"你已经开启骑行优化,请沿可见道路运动",Snackbar.LENGTH_LONG)
                        .setAction("关闭骑行", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                trachChooseBtn.setChecked(false);
                            }
                        }).show();
            }
        });
    }

    //存储记录到本机
    protected void saveRecord(List<AMapLocation> list, String time) {
        if (list != null && list.size() > 0) {
            DbHepler = new DbAdapter(this);
            DbHepler.open();
            String duration = getDuration();
            float distance = getDistance(list);
            String average = getAverage(distance);
            String pathlineSring = getPathLineString(list);
            AMapLocation firstLocaiton = list.get(0);
            AMapLocation lastLocaiton = list.get(list.size() - 1);
            String stratpoint = amapLocationToString(firstLocaiton);
            String endpoint = amapLocationToString(lastLocaiton);
            DbHepler.createrecord(String.valueOf(distance), duration, average,
                    pathlineSring, stratpoint, endpoint, time);
            DbHepler.close();
        } else {
            Toast.makeText(RecordActivity.this, "没有记录到路径", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    public void onBackClick(View view) {
        this.finish();
    }
    //数据计算
    //运动时间
    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
    }

    //速度
    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

    //里程
    private float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstpoint.getLatitude(),
                    firstpoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondpoint.getLatitude(),
                    secondpoint.getLongitude());
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    //路径信息
    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }

    //初始化折线选项对象
    private void initpolyline() {
        //原始路径
        mPolyoptions = new PolylineOptions();
//        mPolyoptions.width(20f);
//        mPolyoptions.color(Color.GRAY);
        mPolyoptions.width(10);
        mPolyoptions.color(Color.parseColor("#FFC125")); //混淆原始路径和平滑后路径

        //平滑
        smoothPolytion = new PolylineOptions();
        smoothPolytion.width(10);
        smoothPolytion.color(Color.parseColor("#FFC125"));

        //纠偏后路径
        tracePolytion = new PolylineOptions();
        tracePolytion.width(20);
        tracePolytion.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));
    }

    //Amap参数设置
    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        mAMap.setMyLocationStyle(myLocationStyle);
        //mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(18)); //缩放调整
    }


    //生命周期方法覆盖
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    //接口实现
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        startlocation();
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();

        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                LatLng mylocation = new LatLng(amapLocation.getLatitude(),
                        amapLocation.getLongitude()); //当前坐标
                altitude_num = amapLocation.getAltitude(); //更新当前海拔高度
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mylocation));
                //定位监听 按钮点击后录制坐标点
                if (btn.isChecked()) {
                    record.addpoint(amapLocation); //移动后记录点

                    mPolyoptions.add(mylocation); //LatLng坐标 当前坐标

                    mTracelocationlist.add(Util.parseTraceLocation(amapLocation)); //纠偏坐标集合
                    redrawline(); //原始轨迹
                    if (mTracelocationlist.size() > tracesize - 1) {
                        if (trachChooseBtn.isChecked()) {
                            trace(); //判断是否选中骑行优化
                        }
                        //trace(); //开始纠偏-实时
                    }
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private void startlocation() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            mLocationOption.setInterval(2000);

            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();

        }
    }


    //轨迹平滑优化
    public List<LatLng> pathOptimize(List<LatLng> originlist){
        List<LatLng> pathoptimizeList = mpathSmoothTool.pathOptimize(originlist);
//        mkalmanPolyline = mAMap.addPolyline(
//                new PolylineOptions().addAll(pathoptimizeList).color(Color.parseColor("#FFC125")));
        return pathoptimizeList;
    }

    //绘制直线
    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mPolyoptions.getPoints());  //mpolyline是LatLng坐标系
            } else {
                mpolyline = mAMap.addPolyline(mPolyoptions);
            }

            //平滑处理后轨迹
//            List<LatLng> list = mPolyoptions.getPoints();
//            //mpolyline.remove();
//            smoothPolytion.setPoints(pathOptimize(list));
//
//            if (mpolyline == null){
//                mpolyline = mAMap.addPolyline(smoothPolytion);
//            }else {
//                mpolyline.setPoints(smoothPolytion.getPoints());
//            }
//            smoothPolytion.visible(true);


        }

        if (mpolyline != null) {
            mpolyline.remove();
        }
        smoothPolytion.setPoints(pathOptimize(mPolyoptions.getPoints()));
        smoothPolytion.visible(true);
        mpolyline = mAMap.addPolyline(smoothPolytion);

        //未平滑处理
//		if (mpolyline != null) {
//			mpolyline.remove();
//		}
//		mPolyoptions.visible(true);
//		mpolyline = mAMap.addPolyline(mPolyoptions);

//		PolylineOptions newpoly = new PolylineOptions();
//		mpolyline = mAMap.addPolyline(newpoly.addAll(mPolyoptions.getPoints()));

    }

    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
    }

    //运动记录按钮跳转
    public void record(View view) {
        Intent intent = new Intent(RecordActivity.this, RecordListActivity.class);
        startActivity(intent);
    }

    //轨迹纠偏 自有轨迹
    private void trace() {
        List<TraceLocation> locationList = new ArrayList<>(mTracelocationlist);
        LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
        //lineID=1
        mTraceClient.queryProcessedTrace(1, locationList, LBSTraceClient.TYPE_AMAP, this);
        TraceLocation lastlocation = mTracelocationlist.get(mTracelocationlist.size()-1);
        mTracelocationlist.clear(); //纠偏轨迹列表清空操作
        mTracelocationlist.add(lastlocation);
    }

    //轨迹纠偏失败回调
    @Override
    public void onRequestFailed(int i, String s) {
        mOverlayList.add(mTraceoverlay);
        mTraceoverlay = new TraceOverlay(mAMap);
    }

    @Override
    public void onTraceProcessing(int i, int i1, List<LatLng> list) {

    }

    /**
     * 轨迹纠偏成功回调。
     * @param lineID 纠偏的线路ID
     * @param linepoints 纠偏结果
     * @param distance 总距离
     * @param waitingtime 等待时间
     */
    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance, int waitingtime) {
        if (lineID == 1) {
            if (linepoints != null && linepoints.size()>0) {
                mTraceoverlay.add(linepoints);
                mDistance += distance;
                mTraceoverlay.setDistance(mTraceoverlay.getDistance()+distance);
                //绘制标记 用于在地图上显示路径距离
                if (mlocMarker == null) {
                    mlocMarker = mAMap.addMarker(new MarkerOptions().position(linepoints.get(linepoints.size() - 1))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.point))
                            .title("距离：" + mDistance+"米"));
                    mlocMarker.showInfoWindow();
                } else {
                    mlocMarker.setTitle("距离：" + mDistance+"米");
                    Toast.makeText(RecordActivity.this, "距离"+mDistance, Toast.LENGTH_SHORT).show();
                    mlocMarker.setPosition(linepoints.get(linepoints.size() - 1));
                    mlocMarker.showInfoWindow();
                }
            }
        } else if (lineID == 2) {
            if (linepoints != null && linepoints.size()>0) {
                mAMap.addPolyline(new PolylineOptions()  //绘制折线
                        .color(Color.parseColor("#8470FF"))
                        .width(40).addAll(linepoints));
            }
        }

    }

    //总距离计算
    private int getTotalDistance() {
        int distance = 0;
        for (TraceOverlay to : mOverlayList) {
            distance = distance + to.getDistance();
        }
        return distance;
    }
}

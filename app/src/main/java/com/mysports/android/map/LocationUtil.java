package com.mysports.android.map;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.trace.LBSTraceBase;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil implements AMap.OnMyLocationChangeListener {
    private MapView mapView;

    private AMap aMap;

    private MyLocationStyle myLocationStyle;

    //构造方法
    public LocationUtil(MapView mapView) {
        this.mapView = mapView;
        aMap = mapView.getMap();
        myLocationStyle = new MyLocationStyle();
    }

    //定位蓝点
    public void requestLocation() {
        //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);
        //设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        //设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);
        //设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }


    @Override
    public void onMyLocationChange(Location location) {

    }

    public void Trace(Context context){

    }
}

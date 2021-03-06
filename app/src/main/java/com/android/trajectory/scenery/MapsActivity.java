package com.android.trajectory.scenery;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.services.core.LatLonPoint;
import com.android.trajectory.R;
import android.app.Activity;
import android.graphics.Point;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;//定位信息类
import com.amap.api.location.AMapLocationClient;//定位服务类
import com.amap.api.location.AMapLocationClientOption;//定位参数设置（在定位服务时需要这些参数）
import com.amap.api.location.AMapLocationListener;//定位回调接口
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.LatLng;


public class MapsActivity extends Activity implements LocationSource, AMapLocationListener {
    private LatLng pos;
    private int i = 0;
    private MyView myView;
    private double x;
    private double y;
    private float dot_x = 0;
    private float dot_y = 0;
    private FrameLayout act_main;
    private LatLonPoint lp = new LatLonPoint(39.993743, 116.472995);// 116.472995,39.993743
    static public boolean fog_draw_pause_judge = true;
    private Button btn;
    private Button stop_btn;
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private TextView mLocationErrText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btn = (Button) findViewById(R.id.Fog_btn);
        stop_btn = (Button) findViewById(R.id.Stop_Fog_btn);
        stop_btn.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fog
                act_main = (FrameLayout) findViewById(R.id.activity_main);
                myView = new MyView(MapsActivity.this);
                act_main.addView(myView);
                i = 0;//初始化计数器
                aMap.getUiSettings().setAllGesturesEnabled(false);//禁止所有手势操作
                btn.setVisibility(View.GONE);
                stop_btn.setVisibility(View.VISIBLE);
            }
        });
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act_main.removeView(myView);
                stop_btn.setVisibility(View.GONE);
                btn.setVisibility(View.VISIBLE);
            }
        });
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        init();
        //设置使用普通地图
        //aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
        //aMap.setMapType(AMap.MAP_TYPE_NORMAL);

    }
    //初始化AMap对象
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mLocationErrText = (TextView) findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);
    }

    /*
    设置一些amap的属性
     */
    private void setUpMap() {
        aMap.getUiSettings().setRotateGesturesEnabled(false);//禁止地图旋转手势
        aMap.getUiSettings().setTiltGesturesEnabled(false);//禁止倾斜手势
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setScaleControlsEnabled(true);//显示比例尺控件
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));//设置比例尺，3-19
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        //aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);//跟随模式
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE); //定位模式
        //aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE); // 设置定位的类型为根据地图面向方向旋转
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
        fog_draw_pause_judge = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
        fog_draw_pause_judge = false;
    }

    /*
    定位成功后回调函数
     */
    @Override
    public void onLocationChanged(final AMapLocation amapLocation) {
        mLocationErrText.setVisibility(View.GONE);
        mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
        x = amapLocation.getLatitude();
        y = amapLocation.getLongitude();

        lp.setLatitude(x);
        lp.setLongitude(y);
        //aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 14));

            if (amapLocation.getErrorCode() == 0) {

                pos = new LatLng(x,y);
                Projection projection = aMap.getProjection();

                Point dot = projection.toScreenLocation(pos);
                dot_x = dot.x;
                dot_y = dot.y;
                if (fog_draw_pause_judge){
                    if (i == 0){
                        myView.start_pot(dot_x,dot_y);
                    } else{
                        myView.line(dot_x, dot_y);
                    }
                    i++;
                    Toast.makeText(MapsActivity.this, "dot_x:" + dot_x + ", dot_y:" + dot_y, Toast.LENGTH_SHORT).show();
                }
            } else {
            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
            mLocationErrText.setVisibility(View.VISIBLE);
            mLocationErrText.setText(errText);
        }
        /*
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mLocationErrText.setVisibility(View.GONE);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                x = amapLocation.getLatitude();//获取纬度
                y = amapLocation.getLongitude();//获取经度
                LatLng pos = new LatLng(x,y);
                Projection projection = aMap.getProjection();
                //将地图的点，转换为屏幕上的点 
                Point dot = projection.toScreenLocation(pos);
                dot_x = dot.x;
                dot_y = dot.y;
                if (i == 0){
                    myView.start_pot(dot_x,dot_y);
                } else{
                    myView.line(dot_x, dot_y);
                }
                i++;
                Toast.makeText(MapsActivity.this, "dot_x:" + dot_x + ", dot_y:" + dot_y, Toast.LENGTH_SHORT).show();

                //amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                //amapLocation.getLatitude();//获取纬度
                //amapLocation.getLongitude();//获取经度
                //amapLocation.getAccuracy();//获取精度信息
                //amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                //amapLocation.getCountry();//国家信息
                //amapLocation.getProvince();//省信息
                //amapLocation.getCity();//城市信息
                //amapLocation.getDistrict();//城区信息
                //amapLocation.getStreet();//街道信息
                //amapLocation.getStreetNum();//街道门牌号信息
                //amapLocation.getCityCode();//城市编码
                //amapLocation.getAdCode();//地区编码
                //amapLocation.getAoiName();//获取当前定位点的AOI信息
                //amapLocation.getGpsStatus();//获取GPS的当前状态

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                mLocationErrText.setVisibility(View.VISIBLE);
                mLocationErrText.setText(errText);
            }
        }*/
    }

    /*private void adjustCamera(LatLng centerLatLng,int range){
        //当前缩放级别下的比例尺 
        //"每像素代表" + scale + "米"
        float scale = aMap.getScalePerPixel();
        //代表range（米）的像素数量 
        int pixel = Math.round(range/scale);
        //小范围，小缩放级别（比例尺较大），有精度损失 
        Projection projection = aMap.getProjection();
        //将地图的中心点，转换为屏幕上的点 
        Point center = projection.toScreenLocation(centerLatLng);
        //获取距离中心点为pixel像素的左、右两点（屏幕上的点 
        Point right = new Point(center.x+pixel,center.y);
        Point left = new Point(center.x-pixel,center.y);

        //将屏幕上的点转换为地图上的点 
        LatLng rightLatlng=projection.fromScreenLocation(right);
        LatLng LeftLatlng = projection.fromScreenLocation(left);

        LatLngBounds bounds = LatLngBounds.builder().include(rightLatlng).include(LeftLatlng).build();
        //bounds.contains();

        aMap.getMapScreenMarkers();
    }*/

    /*
    激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /*
    停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }
}

package com.knight.asus_nb.knight.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.BikingRouteOverlay;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.FileUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLine;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;


public class AddLuActivity extends AppCompatActivity {

    //完成控件声明
    private TextView confirmText, startText, endText, centerText,distanceText;
    //地点选择控件声明
    private EditText sousuoEdit;
    //预览控件声明
    private Button yulanBtn;
    //地图控件声明
    private MapView mapView;
    private BaiduMap mbaiduMap;
    private LocationClientOption.LocationMode LocationMode;
    private LocationClient locationClient;
    private boolean isFirstLocate = true;
    //起终点变量声明
    private Overlay startOverlay, endOverlay;
    private LatLng startLanLng, endLanLng;
    //搜索控件声明
    private RelativeLayout serchRel;
    //提示弹窗助手声明
    private DialogUtil dialogUtil;
    private final String LTAG = "AddLuActivity";
    //路线检索实例
    private RoutePlanSearch routePlanSearch;
    //中心img声明
    private ImageView centerImage;
    private int mode = 0; //操作标识 0 - 操作起点 1 - 操作终点
    //地图路径对象声明
    private BikingRouteOverlay overlay;
    //坐标地址互转声明
    private GeoCoder geoCoder;
    //地点检索对象声明
    private SuggestionSearch suggestionSearch;
    //距离
    private double distance;
    private DecimalFormat df = new   DecimalFormat("#.##");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlushu);
        //绑定视图
        bindViews();
        //设置监听
        bindLisenters();
        //初始化数据
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        dialogUtil = DialogUtil.getInstance(AddLuActivity.this);
    }

    private void init() {
        isFirstLocate = true;
        initLocationOption();
        suggestionSearch = SuggestionSearch.newInstance();
        OnGetSuggestionResultListener searchlistener = new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                //处理sug检索结果
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }
               final List<SuggestionResult.SuggestionInfo> suggestionInfoList = suggestionResult.getAllSuggestions();
                String[] resultStr = new String[suggestionInfoList.size()];
                int i = 0;
                for (SuggestionResult.SuggestionInfo suggestionInfo: suggestionInfoList){
                    resultStr[i] = suggestionInfo.city + "" + suggestionInfo.district + suggestionInfo.key;
                    i++;
                }
                dialogUtil.showItemAlertDialog("提示", resultStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        navigateTo(suggestionInfoList.get(i).getPt());
                        if (mode == 0){
                            addMakerStart();
                        } else if (mode == 1){
                            addMakerEnd();
                        }
                        mode = 3 ;
                    }
                });
            }
        };
        suggestionSearch.setOnGetSuggestionResultListener(searchlistener);
        geoCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    centerText.setText("没有找到检索结果");
                    return;
                } else {
                    //详细地址
                    String address = reverseGeoCodeResult.getAddress();
                    //行政区号
                    int adCode = reverseGeoCodeResult. getCityCode();
                    centerText.setText(address);
                    if (mode == 0){
                        startText.setText(address);
                    } else  if (mode == 1){
                        endText.setText(address);
                    }
                }
            }
        };
        geoCoder.setOnGetGeoCodeResultListener(listener);
    }

    private void bindLisenters() {
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startLanLng == null ||endLanLng == null){
                    dialogUtil.showAlertDialogHint("提示","请将信息选择完整");
                    return;
                }
                if (distance <= 0.1){
                    dialogUtil.showAlertDialogHint("提示","距离太近");
                    return;
                }

                GlobalData.currentAddRoadLine = new RoadLine();
                GlobalData.currentAddRoadLine.setUserName(GlobalData.currentUser.getAccountStr());
                GlobalData.currentAddRoadLine.setStartLocat(startLanLng.latitude + "," + startLanLng.longitude);
                GlobalData.currentAddRoadLine.setEndLocat(endLanLng.latitude + "," + endLanLng.longitude);
                GlobalData.currentAddRoadLine.setUid(GlobalData.currentUser.getId());
                GlobalData.currentAddRoadLine.setMeilageDoub(distance);
                long roadid = getIntent().getLongExtra("roadid",-1);
                if (roadid != -1){
                    GlobalData.currentAddRoadLine.setRoadlistid(roadid);
                }
                dialogUtil.showLoadding("加载中,请稍等");
                GlobalData.currentAddRoadLine.setUserHeadImg(GlobalData.currentUser.getUserHeadUrl());
                confirmText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mbaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(final Bitmap bitmap) {
                                FileUtil fileUtil = FileUtil.getInstance(getApplicationContext().getPackageCodePath());
                                try {
                                    Uri uri = fileUtil.createImg(bitmap);
                                    GlobalData.currentAddRoadLine.setLineImgURL(uri.toString());
                                    //打开修改路书详细信息窗口
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialogUtil.hideLoadding();
                                            Intent intent = new Intent(AddLuActivity.this, ModiftyLuShuActivity.class);
                                            startActivity(intent);
                                            AddLuActivity.this.finish();
                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialogUtil.hideLoadding();
                                            dialogUtil.showAlertDialogHint("提示","加载失败，请稍后重试");
                                        }
                                    });

                                }
                            }
                        });
                    }
                },1000);
                        //发起截图申请



            }
        });
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startLanLng == null){
                    addMakerStart();
                }else {
                    mode = 0;
                    navigateTo(startLanLng);
                }

            }
        });
        endText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endLanLng == null) {
                    addMakerEnd();
                } else {
                    mode = 1;
                    navigateTo(endLanLng);
                }
            }
        });
        serchRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = sousuoEdit.getText().toString();
                //请求查询
                suggestionSearch.requestSuggestion(new SuggestionSearchOption()
                        .keyword(result).city("北京"));
            }
        });
        yulanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startLanLng == null ||endLanLng == null){
                    dialogUtil.showAlertDialogHint("提示","请将信息选择完整");
                    return;
                }
                LuJingGuiHua(startLanLng, endLanLng);
                fixMapShow(startLanLng);
                mode = 3;
            }
        });

    }

    private void fixMapShow(LatLng latLng) {
        int zoomLv = getZoom(startLanLng, endLanLng);
        LatLng centerLatLng = new LatLng((endLanLng.latitude - startLanLng.latitude) / 2 + startLanLng.latitude,
                (endLanLng.longitude - startLanLng.longitude) / 2 + startLanLng.longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(centerLatLng)//缩放中心点
                .zoom(zoomLv);//缩放级别
        mbaiduMap.animateMapStatus(MapStatusUpdateFactory
                .newMapStatus(builder.build()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void bindViews() {
        centerImage = (ImageView) this.findViewById(R.id.img_center);
        centerText = (TextView) this.findViewById(R.id.text_center);
        confirmText = (TextView) this.findViewById(R.id.text_confirm);
        startText = (TextView) this.findViewById(R.id.text_start);
        endText = (TextView) this.findViewById(R.id.text_end);
        sousuoEdit = (EditText) this.findViewById(R.id.edit_serach);
        yulanBtn = (Button) this.findViewById(R.id.btn_confirm);
        mapView = (MapView) this.findViewById(R.id.bmapView);
        serchRel = (RelativeLayout) this.findViewById(R.id.rel_serach);
        distanceText  = (TextView) this.findViewById(R.id.text_distance);
    }

    public void DoBack(View view) {
        AddLuActivity.this.finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止定位
        locationClient.stop();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        //释放检索
        if (routePlanSearch != null) {
            routePlanSearch.destroy();
        }
        if (geoCoder != null){
            geoCoder.destroy();
        }
        if (suggestionSearch != null){
            suggestionSearch.destroy();
        }
    }

    /**
     * 初始化定位参数配置
     */
    private void initLocationOption() {
        mbaiduMap = mapView.getMap();
        mbaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (mode == 0) {
                            //选择终点，移除起点信息
                            startOverlay.remove();
                            centerImage.setVisibility(View.VISIBLE);
                            centerText.setVisibility(View.VISIBLE);
                            centerImage.setImageResource(R.mipmap.lushu_edit_map_startpoint_up);
                            centerText.setText(startText.getText().toString());
                        } else  if (mode == 1){
                            //选择起点，移除终点信息
                            endOverlay.remove();
                            centerImage.setVisibility(View.VISIBLE);
                            centerText.setVisibility(View.VISIBLE);
                            centerImage.setImageResource(R.mipmap.lushu_edit_map_endpoint_up);
                            centerText.setText(endText.getText().toString());
                        }
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {

                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        if (mode == 0) {
                            addMakerStart();
                        } else if (mode == 1){
                            addMakerEnd();
                        }
                        if (startLanLng != null && endLanLng != null){

                            distance =  DistanceUtil.getDistance(startLanLng,endLanLng) / 1000;
                            distanceText.setText(df.format(distance) + "Km");
                        }
                        centerImage.setVisibility(View.GONE);
                        centerText.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        });
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(0);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
//            navigateTo(location);
            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            //第一次定位跳转
            if (isFirstLocate){
                navigateTo(location);
                startText.setText(addr);
                addMakerStart();
                isFirstLocate = false;
            }

        }
    }

    /*
     * 将获取到的地理位置信息取出，并设置初始地图的显示比例
     * */
    private void navigateTo(BDLocation location) {

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        //设置当前位置的经纬度
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        mbaiduMap.animateMapStatus(update);
        //设置地图的缩放比例
        update = MapStatusUpdateFactory.zoomTo(15f);
        mbaiduMap.animateMapStatus(update);

    }

    /*
     * 将获取到的地理位置信息取出，并设置初始地图的显示比例
     * */
    private void navigateTo(LatLng location) {

        //设置当前位置的经纬度
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(location);
        mbaiduMap.animateMapStatus(update);
        //设置地图的缩放比例
        update = MapStatusUpdateFactory.zoomTo(15f);
        mbaiduMap.animateMapStatus(update);

    }

    /**
     * 添加起点标记点
     */
    private void addMakerStart() {
        if (startOverlay != null){
            startOverlay.remove();
        }
        mode = 0;
        MapStatus mapStatus = mbaiduMap.getMapStatus();
        startLanLng = new LatLng(mapStatus.target.latitude, mapStatus.target.longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.lushu_edit_map_startpoint);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(startLanLng)
                .icon(bitmap);
        //反解析地址
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(startLanLng)
                // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                .radius(500));
        //在地图上添加Marker，并显示
        startOverlay = mbaiduMap.addOverlay(option);
        navigateTo(startLanLng);


    }

    /**
     * 添加终点标记点
     */
    private void addMakerEnd() {

        if (endOverlay != null){
            endOverlay.remove();
        }
        mode = 1;
        MapStatus mapStatus = mbaiduMap.getMapStatus();
        endLanLng = new LatLng(mapStatus.target.latitude, mapStatus.target.longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.lushu_edit_map_endpoint);
        //构建MarkerOption，用于在地图上添加Marker
        MarkerOptions option = new MarkerOptions()
                .position(endLanLng)
                .icon(bitmap);
        //反解析地址
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(endLanLng)
                // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                .radius(500));
        //在地图上添加Marker，并显示
        endOverlay = mbaiduMap.addOverlay(option);
        navigateTo(endLanLng);
    }

    /**
     * 不行路径规划
     *
     * @param startPt 起点位置
     * @param endPt   终点位置
     */
    public void LuJingGuiHua(LatLng startPt, LatLng endPt) {
        //创建驾车线路规划检索实例；
        if (routePlanSearch == null) {
            routePlanSearch = RoutePlanSearch.newInstance();
        }
        //创建驾车线路规划检索监听者；
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {

            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                //获取驾车线路规划结果

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

                if(overlay != null){
                    overlay.removeFromMap();
                }
                 overlay = new BikingRouteOverlay(mbaiduMap);
                if (bikingRouteResult.getRouteLines().size() > 0) {
                    //获取路径规划数据,(以返回的第一条路线为例）
                    //为BikingRouteOverlay实例设置数据
                    overlay.setData(bikingRouteResult.getRouteLines().get(0));
                    //在地图上绘制BikingRouteOverlay
                    overlay.addToMap();
                }

            }
        };
        //设置驾车线路规划检索监听者，该方法要先于检索方法drivingSearch(DrivingRoutePlanOption)前调用，否则会在某些场景出现拿不到回调结果的情况

        routePlanSearch.setOnGetRoutePlanResultListener(listener);
        //准备检索起、终点信息；
        PlanNode stNode = PlanNode.withLocation(startPt);
        PlanNode enNode = PlanNode.withLocation(endPt);
        //发起驾车线路规划检索；
        routePlanSearch.bikingSearch((new BikingRoutePlanOption())
                .from(stNode)
                .to(enNode).ridingType(0));
    }

    /**
     * 根据经纬极值计算绽放级别。
     *
     * @param startLanLng 起点坐标
     * @param endLanLng   终点坐标
     * @return
     */
    private int getZoom(LatLng startLanLng, LatLng endLanLng) {
        int[] zoom = new int[]{50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000};
        double distance = DistanceUtil.getDistance(startLanLng, endLanLng);
        for (int i = 0; i < zoom.length; i++) {
            if (zoom[i] - distance > 0) {
                return 18 - i + 3;//之所以会多3，是因为地图范围常常是比例尺距离的10倍以上。所以级别会增加3。
            }
        }
        return -1;
    }
}

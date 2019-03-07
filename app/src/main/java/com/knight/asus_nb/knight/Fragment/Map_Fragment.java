package com.knight.asus_nb.knight.Fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.UI.BNaviGuideActivity;
import com.knight.asus_nb.knight.UI.LuShuDaoHangActivity;
import com.knight.asus_nb.knight.Util.BikingRouteOverlay;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.FileUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.SportHistory;

import org.litepal.LitePal;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Map_Fragment extends Fragment {

    //地图控件声明
    private final static String TAG = "Map_Fragment";
    private MapView mMapView;
    private BaiduMap mbaiduMap;
    private boolean isFirstLocate = true;
    public LocationClient mLocationClient;
    private ImageView locateImg;
    private BDLocation myLocation;
    private LatLng startLatLng,endLatLng;
    //坐标地址互转声明
    private GeoCoder geoCoder;
    //提示弹窗助手声明
    private DialogUtil dialogUtil;
    private LocationClientOption.LocationMode LocationMode;
    //地图路径对象声明
    private BikingRouteOverlay overlay;
    //路线检索实例
    private RoutePlanSearch routePlanSearch;
    //终点声明
    private ImageView endImg,downImg,exitImg,daohangImg;
    //控件声明
    private LinearLayout selLuShuDaoHangLin,numLin,startLin,dataLin,daohangLin,guihualin,
            daohangMapLin;
    private RelativeLayout downRel,daohangRel;
    private TextView meilText,daohang2Text,timeText,suduText,startText,resumeText,daohangText,endText;
    //是否已经是向下了
    private boolean downBool = false;
    private int mode = 0;       //当前模式 0 - 运动 1 - 路径规划
    private SportHistory sportHistory;      //当前的运动记录
    private DecimalFormat df = new   DecimalFormat("#.##");
    //定时器
    private Timer mTimer = new Timer();
    //消息传递
    private Handler mHandler;
    //导航模式
    private boolean daohangBool = false;
    //是否运动中
    private boolean yundongBool = false;
    private Overlay mPolyline;
    private List<LatLng> points;  //划线的所有点
    /*导航起终点Marker，可拖动改变起终点的坐标*/
    private Marker mStartMarker;
    private Marker mEndMarker;
    BikeNaviLaunchParam bikeParam;  //骑车导航
    BitmapDescriptor bdStart = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_start);
    BitmapDescriptor bdEnd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_end);
    //定时任务
    private  TimerTask timerTask;
    //百度导航管理器
    private String mSDCardPath = null;
    private   final String APP_FOLDER_NAME = "Knight";
    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private Overlay startOverlay, endOverlay;
    private boolean hasInitSuccess = false;
    private static final int authBaseRequestCode = 1;
    private boolean isResume = false;       //是否在当前页面


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, null);

        bindViews(view);
        //初始化地图
        initMap(view);
        bindLinseters();
        init();

        return view;
    }
    /**
     * 添加终点标记点
     */
    private void addMakerEnd() {

        if (endOverlay != null){
            endOverlay.remove();
        }

    }
    private void refreshYunDong(){
        if (yundongBool){
            resumeText.setVisibility(View.GONE);
            startText.setText("结束");
            startText.setBackgroundResource(R.drawable.ic_circle_red_back);
        }else {
            resumeText.setVisibility(View.VISIBLE);
            startText.setText("开始");
            meilText.setText("0.00");
            timeText.setText("00:00");
            suduText.setText("0.00");
            startText.setBackgroundResource(R.drawable.ic_circle_blue_back);
        }
    }
    //获取上一次的未完成的运动记录
    private void autoqueryLastSport(){
        if (GlobalData.currentUser == null){
            dialogUtil.showToast("未登录用户");
            return;
        }
        if (sportHistory != null){
            return;
        }
        sportHistory = LitePal.where("uid=?",GlobalData.currentUser.getId() + "").findLast(SportHistory.class);
        if (sportHistory == null){
            sportHistory = new SportHistory();
            return;
        }
        //如果上次已结束，重新生成一个对象
        if (sportHistory.getEndtime() != 0 && sportHistory.getStarttime() != 0){
            sportHistory = new SportHistory();
            yundongBool = false;
            return;
        }
        //如果上次没有点结束，那就继续
        if (sportHistory.getStarttime() != 0){
            startText.setText("结束");
            if (timerTask != null){ timerTask.cancel();}
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(101);
                }
            };
            mTimer.schedule(timerTask,1000,1000);
            yundongBool = true;
            resumeText.setVisibility(View.GONE);
            startText.setBackgroundResource(R.drawable.ic_circle_red_back);
            refreshData();
            return;
        }
    }

    //获取上一次的未完成的运动记录
    private void queryLastSport(){
        if (GlobalData.currentUser == null){
            dialogUtil.showToast("未登录用户");
            return;
        }
        SportHistory sportHistory = LitePal.where("uid=?",GlobalData.currentUser.getId() + "").findLast(SportHistory.class);
        if (sportHistory == null){
            dialogUtil.showAlertDialogHint("提示","上次没有记录");
            return;
        }
        //如果上次已结束，重新生成一个对象
        if (sportHistory.getEndtime() != 0 && sportHistory.getStarttime() != 0){
            dialogUtil.showAlertDialogHint("提示","上次运动已结束");
            return;
        }
        if (sportHistory.getStarttime() != 0){
            yundongBool = true;
            if (timerTask != null){ timerTask.cancel();}
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(101);
                }
            };
            mTimer.schedule(timerTask,1000,1000);
            startText.setText("结束");
            dialogUtil.showToast("继续成功");
            resumeText.setVisibility(View.GONE);
            startText.setBackgroundResource(R.drawable.ic_circle_red_back);
            refreshData();
            return;
        }
    }

    private void bindLinseters() {
//        daohangRel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        daohang2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endLatLng == null)  {
                    dialogUtil.showAlertDialogHint("提示","未选择终点");
                    return;
                }
                if (GlobalData.mapfragmentcode ==  LuShuDaoHangActivity.SEL_DAOHANG){
                    startLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                }
                if (startLatLng == null)  {
                    dialogUtil.showAlertDialogHint("提示","未选择起点");
                    return;
                }
                if (myLocation == null)  {
                    dialogUtil.showAlertDialogHint("提示","定位失败");
                    return;
                }

                bikeParam = new BikeNaviLaunchParam().stPt(startLatLng).endPt(endLatLng);
                //开始骑车导航
                startBikeNavi();
            }
        });
        mbaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                       if (mode == 1)
                        endImg.setImageResource(R.mipmap.lushu_edit_map_endpoint_up);
                    }
                    break;
                    case MotionEvent.ACTION_MOVE:{

                    }
                    break;
                    case MotionEvent.ACTION_UP:{
                        if (mode == 1){
                            MapStatus mapStatus = mbaiduMap.getMapStatus();
                            endLatLng = mapStatus.target;
                            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location( mapStatus.target));
                            endImg.setImageResource(R.mipmap.lushu_edit_map_endpoint);
                        }
                    }
                    break;
                }
            }
        });
        resumeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null){
                    dialogUtil.showAlertDialogHint("提示","用户未登录");
                    return;
                }
                queryLastSport();
            }
        });
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null){
                    dialogUtil.showAlertDialogHint("提示","用户未登录");
                    return;
                }
                String startStr = startText.getText().toString();
                if (startStr.equalsIgnoreCase("开始")){
                    yundongBool = true;
                    //新建一条记录
                    sportHistory = new SportHistory();
                    sportHistory.setStarttime(System.currentTimeMillis());
                    sportHistory.setUid(GlobalData.currentUser.getId());
                    sportHistory.setStartLoc(myLocation.getLatitude() + "," + myLocation.getLongitude());
                    if (timerTask != null ){ timerTask.cancel();}
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(101);
                        }
                    };
                    mTimer.schedule(timerTask,1000,1000);
                    resumeText.setVisibility(View.GONE);
                    startText.setText("结束");
                    startText.setBackgroundResource(R.drawable.ic_circle_red_back);
                    refreshData();
                }else {
                    if (sportHistory == null){
                        dialogUtil.showToast("数据错误");
                    } else {
                        sportHistory.setEndLoc(myLocation.getLatitude() + "," + myLocation.getLongitude());
                        sportHistory.setEndtime(System.currentTimeMillis());

                        mHandler.sendEmptyMessage(105);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mHandler.sendEmptyMessage(106);
                            }
                        },1000);
                        sportHistory.save();
                    }
                    yundongBool = false;
                    timerTask.cancel();
                    resumeText.setVisibility(View.VISIBLE);
                    startText.setText("开始");
                    meilText.setText("0.00");
                    timeText.setText("00:00");
                    suduText.setText("0.00");
                    startText.setBackgroundResource(R.drawable.ic_circle_blue_back);
                    GlobalData.currentUser.addmeil((int)sportHistory.getMeil());
                    GlobalData.currentUser.addtime(sportHistory.getSporttime());
                    GlobalData.currentUser.save();
                }
            }
        });
        locateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myLocation != null)
                navigateTo(myLocation);
            }
        });
        downRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downBool){
                    downBool = false;
                    startLin.setVisibility(View.VISIBLE);
                    downImg.setImageResource(R.mipmap.ic_marker_down);
                } else {
                    downBool = true;
                    startLin.setVisibility(View.GONE);
                    downImg.setImageResource(R.mipmap.ic_marker_up);
                }
            }
        });
        exitImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showData();
                if(overlay != null){
                    overlay.removeFromMap();
                }
                if (mStartMarker != null){
                    mStartMarker.remove();
                }
                if (mEndMarker != null){
                    mEndMarker.remove();
                }
                dialogUtil.showToast("退出路线规划");
            }
        });
        guihualin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endLatLng == null)  dialogUtil.showAlertDialogHint("提示","未选择终点");
                LatLng startLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                LuJingGuiHua(startLatLng,endLatLng);
                dialogUtil.showLoadding("规划中");
                mode = 2;
            }
        });
        daohangMapLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (daohangBool){
                    daohangBool = false;
                    dialogUtil.showToast("退出导航");
                    daohangImg.setImageResource(R.mipmap.location_1);
                    MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,false,null);
                    mbaiduMap.setMyLocationConfiguration(myLocationConfiguration);
                } else {
                    daohangBool = true;
                    dialogUtil.showToast("进入导航");
                    daohangImg.setImageResource(R.mipmap.location_2);
                    MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS,false,null);
                    mbaiduMap.setMyLocationConfiguration(myLocationConfiguration);
                }
            }
        });
        selLuShuDaoHangLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null){
                    dialogUtil.showAlertDialogHint("提示","用户未登录");
                    return;
                }
                Intent intent = new Intent(getActivity(),LuShuDaoHangActivity.class);
                startActivity(intent);
            }
        });
        daohangText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endLatLng == null)  {
                    dialogUtil.showAlertDialogHint("提示","未选择终点");
                    return;
                }
                if (GlobalData.mapfragmentcode ==  LuShuDaoHangActivity.SEL_DAOHANG){
                    startLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                }
                if (startLatLng == null)  {
                    dialogUtil.showAlertDialogHint("提示","未选择起点");
                    return;
                }
                if (myLocation == null)  {
                    dialogUtil.showAlertDialogHint("提示","定位失败");
                    return;
                }
//               try{
//                   Intent intent = new Intent();
//                   intent.setData(Uri.parse("baidumap://map/direction?origin=" + myLocation.getLatitude() + "," + myLocation.getLongitude() +
//                           "&destination=" + endLatLng.latitude + "," + endLatLng.longitude +
//                           "&mode=riding"));
//                   startActivity(intent);
//               }catch (Exception e){
//                   dialogUtil.showAlertDialogHint("提示","未安装百度地图");
//               }
                

                bikeParam = new BikeNaviLaunchParam().stPt(startLatLng).endPt(endLatLng);
                //开始骑车导航
                startBikeNavi();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case LuShuDaoHangActivity.SEL_DAOHANG:{
                //选择导航
                showDaoHang();
                //获取地图中心点地理位置
                MapStatus mapStatus = mbaiduMap.getMapStatus();
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location( mapStatus.target));
            }
            break;
            case LuShuDaoHangActivity.SEL_LUSHU:{
                //选择路书导航
                if (GlobalData.currentDaoHangRoadLine != null){
                    daohang2Text.setVisibility(View.VISIBLE);
                    daohangText.setVisibility(View.GONE);
                    String[] startLocStrs = GlobalData.currentDaoHangRoadLine.getStartLocat().split(",");
                    LatLng startLat = new LatLng(Double.parseDouble(startLocStrs[0]),Double.parseDouble(startLocStrs[1]));
                    String[] endLocStrs = GlobalData.currentDaoHangRoadLine.getEndLocat().split(",");
                    LatLng endLat = new LatLng(Double.parseDouble(endLocStrs[0]),Double.parseDouble(endLocStrs[1]));
                    endLatLng = endLat;
                    startLatLng = startLat;
                    LuJingGuiHua(startLat,endLat);
                    fixMapShow(startLat,endLat);
                } else {
                    dialogUtil.showAlertDialogHint("提示","未选择路书");
                }
            }
            break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isResume = false;
    }
    //计算地图等显示
    private void fixMapShow(LatLng startLanLng, LatLng endLanLng) {
        int zoomLv = getZoom(startLanLng, endLanLng);
        LatLng centerLatLng = new LatLng((endLanLng.latitude - startLanLng.latitude) / 2 + startLanLng.latitude,
                (endLanLng.longitude - startLanLng.longitude) / 2 + startLanLng.longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(centerLatLng)//缩放中心点
                .zoom(zoomLv);//缩放级别
        mbaiduMap.animateMapStatus(MapStatusUpdateFactory
                .newMapStatus(builder.build()));
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
    private void showDaoHang(){
        mode = 1;
        dataLin.setVisibility(View.GONE);
        endImg.setVisibility(View.VISIBLE);
        daohangLin.setVisibility(View.VISIBLE);
        daohangText.setVisibility(View.VISIBLE);
        daohang2Text.setVisibility(View.GONE);
    }
    private void showData(){
        mode = 0;
        daohang2Text.setVisibility(View.GONE);
        dataLin.setVisibility(View.VISIBLE);
        endImg.setVisibility(View.GONE);
        daohangLin.setVisibility(View.GONE);
        daohangText.setVisibility(View.GONE);
    }

    private void init(){
        //消息接收并处理
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case 101:{
                        if (isResume)
                        refreshData();
                    }
                    break;
                    case 102:{
                         dialogUtil.hideLoadding();
                        dialogUtil.showAlertDialogHint("提示","保存成功");
                    }
                    break;
                    case 103:{
                        String msg = message.obj.toString();
                        dialogUtil.showToast(msg);
                    }
                    break;
                    case 104:{
                        dialogUtil.hideLoadding();
                        dialogUtil.showAlertDialogHint("提示","保存失败");
                    }
                    break;
                    case 105:{
                        dialogUtil.showLoadding("数据保存中,请稍等");
                    }
                    break;
                    case 106:{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mbaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                                    @Override
                                    public void onSnapshotReady(final Bitmap bitmap) {
                                        FileUtil fileUtil = FileUtil.getInstance(getActivity().getApplicationContext().getPackageCodePath());
                                        try {
                                            Uri uri = fileUtil.createImg(bitmap);
                                            sportHistory.setMapImgUrl(uri.toString());
                                            sportHistory.save();
                                            mHandler.sendEmptyMessage(102);
                                            //打开修改路书详细信息窗口
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            mHandler.sendEmptyMessage(104);
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                    break;
                }
                return false;
            }
        });
        isFirstLocate = true;
        geoCoder = GeoCoder.newInstance();
        //地址转化监听
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果

                    return;
                } else {
                    //详细地址
                    String address = reverseGeoCodeResult.getAddress();
                    //行政区号
                    int adCode = reverseGeoCodeResult. getCityCode();
                    if (mode == 1){
                        endText.setText(address);
                    }
                }
            }
        };
        geoCoder.setOnGetGeoCodeResultListener(listener);

        //标记拖拽事件
        mbaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
                if(marker == mStartMarker){
                    startLatLng = marker.getPosition();
                }else if(marker == mEndMarker){
                    endLatLng = marker.getPosition();
                }
                bikeParam.stPt(startLatLng).endPt(endLatLng);
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
    }


    //初始化控件
    public void bindViews(View view) {

        daohangRel = (RelativeLayout) view.findViewById(R.id.rel_daohang);
        locateImg = (ImageView)view.findViewById(R.id.img_mylocation);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        endImg  = (ImageView)view.findViewById(R.id.img_end);
        downImg  = (ImageView)view.findViewById(R.id.img_up);
        exitImg  = (ImageView)view.findViewById(R.id.img_exit);
        selLuShuDaoHangLin  = (LinearLayout)view.findViewById(R.id.lin_sel_lushudaohang);
        guihualin = (LinearLayout)view.findViewById(R.id.lin_guihua);
        numLin  = (LinearLayout)view.findViewById(R.id.lin_num);
        startLin  = (LinearLayout)view.findViewById(R.id.lin_start);
        daohangLin  = (LinearLayout)view.findViewById(R.id.lin_daohang);
        downRel  = (RelativeLayout)view.findViewById(R.id.rel_down);
        daohangText  = (TextView)view.findViewById(R.id.text_daohang);
        endText  = (TextView)view.findViewById(R.id.text_end);
        meilText  = (TextView)view.findViewById(R.id.text_meil);
        timeText  = (TextView)view.findViewById(R.id.text_time);
        suduText  = (TextView)view.findViewById(R.id.text_sudu);
        startText  = (TextView)view.findViewById(R.id.text_start);
        resumeText  = (TextView)view.findViewById(R.id.text_resume);
        dataLin  = (LinearLayout)view.findViewById(R.id.lin_data);
        daohangMapLin   = (LinearLayout)view.findViewById(R.id.lin_sel_daohangmoshi);
        daohangImg = (ImageView)view.findViewById(R.id.img_daohang);
        daohang2Text = (TextView)view.findViewById(R.id.text_daohang2);
    }

    //初始化地图
    public void initMap(View view) {
        mbaiduMap = mMapView.getMap();
        initLocationOption();
    }
    //刷新运动数据
    private void refreshData(){
        if (mode == 0)  showData();else showDaoHang();
        if (sportHistory != null){
            meilText.setText(df.format(sportHistory.getMeil() / 1000) + "Km" );
            refreshTime();
            double suduDouble =  sportHistory.getMeil() * 1000 / (System.currentTimeMillis() - sportHistory.getStarttime()) ;
            suduText.setText(df.format(suduDouble) + "m/s");
        }
    }
    //刷新时间
    private void refreshTime() {
        if (sportHistory.getStarttime() == 0){
            return;
        }
        long yundongTime = (System.currentTimeMillis() - sportHistory.getStarttime()) / 1000;
        int hour = (int)(yundongTime / 3600);
        int min = (int)(yundongTime % 3600 / 60);
        int second = (int)(yundongTime % 3600 % 60);
        String hourStr = (hour + "").length() < 2?"0" + hour:hour + "";
        String minStr = (min + "").length() < 2?"0" + min:min + "";
        String secondStr = (second + "").length() < 2?"0" + second:second + "";
        timeText.setText(hourStr + ":" + minStr + ":" + secondStr);
    }

    /*
     * 初始化定位
     * */
    private void initLocationOption() {
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,false,null);
        mbaiduMap.setMyLocationConfiguration(myLocationConfiguration);
        mbaiduMap.setMyLocationEnabled(true);
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        //注册监听函数
        mLocationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(2000);
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
        mLocationClient.setLocOption(locationOption);
        //开始定位
        mLocationClient.start();
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

    /*
     *
     * 监听定位结果
     * */
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
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
            //当前记录为不空且正在运动中，添加位移
            if (sportHistory != null && myLocation != null && yundongBool) {
                //增加距离
                //获取纬度信息
                double startlatitude = myLocation.getLatitude();
                //获取经度信息
                double startlongitude = myLocation.getLongitude();
                double distance = DistanceUtil.getDistance(new LatLng(startlatitude,startlongitude),new LatLng(latitude,longitude));

                    sportHistory.addMeil(distance);

                //划线
//                points.add(new LatLng(latitude,longitude));
//                drawLine(points);
            }
            //更新我的位置经纬度
            myLocation = location;
            MyLocationData.Builder builder = new  MyLocationData.Builder();
            builder.latitude(latitude);
            builder.longitude(longitude);
            mbaiduMap.setMyLocationData(builder.build());
            //第一次定位就跳到定位点
            if (isFirstLocate){
                isFirstLocate = false;
                navigateTo(location);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        isResume = true;
        dialogUtil = DialogUtil.getInstance(getActivity());
        //刷新运动视图
        refreshYunDong();
        if (GlobalData.mapfragmentcode == LuShuDaoHangActivity.SEL_DAOHANG){
            //选择导航
            showDaoHang();
            //获取地图中心点地理位置
            MapStatus mapStatus = mbaiduMap.getMapStatus();
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location( mapStatus.target));
        } else if (GlobalData.mapfragmentcode == LuShuDaoHangActivity.SEL_LUSHU){
            //选择路书导航
            if (GlobalData.currentDaoHangRoadLine != null){
                String[] startLocStrs = GlobalData.currentDaoHangRoadLine.getStartLocat().split(",");
                LatLng startLat = new LatLng(Double.parseDouble(startLocStrs[0]),Double.parseDouble(startLocStrs[1]));
                String[] endLocStrs = GlobalData.currentDaoHangRoadLine.getEndLocat().split(",");
                LatLng endLat = new LatLng(Double.parseDouble(endLocStrs[0]),Double.parseDouble(endLocStrs[1]));
                endLatLng = endLat;
                startLatLng = startLat;
                LuJingGuiHua(startLat,endLat);
                fixMapShow(startLat,endLat);
                daohang2Text.setVisibility(View.VISIBLE);
            } else {
                dialogUtil.showAlertDialogHint("提示","未选择路书");
            }
        }
        //根据当前模式显示对应的界面
        if (mode == 0)showData();else showDaoHang();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalData.mapfragmentcode = -1;
        mode = 0;
        //停止定位
        mLocationClient.stop();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mbaiduMap.setMyLocationEnabled(false);
        //释放检索
        if (routePlanSearch != null) {
            routePlanSearch.destroy();
        }
        if (geoCoder != null){
            geoCoder.destroy();
        }
        if (sportHistory != null){
            sportHistory.save();
        }
    }
    /**
     * 路径规划
     *
     * @param startPt 起点位置
     * @param endPt   终点位置
     */
    public void LuJingGuiHua(LatLng startPt, LatLng endPt) {
        //创建骑车线路规划检索实例；
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
                overlay.setShowEnd(true);
                if (bikingRouteResult.getRouteLines().size() > 0) {
                    //获取路径规划数据,(以返回的第一条路线为例）
                    //为BikingRouteOverlay实例设置数据
                    overlay.setData(bikingRouteResult.getRouteLines().get(0));
                    //在地图上绘制BikingRouteOverlay
                    overlay.addToMap();
                }
                dialogUtil.hideLoadding();
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

    private void drawLine(List<LatLng> points){
        if (mPolyline != null){
            mPolyline.remove();
        }
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(points);
        //在地图上绘制折线
        //mPloyline 折线对象
         mPolyline = mbaiduMap.addOverlay(mOverlayOptions);
    }
    /**
     * 开始骑行导航
     */
    private void startBikeNavi() {
        Log.e(TAG, "startBikeNavi");
        try {
            BikeNavigateHelper.getInstance().initNaviEngine(getActivity(), new IBEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.e(TAG, "BikeNavi engineInitSuccess");
                    routePlanWithBikeParam();
                }

                @Override
                public void engineInitFail() {
                    Log.e(TAG, "BikeNavi engineInitFail");
                }
            });
        } catch (Exception e) {

            Log.e(TAG, "startBikeNavi Exception" + e.toString());
            e.printStackTrace();
        }
    }
    /**
     * 发起骑行导航算路
     */
    private void routePlanWithBikeParam() {
        BikeNavigateHelper.getInstance().routePlanWithParams(bikeParam, new IBRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d(TAG, "BikeNavi onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d(TAG, "BikeNavi onRoutePlanSuccess");
                Intent intent = new Intent();
                intent.setClass(getActivity(), BNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError error) {
                Log.d(TAG, "BikeNavi onRoutePlanFail");
            }

        });
    }

    private void sendMessage(String obj,int what){
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        mHandler.sendMessage(message);
    }

}

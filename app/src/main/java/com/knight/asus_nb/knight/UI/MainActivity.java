package com.knight.asus_nb.knight.UI;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.knight.asus_nb.knight.Fragment.ChooseAreaFragment;
import com.knight.asus_nb.knight.Fragment.Map_Fragment;
import com.knight.asus_nb.knight.Fragment.Person_Fragment;
import com.knight.asus_nb.knight.Fragment.RoadBook_Fragment;
import com.knight.asus_nb.knight.Fragment.Shopping_Fragment;
import com.knight.asus_nb.knight.Fragment.Weather_Fragment;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.Util.SharedPreferencesUtil;
import com.knight.asus_nb.knight.db.User;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //底部导航栏控件声明
    private BottomBar bottomBar;
    //Fragment管理器声明
    private FragmentManager fragmentManager;
    //Fragment事务声明
    private FragmentTransaction fragmentTransaction;
    //Fragment声明
    private Fragment personFragment, mapFragment, roadBookFragment,
            shopFragment, weatherFragment, chooseAreaFragment;
    //上一次退出的时间戳
    private long mExitTime;
    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initpress();
        //初始化数据
        init();
        //初始化控件
        initView();
        //初始化底部导航栏点击事件
        initBottomBarListener();
    }

    private void initpress() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, 1);
                return;
            }
        }
    }
    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private void init() {

    }

    /*
     * 依次对权限进行判断，如果有权限被用户拒绝，那么关闭程序，如果所有权限同意了，就开始地理位置定位
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    /*
     * 初始化地图以外的控件
     * */
    public void initView() {
        //初始化底部导航栏
        bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        //初始化fragment事务，并设置默认页
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        personFragment = new Person_Fragment();
        mapFragment = new Map_Fragment();
        roadBookFragment = new RoadBook_Fragment();
        shopFragment = new Shopping_Fragment();
        weatherFragment = new Weather_Fragment();
        chooseAreaFragment = new ChooseAreaFragment();
        fragmentTransaction.add(R.id.frafment, personFragment);
        fragmentTransaction.commit();

    }

    /*
     * 底部导航栏添加监听器
     * */
    public void initBottomBarListener() {

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (tabId) {
                    //路书界面
                    case R.id.tab_roadbook:
                        fragmentTransaction.replace(R.id.frafment, roadBookFragment);
                        break;
                    //天气界面
                    case R.id.tab_weather:
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        fragmentTransaction.replace(R.id.frafment, chooseAreaFragment);
                        //如果SharedPreferences里面有数据，说明已经选择过城市了，跳转到天气fragment
                        if (prefs.getString("weather", null) != null) {
                            fragmentTransaction.replace(R.id.frafment, weatherFragment);
                        }
                        break;
                    //骑行界面
                    case R.id.tab_bike:
                        fragmentTransaction.replace(R.id.frafment, mapFragment);
                        break;
                    //商城界面
                    case R.id.tab_shopping:
                        fragmentTransaction.replace(R.id.frafment, shopFragment);
                        break;
                    //个人界面
                    case R.id.tab_person:
                        fragmentTransaction.replace(R.id.frafment, personFragment);
                        break;
                }
                fragmentTransaction.commit();

            }
        });
    }

    /*
     * 对主界面上按钮进行监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //地图上的浮动按钮点击事件
            case R.id.floating_button:
                //底部导航栏选择的按钮，重置为我的界面
                bottomBar.selectTabWithId(R.id.tab_person);
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {// 如果两次按键时间间隔大于2000毫秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}



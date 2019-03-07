package com.knight.asus_nb.knight.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.knight.asus_nb.knight.Util.FileUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.Util.SharedPreferencesUtil;
import com.knight.asus_nb.knight.db.User;

import org.litepal.LitePal;

import java.util.List;


public class StartActivity extends Activity {
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        hideToolBar();
        //自动登录
        autologin();
        //延时跳转主页面
        delayClose();
    }

    private void delayClose() {
        Handler mHandler = new Handler();
        //延迟俩秒发送
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //跳转进入APP主页面
                intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                StartActivity.this.finish();
            }
        },1000);
        FileUtil.getInstance("/lib");

    }

    private void hideToolBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 自动登录
     */
    private void autologin(){
        SharedPreferencesUtil sharedPreferencesUtil =  SharedPreferencesUtil.getInstance(StartActivity.this);
        String accountStr =  sharedPreferencesUtil.GetData("account","");
        String pwd = sharedPreferencesUtil.GetData("pwd","");
        if (accountStr != null && pwd != null && accountStr.length() != 0 && pwd.length() != 0){
            checkLogin(accountStr,pwd);
        }
    }
    /**
     * 验证账号
     * @param account
     * @param pwd
     */
    private boolean checkLogin(String account,String pwd){
        List<User> userList = LitePal.where("phoneStr=? and passwordStr=?" ,account, pwd).find(User.class);
        if (userList != null && userList.size() == 1){
            GlobalData.currentUser = userList.get(0);
            return true;
        }
        userList = LitePal.where("emailStr=? and passwordStr=?" ,account, pwd).find(User.class);
        if (userList != null && userList.size() == 1){
            GlobalData.currentUser = userList.get(0);
            return true;
        }
        GlobalData.currentUser = null;
        return false;
    }
}

package com.knight.asus_nb.knight.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.UI.LoginActivity;
import com.knight.asus_nb.knight.UI.SettingActivity;
import com.knight.asus_nb.knight.UI.UserInfoActivity;
import com.knight.asus_nb.knight.UI.WebActivity;
import com.knight.asus_nb.knight.UI.YuDongjiluActivity;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.User;

import org.litepal.LitePal;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class Person_Fragment extends Fragment {
    //标题栏控件声明
    private ImageView userInfo_btn, set_btn, userHeadImg;
    //菜单控件声明
    private RelativeLayout yundongRel, orderRel, saveRel, userinfoRel;
    //用户信息控件声明
    private TextView userNameText, userDescText, userIdText, moonText, meilText, levelText, timeText;
    //提示窗口助手声明
    private DialogUtil dialogUtil;
    //刷新控件声明
    private SwipeRefreshLayout swipeRefreshLayout;
    private DecimalFormat df = new   DecimalFormat("#.##");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_fragment, null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //绑定视图
        initView(getView());
        //绑定监听
        bindLisenters();
    }

    @Override
    public void onResume() {
        super.onResume();
        dialogUtil = DialogUtil.getInstance(getContext());
        reFreshUserInfo();
    }

    private void bindLisenters() {
        //个人信息
        userInfo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null) {
                    dialogUtil.showAlertDialogHint("提示", "用户未登录");
                    return;
                }
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });
        //设置
        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
        yundongRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null) {
                    dialogUtil.showAlertDialogHint("提示", "用户未登录");
                    return;
                }
                Intent intent = new Intent(getActivity(),YuDongjiluActivity.class);
                startActivity(intent);
            }
        });
        orderRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                startActivity(intent);
            }
        });
        saveRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        userinfoRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null || userNameText.getText().toString().equals("点击登录")){
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    startActivity(intent);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reFreshUserInfo();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    //刷新用户数据
    private void reFreshUserInfo(){
        if (GlobalData.currentUser != null){
            loadHeadImg();
            userNameText.setText(GlobalData.currentUser.getAccountStr());
            if (GlobalData.currentUser.getUserDesc() == null || GlobalData.currentUser.getUserDesc().length() == 0){
                userDescText.setText("暂无介绍");
            } else {
                userDescText.setText(GlobalData.currentUser.getUserDesc());
            }
            userIdText.setText( "#" + GlobalData.currentUser.getId());
            meilText.setText(df.format(GlobalData.currentUser.getMeilsum() / 1000));
            queryLevel();
            refreshTime();
        } else {
            userHeadImg.setImageResource(R.mipmap.v1_profile_photo_2x);
            userNameText.setText("点击登录");
            userDescText.setText("暂无简介");
            userIdText.setText("#00000");
            meilText.setText("0");
            levelText.setText("--");
            timeText.setText("00:00");
        }
        moonText.setText(Calendar.getInstance().get(Calendar.MONTH) + 1 + "月");
    }
    //查询排名 按照运动时间排序
    private void queryLevel(){
      List<User> userList =  LitePal.order("timesum desc").find(User.class);
      for (int i = 0; i < userList.size();i++){
          if (userList.get(i).getId() == GlobalData.currentUser.getId()){
              levelText.setText((i + 1) + "");
              break;
          }
      }
    }
    //刷新时间
    private void refreshTime() {
        long yundongTime =  GlobalData.currentUser.getTimesum();
        int hour = (int)(yundongTime / 3600);
        int min = (int)(yundongTime % 3600 / 60);
        int second = (int)(yundongTime % 3600 % 60);
        String hourStr = (hour + "").length() < 2?"0" + hour:hour + "";
        String minStr = (min + "").length() < 2?"0" + min:min + "";
        String secondStr = (second + "").length() < 2?"0" + second:second + "";
        timeText.setText(hourStr + ":" + minStr + ":" + secondStr);
    }
    //加载头像
    private void loadHeadImg() {
        if (GlobalData.currentUser.getUserHeadUrl() != null ){
            Uri uri = Uri.parse(GlobalData.currentUser.getUserHeadUrl());
            userHeadImg.setImageURI(uri);
        } else userHeadImg.setImageResource(R.mipmap.v1_profile_photo_2x);
    }

    //初始化控件
    public void initView(View view) {
        userInfo_btn = (ImageView) view.findViewById(R.id.img_user_info);
        set_btn = (ImageView) view.findViewById(R.id.img_setting);
        userHeadImg = (ImageView) view.findViewById(R.id.img_userhead);
        yundongRel = (RelativeLayout) view.findViewById(R.id.rel_yudong_rec);
        orderRel = (RelativeLayout) view.findViewById(R.id.rel_order);
        saveRel = (RelativeLayout) view.findViewById(R.id.rel_save);
        userinfoRel = (RelativeLayout) view.findViewById(R.id.rel_userinfo);
        userinfoRel = (RelativeLayout) view.findViewById(R.id.rel_userinfo);
        userNameText = (TextView) view.findViewById(R.id.text_username);
        userDescText = (TextView) view.findViewById(R.id.text_userdesc);
        userIdText = (TextView) view.findViewById(R.id.text_userid);
        moonText = (TextView) view.findViewById(R.id.text_moon);
        meilText = (TextView) view.findViewById(R.id.text_meil);
        levelText = (TextView) view.findViewById(R.id.text_level);
        timeText = (TextView) view.findViewById(R.id.text_time);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
    }


}

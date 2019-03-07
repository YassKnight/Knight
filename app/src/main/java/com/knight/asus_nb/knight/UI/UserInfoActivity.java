package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    //菜单控件声明
    private RelativeLayout userHeadRel,userNameRel,userPhoneRel,emailRel,addrRel,descRel;
    //信息显示控件声明
    private TextView userNameText,userPhoneText,emailText,addrText,descText;
    //头像控件声明
    private RoundedImageView userHeadImg;
    //打开相册标识
    private final  int GOPICTTURE = 0;
    //对话提示窗助手
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        //绑定视图
        bindViews();
        //设置监听
        bindLisenters();
        //初始化数据
        init();
    }
    private void bindViews(){
        userHeadRel = (RelativeLayout)this.findViewById(R.id.rel_selheadimg);
        userNameRel = (RelativeLayout)this.findViewById(R.id.rel_username);
        userPhoneRel = (RelativeLayout)this.findViewById(R.id.rel_phone);
        emailRel = (RelativeLayout)this.findViewById(R.id.rel_email);
        addrRel = (RelativeLayout)this.findViewById(R.id.rel_addr);
        descRel = (RelativeLayout)this.findViewById(R.id.rel_desc);

        userNameText = (TextView) this.findViewById(R.id.text_username);
        userPhoneText = (TextView) this.findViewById(R.id.text_phone);
        emailText = (TextView) this.findViewById(R.id.text_email);
        addrText = (TextView) this.findViewById(R.id.text_addr);
        descText = (TextView) this.findViewById(R.id.text_desc);
        userHeadImg = (RoundedImageView) this.findViewById(R.id.img_userhead);
    }
    private void bindLisenters(){
        userHeadRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GOPICTTURE);
            }
        });
        userNameRel.setOnClickListener(this);
        userPhoneRel.setOnClickListener(this);
        emailRel.setOnClickListener(this);
        addrRel.setOnClickListener(this);
        descRel.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOPICTTURE && resultCode == RESULT_OK && data != null) {
            //图库
            try {
                Uri uri = data.getData();
                GlobalData.currentUser.setUserHeadUrl(uri.toString());
                userHeadImg.setImageURI(uri);
                GlobalData.currentUser.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init(){

    }
    public void DoBack(View view){
        UserInfoActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogUtil = DialogUtil.getInstance(UserInfoActivity.this);
        refreshData();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        loadHeadImg();
        userNameText.setText(GlobalData.currentUser.getAccountStr());
        userPhoneText.setText(GlobalData.currentUser.getPhoneStr());
        emailText.setText(GlobalData.currentUser.getEmailStr());
        addrText.setText(GlobalData.currentUser.getUserAdd());
        descText.setText(GlobalData.currentUser.getUserDesc());
    }

    private void loadHeadImg() {
        if (GlobalData.currentUser.getUserHeadUrl() != null ){
            Uri uri = Uri.parse(GlobalData.currentUser.getUserHeadUrl());
            userHeadImg.setImageURI(uri);
        } else userHeadImg.setImageResource(R.mipmap.v1_profile_photo_2x);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(UserInfoActivity.this,ModiftyDataActivity.class);
        switch (view.getId()){
            case R.id.rel_addr:{
                //修改地址
                intent.putExtra("dtype",0);
                intent.putExtra("title","修改地址");
            }
            break;
            case R.id.rel_username:{
                //修改用户名称
                intent.putExtra("dtype",1);
                intent.putExtra("title","修改名称");
            }
            break;
            case R.id.text_email:{
                //修改邮箱
                intent.putExtra("dtype",2);
                intent.putExtra("title","修改邮箱");
            }
            break;
            case R.id.rel_phone:{
                //修改电话
                intent.putExtra("dtype",3);
                intent.putExtra("title","修改电话");
            }
            break;
            case R.id.rel_desc:{
                //修改介绍
                intent.putExtra("dtype",4);
                intent.putExtra("title","修改介绍");
            }
            break;
        }
        startActivity(intent);
    }
}

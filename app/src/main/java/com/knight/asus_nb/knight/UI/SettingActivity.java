package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.CallBack;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.Util.SharedPreferencesUtil;


public class SettingActivity extends AppCompatActivity {

    //菜单控件声明
    private RelativeLayout modiftyRel,dataRel,repRel,useRel,tuichuRel;
    //对话窗窗口提示助手
    private DialogUtil dialogUtil;
    //线控件声明
    private View tuichuView,modiftyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settting);
        //绑定视图
        bindViews();
        //设置监听
        bindLisenters();
        //初始化数据
        init();
    }

    private void bindViews() {
        modiftyRel = (RelativeLayout)this.findViewById(R.id.rel_modifty);
        dataRel = (RelativeLayout)this.findViewById(R.id.rel_data);
        repRel = (RelativeLayout)this.findViewById(R.id.rel_rep);
        useRel = (RelativeLayout)this.findViewById(R.id.rel_use);
        tuichuRel = (RelativeLayout)this.findViewById(R.id.rel_tuichu);
        tuichuView = (View)this.findViewById(R.id.view_tuichu);
        modiftyView = (View)this.findViewById(R.id.view_modfity);
    }
    private void bindLisenters() {
        modiftyRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,ModiftyActivity.class);
                startActivity(intent);
            }
        });
        repRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,ModiftyDataActivity.class);
                intent.putExtra("title","问题反馈");
                startActivity(intent);
            }
        });
        useRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tuichuRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUtil.showAlertDialog("提示", "是否要退出当前账号",
                        new CallBack() {
                            @Override
                            public void getResult(int Type) {
                                if (Type == CallBack.SUCCESS){
                                  GlobalData.currentUser = null;
                                    SharedPreferencesUtil sharedPreferencesUtil =  SharedPreferencesUtil.getInstance(SettingActivity.this);
                                    sharedPreferencesUtil.SaveData("account","");
                                   sharedPreferencesUtil.SaveData("pwd","");
                                  SettingActivity.this.finish();
                                }
                            }
                        });
            }
        });
    }
    private void init() {
        if (GlobalData.currentUser == null){
            tuichuRel.setVisibility(View.GONE);
            modiftyRel.setVisibility(View.GONE);
            modiftyView.setVisibility(View.GONE);
            tuichuView.setVisibility(View.GONE);
        } else {
            tuichuRel.setVisibility(View.VISIBLE);
            modiftyRel.setVisibility(View.VISIBLE);
            modiftyView.setVisibility(View.VISIBLE);
            tuichuView.setVisibility(View.VISIBLE);
        }
        dialogUtil = DialogUtil.getInstance(SettingActivity.this);
    }
    public void DoBack(View view){
        SettingActivity.this.finish();
    }
}

package com.knight.asus_nb.knight.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;

public class ModiftyDataActivity extends AppCompatActivity {

    //标题控件声明
    private TextView titleText;
    //修改内容控件声明
    private EditText modiftyDataEdit;
    //确认控件声明
    private Button confirmBtn;
    //对话提示窗助手
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modiftydata);
        bindViews();
        bindLisenters();
        init();
    }
    private void init() {
        dialogUtil = DialogUtil.getInstance(ModiftyDataActivity.this);
        String title = getIntent().getStringExtra("title");
        titleText.setText(title);
    }
    private void bindLisenters() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String modiftyDataStr = modiftyDataEdit.getText().toString();
                if (GlobalData.currentUser == null){
                    dialogUtil.showAlertDialogHint("提示","用户未登录");
                    return;
                }
                if (modiftyDataStr.length() == 0){
                    dialogUtil.showAlertDialogHint("提示","内容不能为空");
                } else {

                    int type = getIntent().getIntExtra("dtype",-1);
                    switch (type){
                        case 0:{
                            //修改地址
                            GlobalData.currentUser.setUserAdd(modiftyDataStr);
                        }
                        break;
                        case 1:{
                            //修改用户名称
                            GlobalData.currentUser.setShowNameStr(modiftyDataStr);
                        }
                        break;
                        case 2:{
                            //修改邮箱
                            GlobalData.currentUser.setEmailStr(modiftyDataStr);
                        }
                        break;
                        case 3:{
                            //修改电话
                            GlobalData.currentUser.setPhoneStr(modiftyDataStr);
                        }
                        break;
                        case 4:{
                            //修改介绍
                            GlobalData.currentUser.setUserDesc(modiftyDataStr);
                        }
                        break;
                    }
                    GlobalData.currentUser.save();
                    ModiftyDataActivity.this.finish();
                }
            }
        });
    }

    private void bindViews() {
        titleText = (TextView)this.findViewById(R.id.PersonPage_title);
        modiftyDataEdit = (EditText)this.findViewById(R.id.edit_modifty);
        confirmBtn = (Button)this.findViewById(R.id.btn_confirm);
    }

    public void DoBack(View view){
        ModiftyDataActivity.this.finish();
    }
}

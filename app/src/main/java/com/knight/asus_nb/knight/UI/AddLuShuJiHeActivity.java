package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLineList;

public class AddLuShuJiHeActivity extends AppCompatActivity {

    //输入控件声明
    private EditText nameEdit,descEdit;
    //图片图像声明
    private ImageView addImg,showImg;
    //完成按钮控件声明
    private Button confirmBtn;
    //对话窗助手声明
    private DialogUtil dialogUtil;
    private Uri uri;
    //打开相册标识
    private final  int GOPICTTURE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlushulist);
        bindViews();
        bindLisneters();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogUtil = DialogUtil.getInstance(AddLuShuJiHeActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOPICTTURE && resultCode == RESULT_OK && data != null) {
            //图库
            try {
                uri = data.getData();
                showImg.setImageURI(uri);
                showImg.setVisibility(View.VISIBLE);
                addImg.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void init(){

    }
    private void bindLisneters(){
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr = nameEdit.getText().toString();
                String descStr = descEdit.getText().toString();
                if (nameStr.length() == 0 || descStr.length() == 0){
                    dialogUtil.showAlertDialogHint("提示","请输入完整的信息");
                    return;
                }
                if (uri == null){
                    dialogUtil.showAlertDialogHint("提示","未选择图片");
                    return;
                }
                GlobalData.currentAddRoadLineList = new RoadLineList();
                GlobalData.currentAddRoadLineList.setUserName(GlobalData.currentUser.getAccountStr());
                GlobalData.currentAddRoadLineList.setLineDesc(descStr);
                GlobalData.currentAddRoadLineList.setNameStr(nameStr);
                GlobalData.currentAddRoadLineList.setLineImgUrl(uri.toString());
                GlobalData.currentAddRoadLineList.setUid(GlobalData.currentUser.getId());
                GlobalData.currentAddRoadLineList.setUserHeadImg(GlobalData.currentUser.getUserHeadUrl());
                GlobalData.currentAddRoadLineList.save();
                dialogUtil.showToast("保存成功");
                AddLuShuJiHeActivity.this.finish();
            }
        });
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GOPICTTURE);
            }
        });
        showImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GOPICTTURE);
            }
        });
    }
    private void bindViews(){
        nameEdit = (EditText)this.findViewById(R.id.edit_name);
        descEdit = (EditText)this.findViewById(R.id.edit_desc);
        addImg = (ImageView)this.findViewById(R.id.img_add);
        showImg = (ImageView)this.findViewById(R.id.img_show);
        confirmBtn = (Button)this.findViewById(R.id.btn_confirm);
    }
    public void DoBack(View view){
        AddLuShuJiHeActivity.this.finish();
    }
}

package com.knight.asus_nb.knight.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;

public class ModiftyLuShuActivity extends AppCompatActivity {

    //输入控件声明
    private EditText nameEdit,descEdit;
    //完成控件声明
    private Button confirmBtn;
    //对话提示窗声明
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modiftylushu);
        //绑定视图
        bindViews();
        //设置监听
        bindLisenters();
        //初始化数据
        init();
    }

    private void init() {
        dialogUtil = DialogUtil.getInstance(ModiftyLuShuActivity.this);
    }

    private void bindLisenters() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr = nameEdit.getText().toString();
                String descStr = nameEdit.getText().toString();
                if (nameStr.length() == 0 || descStr.length() == 0){
                    dialogUtil.showAlertDialogHint("提示","信息不完整");
                    return;
                }
                if (GlobalData.currentAddRoadLine == null){
                    dialogUtil.showAlertDialogHint("提示","未开始添加");
                    return;
                }
                GlobalData.currentAddRoadLine.setRoadLineName(nameStr);
                GlobalData.currentAddRoadLine.setDescStr(descStr);
                GlobalData.currentAddRoadLine.save();
                GlobalData.currentAddRoadLine = null;
                dialogUtil.showToast("保存成功");
                ModiftyLuShuActivity.this.finish();
            }
        });
    }

    private void bindViews() {
        confirmBtn = (Button)this.findViewById(R.id.btn_confirm);
        nameEdit = (EditText)this.findViewById(R.id.edit_name);
        descEdit = (EditText)this.findViewById(R.id.edit_desc);
    }

    public void DoBack(View view){
        ModiftyLuShuActivity.this.finish();
    }
}

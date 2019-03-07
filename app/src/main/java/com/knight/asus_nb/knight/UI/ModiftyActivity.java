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
import com.knight.asus_nb.knight.Util.SharedPreferencesUtil;

public class ModiftyActivity extends AppCompatActivity {

    //输入控件声明
    private EditText pwdEdit,newPwdEdit,newPwdAgainEdit;
    //登录按钮声明
    private Button confirmBtn;
    //窗口提示助手声明
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modfotypwd);
        //绑定视图
        bindViews();
        //设置监听
        bindLisenters();
        //数据初始化
        init();
    }
    private void bindViews(){
        pwdEdit = (EditText)this.findViewById(R.id.edit_pwd);
        newPwdEdit = (EditText)this.findViewById(R.id.edit_pwd_new);
        newPwdAgainEdit = (EditText)this.findViewById(R.id.edit_pwd_new_again);
        confirmBtn = (Button)this.findViewById(R.id.btn_confirm);
    }
    private void bindLisenters(){
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null){
                    dialogUtil.showAlertDialogHint("提示","用户未登录");
                    return;
                }
                if (checkInput(pwdEdit)
                        && checkInput(newPwdEdit) && checkInput(newPwdAgainEdit)){

                    if (!newPwdEdit.getText().toString().equals(newPwdAgainEdit.getText().toString())){
                        dialogUtil.showAlertDialogHint("提示","新密码输入不一致");
                        return;
                    }
                    if (!pwdEdit.getText().toString().equals(GlobalData.currentUser.getPasswordStr())){
                        dialogUtil.showAlertDialogHint("提示","旧密码输入错误");
                        return;
                    }
                    SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance(ModiftyActivity.this);
                    sharedPreferencesUtil.SaveData("pwd",newPwdEdit.getText().toString());
                    GlobalData.currentUser.setPasswordStr(newPwdEdit.getText().toString());
                    GlobalData.currentUser.save();
                    dialogUtil.showAlertDialogHint("提示","修改成功！");
                } else {
                    dialogUtil.showAlertDialogHint("提示","信息不完整");
                }
            }
        });
    }
    private void init(){
        dialogUtil = DialogUtil.getInstance(ModiftyActivity.this);
    }
    /**
     * 检查输入
     * @return 输入是否有效
     */
    private boolean checkInput(EditText editText){
        if (editText.getText().toString().length() == 0){
            return false;
        }
        return true;
    }
    /**
     * 返回函数声明
     * @param view
     */
    public void DoBack(View view){
        ModiftyActivity.this.finish();
    }
}

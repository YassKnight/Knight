package com.knight.asus_nb.knight.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.db.User;

import org.litepal.LitePal;

import java.util.List;

public class ForgetPwdActivity extends AppCompatActivity {

    //输入控件声明
    private EditText accountEdit,phoneEdit,eamilEdit,pwdEdit,pwdAgainEdit;
    //确认按钮声明
    private Button confirmBtn;
    //窗口提示助手
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpwd);
        //绑定视图
        bindViews();
        //设置监听
        bindLisenters();
        //初始化数据
        init();
    }

    private void init() {
        dialogUtil = DialogUtil.getInstance(ForgetPwdActivity.this);
    }

    private void bindLisenters() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput(accountEdit) &&
                        checkInput(phoneEdit)  &&
                        checkInput(eamilEdit) &&
                        checkInput(pwdEdit) &&
                        checkInput(pwdAgainEdit)){

                    if (pwdEdit.getText().toString().equals(pwdAgainEdit.getText().toString())){
                        checkUser(accountEdit.getText().toString(),phoneEdit.getText().toString(),
                                eamilEdit.getText().toString(),pwdEdit.getText().toString());
                    } else {
                      dialogUtil.showToast("密码不一致");
                    }
                }
        }
        });
    }

    /**
     * 验证账号
     * @return
     */
    private boolean checkUser(String account,String phone,String eamil,String pwd){
       List<User> userList = LitePal.where("accountStr = ? and  phoneStr = ? and emailStr = ?" ,account
               ,phone,eamil).find(User.class);
       if (userList == null || userList.size() == 0){
           //验证失败
           dialogUtil.showAlertDialogHint("提示","信息不正确！");
           return false;
       }
       userList.get(0).setPasswordStr(pwd);
       userList.get(0).save();
       dialogUtil.showAlertDialogHint("提示","修改成功，快去登录吧！");
       return true;
    }

    private void bindViews() {
        accountEdit = (EditText)this.findViewById(R.id.edit_username);
        phoneEdit = (EditText)this.findViewById(R.id.edit_phone);
        eamilEdit = (EditText)this.findViewById(R.id.edit_eamil);
        pwdEdit = (EditText)this.findViewById(R.id.edit_pwd);
        pwdAgainEdit = (EditText)this.findViewById(R.id.edit_pwd_again);
        confirmBtn = (Button)this.findViewById(R.id.btn_confirm);
    }

    /**
     * 检查输入
     * @return 输入是否有效
     */
    private boolean checkInput(EditText editText){
        if (editText.getText().toString().length() == 0){
            dialogUtil.showToast("请输入" + editText.getHint());
            return false;
        }
        return true;
    }

    /**
     * 返回函数声明
     * @param view
     */
    public void DoBack(View view){
        ForgetPwdActivity.this.finish();
    }
}

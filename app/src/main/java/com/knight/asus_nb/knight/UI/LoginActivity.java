package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.Util.SharedPreferencesUtil;
import com.knight.asus_nb.knight.db.User;

import org.litepal.LitePal;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    //输入控件声明
    private EditText accountEdit,pwdEdit;
    //登录按钮控件声明
    private Button  loginBtn;
    //注册文字控件声明 忘记密码控件声明
    private TextView resgisterText,forgetPwdText;
    //提示助手声明
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //绑定视图控件
        bindViews();
        //设置监听事件
        bindLisenter();
        //初始化数据
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogUtil = DialogUtil.getInstance(LoginActivity.this);
    }
    private void init() {
    }

    private void bindLisenter() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accountStr = accountEdit.getText().toString();
                String pwdStr = pwdEdit.getText().toString();

                if (accountStr.length() == 0){
                    dialogUtil.showAlertDialogHint("提示","账号不能为空");
                    return;
                }
                if (pwdStr.length() == 0){
                    dialogUtil.showAlertDialogHint("提示","密码不能为空");
                    return;
                }
                if (checkLogin(accountStr,pwdStr)){
                    //验证成功
                    SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance(LoginActivity.this);
                    sharedPreferencesUtil.SaveData("account",accountStr);
                    sharedPreferencesUtil.SaveData("pwd",pwdStr);
                    dialogUtil.showToast("验证成功");

                    LoginActivity.this.finish();
                } else {
                    //验证失败
                    dialogUtil.showAlertDialogHint("提示","密码或账号错误");
                }
            }
        });
        resgisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ResgisterActivity.class);
                startActivity(intent);
            }
        });
        forgetPwdText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgetPwdActivity.class);
                startActivity(intent);
            }
        });
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
        return false;
    }



    private void bindViews() {
        accountEdit = (EditText)this.findViewById(R.id.edit_account);
        pwdEdit = (EditText)this.findViewById(R.id.edit_pwd);

        loginBtn = (Button) this.findViewById(R.id.btn_login);

        resgisterText = (TextView) this.findViewById(R.id.text_resgister);
        forgetPwdText = (TextView) this.findViewById(R.id.text_forgetpwd);
    }
    //返回函数声明
    public void DoBack(View view){
        LoginActivity.this.finish();
    }
}

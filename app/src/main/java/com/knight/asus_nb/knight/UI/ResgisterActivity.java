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

public class ResgisterActivity extends AppCompatActivity {

    //输入控件声明
    private EditText accountEdit,phoneEdit,eamilEdit,pwdEdit;
    //确认按钮声明
    private Button confirmBtn;
    //窗口提示助手
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister);
        //绑定视图
        bindViews();
        //设置监听
        bindLisenters();
        //初始化数据
        init();
    }

    private void init() {
        dialogUtil = DialogUtil.getInstance(ResgisterActivity.this);
    }


    private void bindLisenters() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput(accountEdit) &&
                        checkInput(phoneEdit)  &&
                        checkInput(eamilEdit) &&
                        checkInput(pwdEdit) ){

                    regesterUser(accountEdit.getText().toString(),
                            phoneEdit.getText().toString(),
                            eamilEdit.getText().toString(),
                            pwdEdit.getText().toString());
                }
            }
        });
    }

    /**
     * 注册账号
     * @return false - 用户已存在 true - 注册成功
     */
    private boolean regesterUser(String account,String phone,String eamil,String pwd){
        List<User> userList = LitePal.where("accountStr=?",account).find(User.class);
        if (userList != null && userList.size() != 0){
            dialogUtil.showAlertDialogHint("提示","账号已被占用");
            return false;
        }
        userList = LitePal.where("phoneStr=?",phone).find(User.class);
        if (userList != null && userList.size() != 0){
            dialogUtil.showAlertDialogHint("提示","电话已被占用");
            return false;
        }
        userList = LitePal.where("emailStr=?", eamil).find(User.class);
        if (userList != null && userList.size() != 0){
            dialogUtil.showAlertDialogHint("提示","邮箱已被占用");
            return false;
        }
        User user = new User();
        user.setAccountStr(account);
        user.setPhoneStr(phone);
        user.setEmailStr(eamil);
        user.setPasswordStr(pwd);
        user.save();
        dialogUtil.showAlertDialogHint("提示","注册成功");
        return true;
    }

    private void bindViews() {
        accountEdit = (EditText)this.findViewById(R.id.edit_account);
        phoneEdit = (EditText)this.findViewById(R.id.edit_phone);
        eamilEdit = (EditText)this.findViewById(R.id.edit_eamil);
        pwdEdit = (EditText)this.findViewById(R.id.edit_pwd);
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
        ResgisterActivity.this.finish();
    }
}

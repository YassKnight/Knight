package com.knight.asus_nb.knight.Util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DialogUtil {
    private static Context mContext;
    private  static DialogUtil instance;
    private static AlertDialog alertDialog;
    private static ProgressDialog progressDialog;

    private DialogUtil() {

    }

    public static DialogUtil getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new DialogUtil();
        }
        return instance;
    }

    /**
     * 显示加载型提示框
     * @param msg 提示内容
     */
    public  void showLoadding(String msg) {
        if (mContext != null){
            progressDialog = new ProgressDialog(mContext);
        }
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    /**
     * 弹出对话提示框
     * @param title  标题
     * @param msg 内容
     */
    public  void showAlertDialogHint(String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog =  builder.create();
        alertDialog.show();
    }

    /**
     * 弹出对话提示框
     * @param title  标题
     * @param msg 内容
     */
    public  void showAlertDialog(String title, String msg, final CallBack callBack){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callBack.getResult(CallBack.SUCCESS);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callBack.getResult(CallBack.FAIL);
            }
        });
        alertDialog =  builder.create();
        alertDialog.show();
    }

    /**
     * 显示集合对话弹窗
     * @param title 标题
     * @param content 内容
     * @param callBack 成功回调
     */
    public  void showItemAlertDialog(String title,String[] content,DialogInterface.OnClickListener callBack){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setItems(content,callBack);
        alertDialog =  builder.create();
        alertDialog.show();
    }

    /**
     * 吐司提示
     * @param msg 提示内容
     */
    public  void showToast(String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 隐藏加载型提示框
     * @return 是否成功
     */
    public  boolean hideLoadding() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            return true;
        }
        return false;
    }
    /**
     * 隐藏对话型提示框
     * @return 是否成功
     */
    public  boolean hideAlertDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            return true;
        }
        return false;
    }
}

package com.knight.asus_nb.knight.db;

import org.litepal.crud.LitePalSupport;

/**
 * 用户数据
 */
public class User extends LitePalSupport {
    private long uid; //用户唯一id
    private String phoneStr = "";//用户电话号码
    private String showNameStr = ""; //用户名称
    private String accountStr = ""; //用户账号
    private String emailStr = "";//用户邮箱
    private String passwordStr = ""; // 用户密码
    private String userDesc = ""; //用户简介
    private String userHeadUrl = null; //用户头像路径
    private String userAdd = ""; //用户地址
    private long meilsum;       //总运动里程  单位 米
    private long timesum;       //总运动时间  单位 秒

    public void addmeil(long meil){
        meilsum+=meil;
    }
    public void addtime(long second){
        timesum+=second;
    }
    public long getMeilsum() {
        return meilsum;
    }

    public void setMeilsum(long meilsum) {
        this.meilsum = meilsum;
    }

    public long getTimesum() {
        return timesum;
    }

    public void setTimesum(long timesum) {
        this.timesum = timesum;
    }

    public long getId(){
        return getBaseObjId();
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getPhoneStr() {
        return phoneStr;
    }

    public void setPhoneStr(String phoneStr) {
        this.phoneStr = phoneStr;
    }

    public String getShowNameStr() {
        return showNameStr;
    }

    public void setShowNameStr(String showNameStr) {
        this.showNameStr = showNameStr;
    }

    public String getAccountStr() {
        return accountStr;
    }

    public void setAccountStr(String accountStr) {
        this.accountStr = accountStr;
    }

    public String getEmailStr() {
        return emailStr;
    }

    public void setEmailStr(String emailStr) {
        this.emailStr = emailStr;
    }

    public String getPasswordStr() {
        return passwordStr;
    }

    public void setPasswordStr(String passwordStr) {
        this.passwordStr = passwordStr;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getUserHeadUrl() {
        return userHeadUrl;
    }

    public void setUserHeadUrl(String userHeadUrl) {
        this.userHeadUrl = userHeadUrl;
    }

    public String getUserAdd() {
        return userAdd;
    }

    public void setUserAdd(String userAdd) {
        this.userAdd = userAdd;
    }
}

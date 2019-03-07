package com.knight.asus_nb.knight.db;

import org.litepal.crud.LitePalSupport;

/**
 * 路线集合数据
 */
public class RoadLineList extends LitePalSupport {
    private String nameStr; //路线集合名称
    private String lineDesc; //路线集合简介
    private int rid;  //路线集合id
    private String lineImgUrl; //线路集合主图片路径
    private long uid; //用户唯一id
    private String userName; //发布用户名称
    private String userHeadImg; //用户头像路径
    public long getId(){
        return getBaseObjId();
    }
    public String getNameStr() {
        return nameStr;
    }

    public void setNameStr(String nameStr) {
        this.nameStr = nameStr;
    }

    public String getLineDesc() {
        return lineDesc;
    }

    public void setLineDesc(String lineDesc) {
        this.lineDesc = lineDesc;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getLineImgUrl() {
        return lineImgUrl;
    }

    public void setLineImgUrl(String lineImgUrl) {
        this.lineImgUrl = lineImgUrl;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg) {
        this.userHeadImg = userHeadImg;
    }
}

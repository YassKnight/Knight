package com.knight.asus_nb.knight.db;

import org.litepal.crud.LitePalSupport;
//评论数据
public class Comment extends LitePalSupport {
    private int cid;  //评价唯一id
    private String contentStr; //评论内容
    private long commentTime; //评论时间；
    private long roid; //评论线路id
    private long repCid; //回复评论的id
    private long uid; //用户唯一id
    private String userName; //发布用户名称
    private String userHeadImg; //用户头像路径

    public long getId(){
        return getBaseObjId();
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }

    public long getRoid() {
        return roid;
    }

    public void setRoid(long roid) {
        this.roid = roid;
    }

    public long getRepCid() {
        return repCid;
    }

    public void setRepCid(long repCid) {
        this.repCid = repCid;
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

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}

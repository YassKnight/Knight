package com.knight.asus_nb.knight.db;

import org.litepal.crud.LitePalSupport;

/**
 * 单条路线数据
 */
public class RoadLine extends LitePalSupport {
    private String roadLineName; //线路名称
    private double meilageDoub; //线路总里程
    private double altitudeDiff; //海拔差值
    private int roid; //总评论数
    private long uid; //用户唯一id
    private long roadlistid; //所属集合id
    private String userName; //发布用户名称
    private String userHeadImg; //用户头像路径
    private String lineImgURL; //线路主图片
    private String startLocat; //起点位置 格式：经度，纬度
    private String endLocat; //终点位置 格式：经度，纬度
    private String descStr;  //线路描述
    private double startLocLatDouble;  //开始坐标经度
    private double startLocLonDouble; //开始坐标纬度
    private double endLocLatDouble;  //开始坐标经度
    private double endLocLonDouble; //开始坐标纬度

    public long getRoadlistid() {
        return roadlistid;
    }

    public void setRoadlistid(long roadlistid) {
        this.roadlistid = roadlistid;
    }

    public String getDescStr() {
        return descStr;
    }

    public void setDescStr(String descStr) {
        this.descStr = descStr;
    }

    public long getId(){
        return getBaseObjId();
    }
    public String getLineImgURL() {
        return lineImgURL;
    }

    public void setLineImgURL(String lineImgURL) {
        this.lineImgURL = lineImgURL;
    }

    public String getStartLocat() {
        return startLocat;
    }

    public void setStartLocat(String startLocat) {
        this.startLocat = startLocat;
        String[] locs = startLocat.split(",");
        this.startLocLatDouble = Double.parseDouble(locs[0]);
        this.startLocLonDouble = Double.parseDouble(locs[1]);
    }

    public String getEndLocat() {
        return endLocat;
    }

    public void setEndLocat(String endLocat) {
        this.endLocat = endLocat;
        String[] locs = endLocat.split(",");
        this.endLocLatDouble = Double.parseDouble(locs[0]);
        this.endLocLonDouble = Double.parseDouble(locs[1]);
    }

    public String getRoadLineName() {
        return roadLineName;
    }

    public void setRoadLineName(String roadLineName) {
        this.roadLineName = roadLineName;
    }

    public double getMeilageDoub() {
        return meilageDoub;
    }

    public void setMeilageDoub(double meilageDoub) {
        this.meilageDoub = meilageDoub;
    }

    public double getAltitudeDiff() {
        return altitudeDiff;
    }

    public void setAltitudeDiff(double altitudeDiff) {
        this.altitudeDiff = altitudeDiff;
    }

    public int getCommentNum() {
        return roid;
    }

    public void setCommentNum(int roid) {
        this.roid = roid;
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

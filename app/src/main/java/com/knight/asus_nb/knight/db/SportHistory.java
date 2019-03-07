package com.knight.asus_nb.knight.db;

import org.litepal.crud.LitePalSupport;

/**
 * 运动记录数据
 */
public class SportHistory extends LitePalSupport {
    private long uid;  //用户id
    private double meil; //里程
    private long sporttime; //运动时间
    private long starttime; //开始时间
    private long endtime; //结束时间
    private String startLoc; //起点位置
    private String endLoc; //终点位置
    private String mapImgUrl; //地图
    private double startLocLatDouble;  //开始坐标经度
    private double startLocLonDouble; //开始坐标纬度
    private double endLocLatDouble;  //开始坐标经度
    private double endLocLonDouble; //开始坐标纬度

    public String getMapImgUrl() {
        return mapImgUrl;
    }

    public void setMapImgUrl(String mapImgUrl) {
        this.mapImgUrl = mapImgUrl;
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

    public double getMeil() {
        return meil;
    }

    public void setMeil(double meil) {
        this.meil = meil;
    }
    public void addMeil(double meil) {
        this.meil += meil;
    }

    public long getSporttime() {
        return sporttime;
    }

    public void setSporttime(long sporttime) {
        this.sporttime = sporttime / 1000;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
        if (starttime != 0) setSporttime(endtime - starttime);
    }

    public String getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(String startLoc) {
        this.startLoc = startLoc;
        String[] locs = startLoc.split(",");
        this.startLocLatDouble = Double.parseDouble(locs[0]);
        this.startLocLonDouble = Double.parseDouble(locs[1]);
    }

    public String getEndLoc() {
        return endLoc;
    }

    public void setEndLoc(String endLoc) {
        this.endLoc = endLoc;
        String[] locs = endLoc.split(",");
        this.endLocLatDouble = Double.parseDouble(locs[0]);
        this.endLocLonDouble = Double.parseDouble(locs[1]);
    }
}

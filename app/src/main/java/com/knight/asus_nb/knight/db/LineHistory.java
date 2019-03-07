package com.knight.asus_nb.knight.db;

import org.litepal.crud.LitePalSupport;

public class LineHistory extends LitePalSupport {
    private long uid;
    private double lat;
    private double lon;
    private long time;

    public long getId(){
        return getBaseObjId();
    }
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

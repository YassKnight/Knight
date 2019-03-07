package com.knight.asus_nb.knight.application;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePalApplication;

public class Application extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}

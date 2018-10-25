package com.knight.asus_nb.knight.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.List;

public class MyPageAdapter extends PagerAdapter {
    private List<View> viewList;
    public MyPageAdapter( List<View> viewList) {
        this.viewList=viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

}

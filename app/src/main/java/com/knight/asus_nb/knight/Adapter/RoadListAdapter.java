package com.knight.asus_nb.knight.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.db.RoadLine;
import com.knight.asus_nb.knight.db.RoadLineList;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoadListAdapter extends BaseAdapter {

    private List<RoadLineList> roadLineList;  //集合数据列表
    private Activity mContext;
    private LayoutInflater mLayoutInlater;

    public RoadListAdapter(Activity activity){
        this.mContext = activity;
        roadLineList = new ArrayList<RoadLineList>();
        mLayoutInlater = activity.getLayoutInflater();
    }
    public boolean delete(int i){
        if (roadLineList.get(i) != null){
            RoadLineList roadLine = roadLineList.get(i);
            roadLineList.remove(i);
            roadLine.delete();
            notifyDataSetChanged();
            return true;
        }
        return false;
    }
    public void AddItem(RoadLineList roadLine){
        roadLineList.add(roadLine);
    }
    public List<RoadLineList> getRoadLineList() {
        return roadLineList;
    }

    public void setRoadLineList(List<RoadLineList> roadLineList) {
        this.roadLineList = roadLineList;
    }

    @Override
    public int getCount() {
        return roadLineList.size();
    }

    @Override
    public RoadLineList getItem(int i) {
        return roadLineList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewholder;
        if (view == null){
            //如果当前view没有创建即创建View
            //实例化对象
            viewholder = new ViewHolder();
            //加载view
            view = mLayoutInlater.inflate(R.layout.item_saiduan,null);
            //绑定view
            viewholder.showImg = (RoundedImageView)view.findViewById(R.id.item_img_main);
            viewholder.nameText = (TextView) view.findViewById(R.id.item_text_name);
            //将viewholder数据存在view中
            view.setTag(viewholder);
        } else {
            //有创建view从view中获取数据
            viewholder = (ViewHolder) view.getTag();
        }
        //拿到对应数列号的单个数据
        RoadLineList roadLine = roadLineList.get(i);
        //根据图片路径设置图片信息
        if (roadLine.getLineImgUrl() == null || roadLine.getLineImgUrl().length() == 0){
            viewholder.showImg.setImageResource(R.mipmap.ic_fengjing);
        }else {
            try{
                Uri uri = Uri.parse(roadLine.getLineImgUrl());
                viewholder.showImg.setImageURI(uri);
            }catch (Exception e){
                viewholder.showImg.setImageResource(R.mipmap.ic_fengjing);
            }
        }
        //设置路线名称
        viewholder.nameText.setText(roadLine.getNameStr());
        return view;
    }

    class ViewHolder{
        RoundedImageView showImg;
        TextView nameText;
    }
}

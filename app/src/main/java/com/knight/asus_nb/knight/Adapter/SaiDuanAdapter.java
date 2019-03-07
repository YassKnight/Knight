package com.knight.asus_nb.knight.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.db.RoadLine;
import com.knight.asus_nb.knight.db.SportHistory;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SaiDuanAdapter extends BaseAdapter {

    private List<RoadLine> roadLineList;  //路书数据列表
    private LayoutInflater mLayoutInflater;
    private DecimalFormat df = new   DecimalFormat("#.##");

    public SaiDuanAdapter(Activity activity){
        this.mLayoutInflater = activity.getLayoutInflater();
        roadLineList = new ArrayList<RoadLine>();
    }
    //删除
    public boolean deleteItem(int i){
        if ( roadLineList.get(i) != null){
            RoadLine roadLine = roadLineList.get(i);
            roadLineList.remove(i);
            roadLine.delete();
            notifyDataSetChanged();
            return true;
        }
       return false;
    }
    public List<RoadLine> getRoadLineList() {
        return roadLineList;
    }

    public void setRoadLineList(List<RoadLine> roadLineList) {
        this.roadLineList = roadLineList;
    }

    @Override
    public int getCount() {
        return roadLineList.size();
    }

    @Override
    public RoadLine getItem(int i) {
        return roadLineList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.item_saiduanline,null);
            viewHolder.roadIdText = (TextView)view.findViewById(R.id.text_roadid);
            viewHolder.userHeadImg = (RoundedImageView) view.findViewById(R.id.roundimg_userhead);
            viewHolder.userNameText = (TextView)view.findViewById(R.id.text_username);
            viewHolder.meilageText = (TextView)view.findViewById(R.id.text_meilage);
            viewHolder.conmentNumber = (TextView)view.findViewById(R.id.text_msg_number);
            viewHolder.itemRel = (RelativeLayout)view.findViewById(R.id.item_rel);
            viewHolder.line_img = (ImageView) view.findViewById(R.id.line_img);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        RoadLine roadLine = roadLineList.get(i);
        if (roadLine == null){
            return view;
        }
        //显示id
        viewHolder.roadIdText.setText("#" + roadLine.getId());
        //显示头像
        if (roadLine.getUserHeadImg() != null && roadLine.getUserHeadImg().length() != 0){
            viewHolder.userHeadImg.setImageURI(Uri.parse(roadLine.getUserHeadImg()));
        } else {
            viewHolder.userHeadImg.setImageResource(R.mipmap.v1_profile_photo_2x);
        }
        //显示路书图片
        if (roadLine.getLineImgURL() != null && roadLine.getLineImgURL().length() != 0){
            viewHolder.line_img.setImageURI(Uri.parse(roadLine.getLineImgURL()));
        } else {
            viewHolder.line_img.setImageResource(R.mipmap.ic_fengjing);
        }
        //显示名称
        viewHolder.userNameText.setText(roadLine.getUserName());

        viewHolder.meilageText.setText(df.format(roadLine.getMeilageDoub()) + "Km");
        viewHolder.conmentNumber.setText(roadLine.getCommentNum() + "");

        return view;
    }
    class ViewHolder{
        TextView roadIdText;
        RoundedImageView userHeadImg;
        TextView userNameText;
        TextView meilageText;
        TextView conmentNumber;
        RelativeLayout itemRel;
        ImageView line_img;
    }
}

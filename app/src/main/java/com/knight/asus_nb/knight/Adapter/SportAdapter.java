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
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLineList;
import com.knight.asus_nb.knight.db.SportHistory;
import com.knight.asus_nb.knight.db.SportHistory;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SportAdapter extends BaseAdapter {

    private List<SportHistory> SportHistoryList;   //运动记录数据列表
    private LayoutInflater mLayoutInflater;
    private DecimalFormat df = new   DecimalFormat("#.##");

    public SportAdapter(Activity activity){
        this.mLayoutInflater = activity.getLayoutInflater();
        SportHistoryList = new ArrayList<SportHistory>();
    }
    public boolean delete(int i){
        if (SportHistoryList.get(i) != null){
            SportHistory sport = SportHistoryList.get(i);
            SportHistoryList.remove(i);
            sport.delete();
            notifyDataSetChanged();
            return true;
        }
        return false;
    }
    public List<SportHistory> getSportHistoryList() {
        return SportHistoryList;
    }

    public void setSportHistoryList(List<SportHistory> SportHistoryList) {
        this.SportHistoryList = SportHistoryList;
    }

    @Override
    public int getCount() {
        return SportHistoryList.size();
    }

    @Override
    public SportHistory getItem(int i) {
        return SportHistoryList.get(i);
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
            view = mLayoutInflater.inflate(R.layout.item_yundongjilu,null);
            viewHolder.roadIdText = (TextView)view.findViewById(R.id.text_roadid);
            viewHolder.userHeadImg = (RoundedImageView) view.findViewById(R.id.roundimg_userhead);
            viewHolder.userNameText = (TextView)view.findViewById(R.id.text_username);
            viewHolder.meilageText = (TextView)view.findViewById(R.id.text_meil);
            viewHolder.timeText = (TextView)view.findViewById(R.id.text_time);
            viewHolder.suduText = (TextView)view.findViewById(R.id.text_sudu);
            viewHolder.line_img = (ImageView) view.findViewById(R.id.line_img);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        SportHistory SportHistory = SportHistoryList.get(i);
        //显示id
        viewHolder.roadIdText.setText("#" + SportHistory.getId());
        //显示头像
        if (GlobalData.currentUser.getUserHeadUrl() != null && GlobalData.currentUser.getUserHeadUrl().length() != 0){
            viewHolder.userHeadImg.setImageURI(Uri.parse(GlobalData.currentUser.getUserHeadUrl()));
        } else {
            viewHolder.userHeadImg.setImageResource(R.mipmap.v1_profile_photo_2x);
        }
        //显示运动记录图片
        if (SportHistory.getMapImgUrl() != null && SportHistory.getMapImgUrl().length() != 0){
            viewHolder.line_img.setImageURI(Uri.parse(SportHistory.getMapImgUrl()));
        } else {
            viewHolder.line_img.setImageResource(R.mipmap.ic_fengjing);
        }
        viewHolder.userNameText.setText(GlobalData.currentUser.getAccountStr());
        //显示里程
        viewHolder.meilageText.setText(df.format(SportHistory.getMeil() / 1000) + "Km");
        //显示时间
        long yundongTime = SportHistory.getSporttime();
        int hour = (int)(yundongTime / 3600);
        int min = (int)(yundongTime % 3600 / 60);
        int second = (int)(yundongTime % 3600 % 60);
        String hourStr = (hour + "").length() < 2?"0" + hour:hour + "";
        String minStr = (min + "").length() < 2?"0" + min:min + "";
        String secondStr = (second + "").length() < 2?"0" + second:second + "";
        viewHolder.timeText .setText(hourStr + ":" + minStr + ":" + secondStr);
        double suduDouble =  SportHistory.getMeil() * 1000/ (System.currentTimeMillis() - SportHistory.getStarttime()) ;
        viewHolder.suduText.setText(df.format(suduDouble) + "m/s");

        return view;
    }
    class ViewHolder{
        TextView roadIdText;
        RoundedImageView userHeadImg;
        TextView userNameText;
        TextView meilageText;
        TextView timeText;
        TextView suduText;
        ImageView line_img;
    }
}

package com.knight.asus_nb.knight.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.db.Comment;
import com.knight.asus_nb.knight.db.User;
import com.makeramen.roundedimageview.RoundedImageView;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class CommentAdapter extends BaseAdapter {

    private List<Comment> CommentList;      //评论数据列表
    private LayoutInflater mLayoutInflater; //视图加载器
    private Handler mHandler;       //消息传送器
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:SS");  //时间显示格式
    public CommentAdapter(Activity activity) {
        this.mLayoutInflater = activity.getLayoutInflater();
        CommentList = new ArrayList<Comment>();

    }
    //添加评论
    public void addData(Comment comment){
        CommentList.add(comment);
        notifyDataSetChanged();
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public List<Comment> getCommentList() {
        return CommentList;
    }

    public void setCommentList(List<Comment> CommentList) {
        this.CommentList = CommentList;
    }

    @Override
    public int getCount() {
        return CommentList.size();
    }

    @Override
    public Object getItem(int i) {
        return CommentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.item_comment, null);
            viewHolder.userNameText = (TextView) view.findViewById(R.id.text_username);
            viewHolder.userHeadImg = (RoundedImageView) view.findViewById(R.id.img_userhead);
            viewHolder.commentTime = (TextView) view.findViewById(R.id.text_comment_time);
            viewHolder.commentContext = (TextView) view.findViewById(R.id.text_comment);
            viewHolder.repImg = (ImageView) view.findViewById(R.id.img_rep);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
       final Comment Comment = CommentList.get(i);

        viewHolder.userNameText.setText(Comment.getUserName());
        //显示头像
        if (Comment.getUserHeadImg() != null && Comment.getUserHeadImg().length() != 0){
            viewHolder.userHeadImg.setImageURI(Uri.parse(Comment.getUserHeadImg()));
        } else {
            viewHolder.userHeadImg.setImageResource(R.mipmap.v1_profile_photo_2x);
        }
        //显示时间
        viewHolder.commentTime.setText(dateFormat.format(new Date(Comment.getCommentTime())));
        if (Comment.getRepCid() != 0){
            User user = LitePal.find(User.class,Comment.getRepCid());
            viewHolder.commentContext.setText("回复"+ user.getAccountStr() + ":" + Comment.getContentStr());
        } else {
            viewHolder.commentContext.setText(Comment.getContentStr());
        }
        //发送消息
        viewHolder.repImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null){
                    Message message = new Message();
                    message.obj = Comment;
                    message.what = 101;
                    mHandler.sendMessage(message);
                }
            }
        });

        return view;
    }

    class ViewHolder {
        TextView userNameText;          //用户名称
        RoundedImageView userHeadImg;   //用户头像
        TextView commentTime;             //评论时间
        TextView commentContext;        //评论内容
        ImageView repImg;               //回复图标
    }
}

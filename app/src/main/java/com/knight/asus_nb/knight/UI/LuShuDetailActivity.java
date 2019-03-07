package com.knight.asus_nb.knight.UI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.knight.asus_nb.knight.Adapter.CommentAdapter;
import com.knight.asus_nb.knight.CoustomUi.ScorllListView;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.Comment;
import com.knight.asus_nb.knight.db.User;
import com.makeramen.roundedimageview.RoundedImageView;

import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.util.List;

public class LuShuDetailActivity extends AppCompatActivity {

    //评价集合
    private ScorllListView commentList;
    private CommentAdapter Adapter;
    private ScrollView scroll;
    //显示数据声明
    private TextView lineNameText,lineidText,lichengText,renliangText,useTimeText,commentNum,userNameText,userDescText;
    //用户头像
    private RoundedImageView userImg,lineImg;
    private SwipeRefreshLayout swipeRefresh;
    //评论编辑
    private EditText commentEdit;
    //发送按钮
    private Button sendBtn;
    private DialogUtil dialogUtil;
    private Handler mHandler;
    private Comment repComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lushudetail);
        bindViews();
        bindLisenters();
        init();

    }

    private void init() {
        scroll.scrollTo(0,0);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case 101:{
                        repComment = (Comment) message.obj;
                        commentEdit.setText("");
                        commentEdit.setHint("回复" + repComment.getUserName());
                        commentEdit.setFocusable(true);
                        commentEdit.setFocusableInTouchMode(true);
                        commentEdit.requestFocus();
                    }
                    break;
                }
                return false;
            }
        });
    }

    private void refreshData() {

        if (GlobalData.currentShowRoadLine.getLineImgURL() != null && GlobalData.currentShowRoadLine.getLineImgURL().length() != 0){
            lineImg.setImageURI(Uri.parse(GlobalData.currentShowRoadLine.getLineImgURL()));
        } else {
            lineImg.setImageResource(R.mipmap.ic_fengjing);
        }
        User user = LitePal.find(User.class,GlobalData.currentShowRoadLine.getUid());
        if (user.getUserHeadUrl() != null && user.getUserHeadUrl().length() != 0){
            userImg.setImageURI(Uri.parse(user.getUserHeadUrl()));
        } else {
            userImg.setImageResource(R.mipmap.v1_profile_photo_2x);
        }
        userDescText.setText(user.getUserDesc());
        userNameText.setText(GlobalData.currentShowRoadLine.getUserName());
        lineNameText.setText(GlobalData.currentShowRoadLine.getRoadLineName());
        lineidText.setText( "#" + GlobalData.currentShowRoadLine.getId());
        DecimalFormat df = new  DecimalFormat("#.##");
        lichengText.setText(df.format(GlobalData.currentShowRoadLine.getMeilageDoub() ) + "Km");
        double timeDouble =  GlobalData.currentShowRoadLine.getMeilageDoub()  * 1000/ 4;
        renliangText.setText((int) (timeDouble * 0.16388889) + "卡路里");
        int hour = (int)(timeDouble / 3600);
        int min = (int)(timeDouble % 3600 / 60);
        if (hour >= 1){
            useTimeText.setText(hour + "小时" + min + "分钟");
        } else {
            useTimeText.setText( min + "分钟");
        }
        commentNum.setText(GlobalData.currentShowRoadLine.getCommentNum() + "");
    }

    private void bindLisenters() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryComment();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null){
                    dialogUtil.showAlertDialogHint("提示","用户未登录");
                    return;
                }
                String editStr = commentEdit.getText().toString();
                if (editStr.length() == 0){
                    dialogUtil.showAlertDialogHint("提示","输入不能为空");
                    return;
                }
                Comment comment = new Comment();
                comment.setCommentTime(System.currentTimeMillis());
                comment.setContentStr(editStr);
                comment.setUid(GlobalData.currentUser.getId());
                comment.setRoid(GlobalData.currentShowRoadLine.getId());
                comment.setUserHeadImg(GlobalData.currentUser.getUserHeadUrl());
                comment.setUserName(GlobalData.currentUser.getAccountStr());
                if (repComment != null){
                    comment.setRepCid(repComment.getRepCid());
                }
                repComment = null;
                comment.save();
                GlobalData.currentShowRoadLine.setCommentNum(GlobalData.currentShowRoadLine.getCommentNum() + 1);
                GlobalData.currentShowRoadLine.save();
                Adapter.addData(comment);
                commentNum.setText(GlobalData.currentShowRoadLine.getCommentNum() + "");
                commentEdit.setText("");
                sendBtn.requestFocus();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryComment();
        dialogUtil = DialogUtil.getInstance(LuShuDetailActivity.this);
        refreshData();
    }

    /**
     * 查询评论
     */
    private void queryComment(){
        List<Comment> commentList = LitePal.where("roid=?",GlobalData.currentShowRoadLine.getId() + "").find(Comment.class);
        refreshList(commentList);
    }

    private void refreshList(List<Comment> comments) {
        if (Adapter == null){
            Adapter = new CommentAdapter(LuShuDetailActivity.this);
            Adapter.setmHandler(mHandler);
            commentList.setAdapter(Adapter);
        }
        Adapter.setCommentList(comments);
        Adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    private void bindViews() {
        swipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        commentList = (ScorllListView) this.findViewById(R.id.list_comment);
        lineNameText = (TextView)this.findViewById(R.id.text_roadname);
        lineidText = (TextView)this.findViewById(R.id.text_roadid);
        lichengText = (TextView)this.findViewById(R.id.text_licheng);
        renliangText = (TextView)this.findViewById(R.id.text_xiaohao);
        useTimeText = (TextView)this.findViewById(R.id.text_time);
        commentNum = (TextView)this.findViewById(R.id.text_com_num);
        scroll = (ScrollView)this.findViewById(R.id.scroll);
        commentEdit = (EditText) this.findViewById(R.id.edit_comment);
        sendBtn = (Button) this.findViewById(R.id.btn_send);
        userImg = (RoundedImageView) this.findViewById(R.id.img_userimg);
        userNameText = (TextView)this.findViewById(R.id.text_username);
        userDescText  = (TextView)this.findViewById(R.id.text_userdesc);
        lineImg  = (RoundedImageView) this.findViewById(R.id.img_roadimg);
    }

    public void DoBack(View view){
        LuShuDetailActivity.this.finish();
    }
    public void DoShare(View view){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(GlobalData.currentShowRoadLine.getLineImgURL()));
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "路书详情"));
    }
}

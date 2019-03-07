package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.knight.asus_nb.knight.Adapter.CommentAdapter;
import com.knight.asus_nb.knight.Adapter.SaiDuanAdapter;
import com.knight.asus_nb.knight.CoustomUi.ScorllListView;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLine;
import com.makeramen.roundedimageview.RoundedImageView;
import org.litepal.LitePal;

import java.util.List;

public class RoadLineListDetailActivity extends AppCompatActivity {

    //评价集合
    private ScorllListView roadlineList;
    private SaiDuanAdapter Adapter;
    private ScrollView scroll;
    //显示数据声明
    private TextView lineNameText, userNameText, descText;
    //用户头像
    private RoundedImageView userImg;
    private ImageView roadImg,addImg;
    private SwipeRefreshLayout swipeRefresh;
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadlistdetail);
        bindViews();
        bindLisenters();
        init();
    }

    private void init() {
        scroll.scrollTo(0,0);

    }

    private void refreshData() {
        if (GlobalData.currentShowRoadLineList == null){
            dialogUtil.showAlertDialogHint("提示","未选择集合");
            return;
        }
        userImg.setImageURI(Uri.parse(GlobalData.currentShowRoadLineList.getUserHeadImg()));
        lineNameText.setText(GlobalData.currentShowRoadLineList.getNameStr());
        userNameText.setText( GlobalData.currentShowRoadLineList.getUserName());
        roadImg.setImageURI(Uri.parse(GlobalData.currentShowRoadLineList.getLineImgUrl()));
        descText.setText("简介" + GlobalData.currentShowRoadLineList.getLineDesc());
    }

    private void bindLisenters() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryRoadLine();
            }
        });
        roadlineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalData.currentShowRoadLine = Adapter.getItem(i);
                Intent intent = new Intent(RoadLineListDetailActivity.this,LuShuDetailActivity.class);
                startActivity(intent);
            }
        });
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoadLineListDetailActivity.this,AddLuActivity.class);
                intent.putExtra("roadid",GlobalData.currentShowRoadLineList.getId());

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryRoadLine();
        dialogUtil = DialogUtil.getInstance(RoadLineListDetailActivity.this);
        refreshData();
    }

    /**
     * 查询评论
     */
    private void queryRoadLine(){
        List<RoadLine> roadLineList = LitePal.where("roadlistid=?",GlobalData.currentShowRoadLineList.getId() + "").find(RoadLine.class);
        refreshList(roadLineList);
    }

    private void refreshList(List<RoadLine> roadLineList) {
        if (Adapter == null){
            Adapter = new SaiDuanAdapter(RoadLineListDetailActivity.this);
            roadlineList.setAdapter(Adapter);
        }
        Adapter.setRoadLineList(roadLineList);
        Adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    private void bindViews() {
        swipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        roadlineList = (ScorllListView) this.findViewById(R.id.list_roadline);
        lineNameText = (TextView)this.findViewById(R.id.item_text_name);
        userNameText = (TextView)this.findViewById(R.id.text_username);
        descText = (TextView)this.findViewById(R.id.text_desc);
        scroll = (ScrollView)this.findViewById(R.id.scroll);
        roadImg  = (ImageView) this.findViewById(R.id.img_roadimg);
        userImg = (RoundedImageView) this.findViewById(R.id.img_userimg);
        addImg = (ImageView) this.findViewById(R.id.img_add);
    }

    public void DoBack(View view){
        RoadLineListDetailActivity.this.finish();
    }
    public void DoShare(View view){

    }
}

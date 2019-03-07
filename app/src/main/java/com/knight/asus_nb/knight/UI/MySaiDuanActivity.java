package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.knight.asus_nb.knight.Adapter.SaiDuanAdapter;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.CallBack;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLine;

import org.litepal.LitePal;

import java.util.List;

public class MySaiDuanActivity extends AppCompatActivity {
    //下来刷新控件声明
    private SwipeRefreshLayout swipeRefresh,swipeRefreshNoItem;
    private ListView saiDunLineList;
    private SaiDuanAdapter saiDuanAdapter;
    private ImageView addImg;
    private DialogUtil dialogUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysqiduan);
        bindViews();
        bindLisenters();
        init();
    }
    private void init() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        querySaidun();
        dialogUtil = DialogUtil.getInstance(MySaiDuanActivity.this);
    }

    private void bindLisenters() {
        saiDunLineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalData.currentShowRoadLine = saiDuanAdapter.getItem(i);
                Intent intent = new Intent(MySaiDuanActivity.this,LuShuDetailActivity.class);
                startActivity(intent);
            }
        });
        saiDunLineList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                dialogUtil.showAlertDialog("提示", "确定删除？", new CallBack() {
                    @Override
                    public void getResult(int Type) {
                        if (Type == CallBack.SUCCESS){
                            saiDuanAdapter.deleteItem(i);
                        }
                    }
                });
                return true;
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                querySaidun();
            }
        });
        swipeRefreshNoItem.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                querySaidun();
            }
        });
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开添加界面
                Intent intent = new Intent(MySaiDuanActivity.this,AddLuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void bindViews() {
        addImg = (ImageView) this.findViewById(R.id.img_add);
        saiDunLineList = (ListView)this.findViewById(R.id.list_saiduanlushu);
        swipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeRefreshNoItem  = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh_noitem);
    }

    public void DoBack(View view){
        MySaiDuanActivity.this.finish();
    }

    //查询我的赛段
    private void querySaidun(){
        List<RoadLine> roadLines =  LitePal.where("uid=?",GlobalData.currentUser.getId() + "").find(RoadLine.class);
        refreshList(roadLines);
    }

    private void refreshList(List<RoadLine> roadLines) {
        if (saiDuanAdapter == null){
            saiDuanAdapter = new SaiDuanAdapter(MySaiDuanActivity.this);
            saiDunLineList.setAdapter(saiDuanAdapter);
        }
        if (roadLines.size() == 0){
            swipeRefreshNoItem.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshNoItem.setVisibility(View.GONE);
        }
        saiDuanAdapter.setRoadLineList(roadLines);
        saiDuanAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
        swipeRefreshNoItem.setRefreshing(false);
    }
}

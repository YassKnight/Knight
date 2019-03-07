package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.knight.asus_nb.knight.Adapter.SaiDuanAdapter;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLine;

import org.litepal.LitePal;

import java.util.List;


public class SaiDuanActivity extends AppCompatActivity {
    //下来刷新控件声明
    private SwipeRefreshLayout swipeRefresh,swipeRefreshNoItem;
    private ListView saiDunLineList;
    private SaiDuanAdapter saiDuanAdapter;
    private TextView mySaiDuanNumText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saiduan);
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
        queryMySaidun();
    }

    private void bindLisenters() {
        saiDunLineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalData.currentShowRoadLine = saiDuanAdapter.getItem(i);
                Intent intent = new Intent(SaiDuanActivity.this,LuShuDetailActivity.class);
                startActivity(intent);
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
    }
    public void GoMySaiDuan(View view){
        Intent intent = new Intent(SaiDuanActivity.this,MySaiDuanActivity.class);
        startActivity(intent);
    }
    private void bindViews() {
        mySaiDuanNumText = (TextView)this.findViewById(R.id.text_mysaiduan_num);
        saiDunLineList = (ListView)this.findViewById(R.id.list_saiduanlushu);
        swipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeRefreshNoItem  = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh_noitem);
    }

    public void DoBack(View view){
        SaiDuanActivity.this.finish();
    }

    //查询赛段前10的
    private void querySaidun(){
        List<RoadLine>  roadLines =  LitePal.limit(10).find(RoadLine.class);
        refreshList(roadLines);
    }

    //查询我的赛段
    private void queryMySaidun(){
        List<RoadLine> roadLines =  LitePal.where("uid=?",GlobalData.currentUser.getId() + "").find(RoadLine.class);
        mySaiDuanNumText.setText(roadLines.size() + "条");
    }

    private void refreshList(List<RoadLine> roadLines) {
        if (saiDuanAdapter == null){
            saiDuanAdapter = new SaiDuanAdapter(SaiDuanActivity.this);
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

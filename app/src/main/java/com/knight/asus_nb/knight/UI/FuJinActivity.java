package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.knight.asus_nb.knight.Adapter.SaiDuanAdapter;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLine;

import org.litepal.LitePal;

import java.util.List;

public class FuJinActivity extends AppCompatActivity {
    //下来刷新控件声明
    private SwipeRefreshLayout swipeRefresh,swipeRefreshNoItem;
    private ListView fujinLineList;
    private SaiDuanAdapter saiDuanAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fujinlushu);
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
    }

    private void bindLisenters() {
        fujinLineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalData.currentShowRoadLine = saiDuanAdapter.getItem(i);
                Intent intent = new Intent(FuJinActivity.this,LuShuDetailActivity.class);
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

    private void bindViews() {
        fujinLineList = (ListView)this.findViewById(R.id.list_fujinglushu);
        swipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeRefreshNoItem  = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh_noitem);
    }

    public void DoBack(View view){
        FuJinActivity.this.finish();
    }

    //查询赛段前10的
    private void querySaidun(){
        List<RoadLine>  roadLines =  LitePal.limit(10).find(RoadLine.class);
        refreshList(roadLines);
    }

    private void refreshList(List<RoadLine> roadLines) {
        if (saiDuanAdapter == null){
            saiDuanAdapter = new SaiDuanAdapter(FuJinActivity.this);
            fujinLineList.setAdapter(saiDuanAdapter);
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

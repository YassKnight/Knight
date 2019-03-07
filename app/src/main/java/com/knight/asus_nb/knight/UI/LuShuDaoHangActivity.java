package com.knight.asus_nb.knight.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.knight.asus_nb.knight.Adapter.SaiDuanAdapter;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLine;

import org.litepal.LitePal;

import java.util.List;

public class LuShuDaoHangActivity extends AppCompatActivity {
    //下来刷新控件声明
    private SwipeRefreshLayout swipeRefresh,swipeRefreshNoItem;
    private ListView fujinLineList;
    private SaiDuanAdapter saiDuanAdapter;
    //选择目的地控件声明
    private RelativeLayout selMudiRel;
    //选择导航
    public static final  int SEL_DAOHANG = 0;
    //选择路书
    public static final  int SEL_LUSHU = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lushudaohang);
        bindViews();
        bindLisenters();
        init();
    }
    private void init() {
        GlobalData.mapfragmentcode = 2;
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
                GlobalData.currentDaoHangRoadLine = saiDuanAdapter.getItem(i);
                GlobalData.mapfragmentcode =  SEL_LUSHU;
                LuShuDaoHangActivity.this.finish();
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
        selMudiRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalData.mapfragmentcode =  SEL_DAOHANG;
                LuShuDaoHangActivity.this.finish();
            }
        });
    }


    private void bindViews() {
        fujinLineList = (ListView)this.findViewById(R.id.list_fujinglushu);
        swipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeRefreshNoItem  = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh_noitem);
        selMudiRel = (RelativeLayout)this.findViewById(R.id.rel_sel_mudi);
    }

    public void DoBack(View view){
        LuShuDaoHangActivity.this.finish();
    }


    private void querySaidun(){
        List<RoadLine> roadLines =  LitePal.where("uid=?",GlobalData.currentUser.getId() + "").find(RoadLine.class);
        refreshList(roadLines);
    }

    private void refreshList(List<RoadLine> roadLines) {
        if (saiDuanAdapter == null){
            saiDuanAdapter = new SaiDuanAdapter(LuShuDaoHangActivity.this);
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

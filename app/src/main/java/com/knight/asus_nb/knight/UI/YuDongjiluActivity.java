package com.knight.asus_nb.knight.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.knight.asus_nb.knight.Adapter.SportAdapter;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.CallBack;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.SportHistory;

import org.litepal.LitePal;

import java.util.List;
 
public class YuDongjiluActivity extends AppCompatActivity {
    //下来刷新控件声明
    private SwipeRefreshLayout swipeRefresh,swipeRefreshNoItem;
    private ListView fujinLineList;
    private SportAdapter sportAdapter;
    private DialogUtil dialogUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yundongjilu);
        bindViews();
        bindLisenters();
        init();
    }
    private void init() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryYuDongJiLu();
        dialogUtil = DialogUtil.getInstance(YuDongjiluActivity.this);
    }

    private void bindLisenters() {
        fujinLineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        fujinLineList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                dialogUtil.showAlertDialog("提示", "确定删除", new CallBack() {
                    @Override
                    public void getResult(int Type) {
                        if (Type == CallBack.SUCCESS){
                            sportAdapter.delete(i);
                        }
                    }
                });
                return true;
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryYuDongJiLu();
            }
        });
        swipeRefreshNoItem.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryYuDongJiLu();
            }
        });
    }

    private void bindViews() {
        fujinLineList = (ListView)this.findViewById(R.id.list_fujinglushu);
        swipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeRefreshNoItem  = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh_noitem);
    }

    public void DoBack(View view){
        YuDongjiluActivity.this.finish();
    }

    //查询赛段前10的
    private void queryYuDongJiLu(){
        List<SportHistory> roadLines =  LitePal.where("uid=?",GlobalData.currentUser.getId() + "").find(SportHistory.class);
        refreshList(roadLines);
    }

    private void refreshList(List<SportHistory> roadLines) {
        if (sportAdapter == null){
            sportAdapter = new SportAdapter(YuDongjiluActivity.this);
            fujinLineList.setAdapter(sportAdapter);
        }
        if (roadLines.size() == 0){
            swipeRefreshNoItem.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshNoItem.setVisibility(View.GONE);
        }
        sportAdapter.setSportHistoryList(roadLines);
        sportAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
        swipeRefreshNoItem.setRefreshing(false);
    }

}
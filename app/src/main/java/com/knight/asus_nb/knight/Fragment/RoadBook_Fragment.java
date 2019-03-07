package com.knight.asus_nb.knight.Fragment;

import com.knight.asus_nb.knight.Adapter.RoadListAdapter;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.UI.AddLuShuJiHeActivity;
import com.knight.asus_nb.knight.UI.FuJinActivity;
import com.knight.asus_nb.knight.UI.RoadLineListDetailActivity;
import com.knight.asus_nb.knight.UI.SaiDuanActivity;
import com.knight.asus_nb.knight.UI.SouSuoActivity;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.Util.GlobalData;
import com.knight.asus_nb.knight.db.RoadLineList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class RoadBook_Fragment extends Fragment implements View.OnTouchListener{
    //下来刷新控件声明
    private SwipeRefreshLayout swipeRefresh,swipeRefreshNoItem;
    //搜索、赛段、附近控件声明
    private RelativeLayout souSuoRel,saiDuanRel,fuJinRel;
    //推荐list控件声明
    private ListView tuiJianList;
    //路书集合数据适配器
    private RoadListAdapter roadListAdapter;
    //推荐、我的控件声明
    private TextView tuijianText,wodeText;
    //菜单控件声明
    private LinearLayout menuLin,noItemLin;
    //对话提示助手声明
    private DialogUtil dialogUtil;
    //添加控件声明
    private ImageView addImg;
    //当前显示模式 0 -推荐 1 -我的
    private int mode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //创建视图并返回
        View view = inflater.inflate(R.layout.roadbook_fragment,null);
        //绑定视图
        bindViews(view);
        //绑定监听器
        bindLisenter();
        //初始化相关数据
        init();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        dialogUtil = DialogUtil.getInstance(getActivity());
        //显示对应模式
        if (mode == 0){
            showTuijian();
        } else {
            showMy();
        }
    }

    private void bindLisenter() {
        souSuoRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SouSuoActivity.class);
                startActivity(intent);
            }
        });
        saiDuanRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null) {
                    dialogUtil.showAlertDialogHint("提示", "用户未登录");
                    return;
                }
                Intent intent = new Intent(getActivity(),SaiDuanActivity.class);
                startActivity(intent);
            }
        });
        fuJinRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FuJinActivity.class);
                startActivity(intent);
            }
        });
        wodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalData.currentUser == null){
                    dialogUtil.showAlertDialogHint("提示","当前用户未登录");
                    return;
                }
                showMy();
            }
        });
        tuijianText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTuijian();
            }
        });
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加路线集合
                Intent intent = new Intent(getActivity(),AddLuShuJiHeActivity.class);
                startActivity(intent);
            }
        });
        tuiJianList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalData.currentShowRoadLineList = roadListAdapter.getItem(i);
                Intent intent = new Intent(getActivity(),RoadLineListDetailActivity.class);
                startActivity(intent);
            }
        });
        tuiJianList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mode == 1){
                    roadListAdapter.delete(i);
                }
                return true;
            }
        });
    }

    /**
     * 显示我的视图
     */
    private void showMy(){
        mode = 1;
        swipeRefresh.setRefreshing(true);
        menuLin.setVisibility(View.GONE);
        addImg.setVisibility(View.VISIBLE);
        tuijianText.setTextColor(Color.parseColor("#212121"));
        wodeText.setTextColor(Color.parseColor("#303F9F"));
        queryMyRood();
    }
    /**
     * 显示推荐视图
     */
    private void showTuijian(){
        mode = 0;
        swipeRefresh.setRefreshing(true);
        menuLin.setVisibility(View.VISIBLE);
        addImg.setVisibility(View.GONE);
        wodeText.setTextColor(Color.parseColor("#212121"));
        tuijianText.setTextColor(Color.parseColor("#303F9F"));
        queryTuiJianRood();
    }
    //查询我的路书
    private void queryMyRood(){
       List<RoadLineList>  roadLineLists =  LitePal.where("uid = ?",GlobalData.currentUser.getId() + "").find(RoadLineList.class);
        refreshList(roadLineLists);
    }
    //刷新集合数据
    private void refreshList(List<RoadLineList> roadLineLists) {
        if (roadListAdapter == null){
            roadListAdapter = new RoadListAdapter(getActivity());
            tuiJianList.setAdapter(roadListAdapter);
        }
        if (roadLineLists.size() == 0){
            swipeRefreshNoItem.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshNoItem.setVisibility(View.GONE);
        }
        roadListAdapter.setRoadLineList(roadLineLists);
        roadListAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
        swipeRefreshNoItem.setRefreshing(false);
    }

    //查询所有前10的路书
    private void queryTuiJianRood(){
        List<RoadLineList>  roadLineLists =  LitePal.limit(10).find(RoadLineList.class);
        refreshList(roadLineLists);
    }

    /**
     * 初始化相关数据
     */
    private void init(){
        //实例化数据适配器
        roadListAdapter = new RoadListAdapter(getActivity());

        //将适配器与视图绑定
        tuiJianList.setAdapter(roadListAdapter);
        //设置刷新条的颜色
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        //设置触发刷新后的监听
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mode == 0)
                queryTuiJianRood();
                else  queryMyRood();
            }
        });
        swipeRefreshNoItem.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mode == 0)
                    queryTuiJianRood();
                else  queryMyRood();
            }
        });
    }
    /**
     * 绑定视图
     */
    private void bindViews(View view) {
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshNoItem  = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_noitem);
        souSuoRel = (RelativeLayout) view.findViewById(R.id.rel_sousuo);
        saiDuanRel  = (RelativeLayout) view.findViewById(R.id.rel_saiduan);
        fuJinRel  = (RelativeLayout) view.findViewById(R.id.rel_fujing);
        tuiJianList  = (ListView) view.findViewById(R.id.list_tuijian);
        tuijianText = (TextView)view.findViewById(R.id.text_tuijian);
        wodeText = (TextView)view.findViewById(R.id.text_wode);
        menuLin = (LinearLayout) view.findViewById(R.id.lin_meanu);
        noItemLin = (LinearLayout)view.findViewById(R.id.lin_noitem);
        addImg = (ImageView)view.findViewById(R.id.img_add);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}

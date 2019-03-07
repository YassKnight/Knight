package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knight.asus_nb.knight.Adapter.SaiDuanAdapter;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.db.RoadLine;

import org.litepal.LitePal;

import java.util.List;

public class SouSuoActivity extends AppCompatActivity {
    //编辑控件声明
    private EditText indexEdt;
    //确定按钮控件声明
    private TextView confirmText;
    //选择起终点搜索控件
    private RelativeLayout selQizhongRel;
    //下来刷新控件声明
    private SwipeRefreshLayout swipeRefresh,swipeRefreshNoItem;
    private ListView fujinLineList;
    private SaiDuanAdapter saiDuanAdapter;
    private DialogUtil dialogUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //关联视图文件
        setContentView(R.layout.activity_sousuo);
        //绑定对应视图
        bindViews();
        //绑定视图监听
        bindListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogUtil = DialogUtil.getInstance(SouSuoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getStringExtra("name") != null){
            dialogUtil = DialogUtil.getInstance(SouSuoActivity.this);
            querySaidun(data.getDoubleExtra("startlat",-1),data.getDoubleExtra("startlon",-1)
            ,data.getDoubleExtra("endlat",-1),data.getDoubleExtra("endlon",-1));
        }

    }

    private void bindListener() {
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = indexEdt.getText().toString();
                if (content != null && content.length() > 0){
                    querySaidun(content);
                }else {
                    Toast.makeText(SouSuoActivity.this,"请输入搜索条件",Toast.LENGTH_SHORT).show();
                }
            }
        });
        selQizhongRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SouSuoActivity.this,SelStartEndActivity.class);

                startActivityForResult(intent,1);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                querySaidun(indexEdt.getText().toString());
            }
        });
        swipeRefreshNoItem.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                querySaidun(indexEdt.getText().toString());
            }
        });
    }

    private void bindViews() {
        indexEdt = (EditText) this.findViewById(R.id.edit_index);
        confirmText = (TextView) this.findViewById(R.id.text_confirm);
        selQizhongRel  = (RelativeLayout) this.findViewById(R.id.rel_selqi);
        fujinLineList = (ListView)this.findViewById(R.id.list_fujinglushu);
        swipeRefreshNoItem = (SwipeRefreshLayout)this.findViewById(R.id.swipe_refresh_noitem);
        swipeRefresh = (SwipeRefreshLayout)this.findViewById(R.id.swipe_refresh);
    }
    //查询数据
    private void querySaidun(String name){
        dialogUtil.showLoadding("查询中");
        //查询名称
        List<RoadLine> roadLines =  LitePal.where("roadLineName like ?" , "%" +name + "%").find(RoadLine.class);
        if (roadLines == null || roadLines.size() == 0){
            try {
                //查询id
               long id = Long.parseLong(name);
                RoadLine roadLine =   LitePal.find(RoadLine.class,id);
                if (roadLine != null)roadLines.add(roadLine);
            }catch (Exception e){
                    //不是id
            }
            //根据创建人查询
            List<RoadLine> roadLine =   LitePal.where("userName = ?" , name ).find(RoadLine.class);
            if (roadLine != null && roadLine.size() > 0) roadLines.addAll(roadLine);
        }
        refreshList(roadLines);
        swipeRefreshNoItem.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.hideLoadding();
                    }
                });
            }
        },1000);
    }
    //查询半径为50米的范围内的数据
    private void querySaidun(double startlat,double startlon,double endlat,double endlon){
        dialogUtil.showLoadding("查询中");
        //查询名称
        List<RoadLine> roadLines =  LitePal.where("startLocLatDouble > ? and startLocLatDouble < ? and startLocLonDouble > ? and" +
                " startLocLonDouble < ? and endLocLatDouble > ? and endLocLatDouble < ? and" +
                " endLocLonDouble > ? and endLocLonDouble < ?" ,
                (startlat - 0.0005) + "" ,(startlat + 0.0005) + "" ,(startlon - 0.0005) + "" ,(startlat + 0.0005) + ""
        ,       (endlat - 0.0005) + "" ,(endlat + 0.0005) + "" ,(endlon - 0.0005) + "" ,(endlon + 0.0005) + "" ).find(RoadLine.class);
        if (roadLines != null){
            refreshList(roadLines);
        }
        swipeRefreshNoItem.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogUtil.hideLoadding();
                    }
                });
            }
        },1000);
    }

    private void refreshList(List<RoadLine> roadLines) {
        if (saiDuanAdapter == null){
            saiDuanAdapter = new SaiDuanAdapter(SouSuoActivity.this);
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

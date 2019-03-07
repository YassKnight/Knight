package com.knight.asus_nb.knight.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.TextureMapView;
import com.knight.asus_nb.knight.Adapter.SaiDuanAdapter;
import com.knight.asus_nb.knight.R;
import com.knight.asus_nb.knight.Util.DialogUtil;
import com.knight.asus_nb.knight.db.RoadLine;

import org.litepal.LitePal;

import java.util.List;

public class SelStartEndActivity extends AppCompatActivity {

    private TextView  endText,startText;
    private RelativeLayout endRel,startRel;
    private String startLoc,endLoc;
    private double startlat,startlon,endlat,endlon;
    private DialogUtil dialogUtil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //关联选择起终点界面
        setContentView(R.layout.activity_selstartend);
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogUtil = DialogUtil.getInstance(SelStartEndActivity.this);
    }

    public void bindViews(){
        endText = (TextView)this.findViewById(R.id.text_end);
        startText = (TextView)this.findViewById(R.id.text_start);
        endRel = (RelativeLayout)this.findViewById(R.id.rel_end);
        startRel = (RelativeLayout)this.findViewById(R.id.rel_start);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data  == null){
            return;
        }
        switch (resultCode){
            case 0:{
                if (data.getStringExtra("loc") == null){
                    return;
                }
                //起点
                startLoc = data.getStringExtra("loc");
                startlat = data.getDoubleExtra("lat",-1);
                startlon = data.getDoubleExtra("lon",-1);
                startRel.setVisibility(View.VISIBLE);
                startText.setText(data.getStringExtra("name"));
            }
            break;
            case 1:{
                if (data.getStringExtra("loc") == null){
                    return;
                }
                //终点
                endLoc = data.getStringExtra("loc");
                endlat = data.getDoubleExtra("lat",-1);
                endlon = data.getDoubleExtra("lon",-1);
                endRel.setVisibility(View.VISIBLE);
                endText.setText(data.getStringExtra("name"));
            }
            break;
        }
    }

    //返回上级界面函数
    public void DoBack(View view){
        SelStartEndActivity.this.finish();
    }
    //选择起点函数
    public void DoSelStart(View view){
        Intent intent = new Intent(SelStartEndActivity.this,SelMapActivity.class);
        intent.putExtra("mode",0);
        startActivityForResult(intent,0);
    }
    //选择终点函数
    public void DoSelEnd(View view){
        Intent intent = new Intent(SelStartEndActivity.this,SelMapActivity.class);
        intent.putExtra("mode",1);
        startActivityForResult(intent,0);
    }
    //删除已选择的起点
    public void DoDeleteStart(View view){
        startText.setText("");
        startRel.setVisibility(View.GONE);
    }
    //删除已选择的终点
    public void DoDeleteEnd(View view){
        endText.setText("");
        endRel.setVisibility(View.GONE);
    }
    //开始搜索函数
    public void DoSouSuo(View view){
        if (startLoc == null || endLoc == null || startLoc.length() == 0 || endLoc.length() == 0){
            dialogUtil.showAlertDialogHint("提示","信息不完整");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("name",startLoc + "," + endLoc);
        intent.putExtra("startlat",startlat);
        intent.putExtra("startlon",startlon);
        intent.putExtra("endlat",endlat);
        intent.putExtra("endlon",endlon);
        setResult(0,intent);
        SelStartEndActivity.this.finish();
    }

}

package com.knight.asus_nb.knight.UI;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.knight.asus_nb.knight.R;

public class WebActivity extends AppCompatActivity {
    private WebView webView;
    private TextView titlText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView)this.findViewById(R.id.webview);
        titlText = (TextView)this.findViewById(R.id.PersonPage_title);
        init();
    }

    private void init() {
        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);//解决图片不显示
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        //自适应屏幕
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        String url = getIntent().getStringExtra("url");
        if (url == null || url.length() == 0){
            url = "https://cs17927240.m.icoc.bz/mlist.jsp";
        }
        webView.loadUrl(url);

        webView.setWebChromeClient(new WebChromeClient() {
            //这里设置获取到的网站title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titlText.setText(title);
            }
        });
    }

    public void DoBack(View view){
        WebActivity.this.finish();
    }
}

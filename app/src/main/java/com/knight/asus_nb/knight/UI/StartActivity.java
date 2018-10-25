package com.knight.asus_nb.knight.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.knight.asus_nb.knight.R;


public class StartActivity extends Activity {
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        new Thread() {
            public void run() {
                try {
                    sleep(2000);
                    intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
}

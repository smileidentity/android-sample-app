package com.demo.smileid.sid_sdk;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SIDJobCompletedActivity extends AppCompatActivity {

    public static final String SUCCESS_MESSAGE = "SIDJobCompletedActivity" +
            ".SUCCESS_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sid_activity_job_completed);
        if (getIntent() != null) {
            String message = getIntent().getStringExtra(SUCCESS_MESSAGE);
            if (!TextUtils.isEmpty(message)) {
                ((TextView) findViewById(R.id.tv_status)).setText(message);
            }
        }
    }

    public void go2Next(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
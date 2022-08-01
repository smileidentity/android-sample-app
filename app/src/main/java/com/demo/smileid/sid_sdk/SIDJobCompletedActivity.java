package com.demo.smileid.sid_sdk;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class SIDJobCompletedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_job_completed);
    }

    public void go2Next(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
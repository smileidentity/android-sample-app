package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SIDJobFailedActivity extends AppCompatActivity {

    public static final String FAILED_MSG = "FAILED_MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_job_failed);

        if (getIntent() != null) {
            String message = getIntent().getStringExtra(FAILED_MSG);

            if ((message != null) && (!message.isEmpty())) {
                ((TextView) findViewById(R.id.tvFailedMsg)).setText(message);
            }
        }
    }

    public void goHome(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_demo);

        ((TextView) findViewById(R.id.tvVersion)).setText(String.format(getString(
            R.string.about_demo_screen_lbl_version_nbr), BuildConfig.VERSION_NAME));
        findViewById(R.id.ivBackArrow).setOnClickListener(v -> super.onBackPressed());
    }
}
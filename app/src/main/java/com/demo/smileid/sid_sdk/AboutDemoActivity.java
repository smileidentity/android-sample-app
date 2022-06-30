package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class AboutDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_about_demo);

        findViewById(R.id.tvContactLink).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://smileidentity.com/contact-us"))));
        findViewById(R.id.tvWebsiteLink).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://smileidentity.com/"))));
        findViewById(R.id.ivBackArrow).setOnClickListener(v -> super.onBackPressed());
    }
}
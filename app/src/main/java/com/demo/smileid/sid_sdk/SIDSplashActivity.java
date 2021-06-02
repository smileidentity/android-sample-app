package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.smileidentity.libsmileid.utils.JsonUtils;
import com.smileidentity.libsmileid.utils.Version;

import java.io.IOException;

public class SIDSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.sid_activity_splash);

        ((TextView) findViewById(R.id.tvVersion)).setText(String.format(getString(
                R.string.lbl_version_number), Version.name(), BuildConfig.VERSION_NAME));

        /*Bundle params = new Bundle();
        params.putString("name", "SPLASHACTIVTY");
        params.putString("class", SIDSplashActivity.class.getName());
        SmileApp.mFirebaseAnalytics.logEvent("TESTING_SCREEN", params);*/

        moveToHomeScreen();
    }

    private void moveToHomeScreen() {
        if (!playServiceAvailable()) {
            Toast.makeText(this, R.string.lbl_play_service_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SIDSplashActivity.this, SIDMainActivity.class));
            }
        };

        new Handler().postDelayed(runnable, 3000);
    }

    private boolean playServiceAvailable() {
        return true;
    }
}
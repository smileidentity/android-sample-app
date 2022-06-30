package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.smileidentity.libsmileid.utils.Version;

public class SIDSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.sid_activity_splash);
    }

    public void moveToHomeScreen(View view) {
        startActivity(new Intent(SIDSplashActivity.this, SIDHomeActivity.class));
    }

    private boolean playServiceAvailable() {
        /*int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SERVICE_MISSING ||
                resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                resultCode == ConnectionResult.SERVICE_DISABLED) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, RC_HANDLE_GMS);
            dlg.show();
        }
        return resultCode == ConnectionResult.SUCCESS;*/
        return true;
    }
}
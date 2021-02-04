package com.demo.smileid.sid_sdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos;

public class BaseSIDActivity extends AppCompatActivity {
    protected boolean mUseMultipleEnroll = false, mUseOffLineAuth = false;
    protected int jobType = -1;
    private static final int PERMISSION_ALL = 1;

    protected String[] PERMISSIONS = {
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    };

    protected void startSelfieCapture(final boolean isEnrollMode, final boolean hasId,
        final boolean use258, final boolean reEnroll, final boolean hasNoIdCard) {

        coreStartSelfieCapture(
            new Intent(this, SIDSelfieActivity.class) {
                {
                    putExtra(SIDStringExtras.EXTRA_ENROLL_MODE, isEnrollMode);
                    putExtra(SIDStringExtras.EXTRA_HAS_ID, hasId);
                    putExtra(SIDStringExtras.EXTRA_USE_258, use258);
                    putExtra(SIDStringExtras.EXTRA_REENROLL, reEnroll);
                    putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, jobType);
                    putExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL, mUseMultipleEnroll);
                    putExtra(SIDStringExtras.EXTRA_HAS_NO_ID_CARD, hasNoIdCard);
                    putExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, mUseOffLineAuth);
                }
            }
        );
    }

    protected void startSelfieCapture(boolean isEnrollMode) {
        startSelfieCapture(isEnrollMode, true);
    }

    protected void startSelfieCapture(boolean isEnrollMode, boolean hasId) {
        startSelfieCapture(isEnrollMode, hasId, false, false, false);
    }

    protected void coreStartSelfieCapture(Intent intent) {
        if (permissionGranted(PERMISSIONS)) {
            SIDGeoInfos.getInstance().init(this);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private boolean permissionGranted(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}

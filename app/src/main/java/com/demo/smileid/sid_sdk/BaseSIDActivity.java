package com.demo.smileid.sid_sdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseSIDActivity extends AppCompatActivity {
    protected boolean mUseMultipleEnroll = false, mUseOffLineAuth = false;
    protected int jobType = -1;
    private static final int PERMISSION_ALL = 1;

    protected String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> permissions = new ArrayList<>(Arrays.asList(PERMISSIONS));

        permissions.add((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ?
            Manifest.permission.READ_PHONE_NUMBERS : Manifest.permission.READ_PHONE_STATE);

        PERMISSIONS = permissions.toArray(new String[] {});
    }

    protected void startSelfieCapture(boolean isEnrollMode, boolean hasId, boolean use258, boolean reenroll, boolean hasNoIdCard) {
        Intent intent = new Intent(this, SIDSelfieActivity.class);
        intent.putExtra(SIDStringExtras.EXTRA_ENROLL_MODE, isEnrollMode);
        intent.putExtra(SIDStringExtras.EXTRA_HAS_ID, hasId);
        intent.putExtra(SIDStringExtras.EXTRA_USE_258, use258);
        intent.putExtra(SIDStringExtras.EXTRA_REENROLL, reenroll);
        intent.putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, jobType);
        intent.putExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL, mUseMultipleEnroll);
        intent.putExtra(SIDStringExtras.EXTRA_HAS_NO_ID_CARD, hasNoIdCard);
        intent.putExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, mUseOffLineAuth);
        coreStartSelfieCapture(intent);
    }

    protected void startSelfieCapture(boolean isEnrollMode) {
        startSelfieCapture(isEnrollMode, true);
    }

    protected void startSelfieCapture(boolean isEnrollMode, boolean hasId) {
        startSelfieCapture(isEnrollMode, hasId, false, false);
    }

    protected void startSelfieCapture(boolean isEnrollMode, boolean hasId, boolean use258, boolean reenroll) {
        startSelfieCapture(isEnrollMode, hasId, false, reenroll, false);
    }

    protected void coreStartSelfieCapture(Intent intent) {
        if (permissionGranted(PERMISSIONS)) {
            SIDGeoInfos.getInstance().init(this);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    protected boolean permissionGranted(String... permissions) {
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
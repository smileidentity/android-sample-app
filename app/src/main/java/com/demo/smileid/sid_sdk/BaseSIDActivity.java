package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos;

public class BaseSIDActivity extends AppCompatActivity {

    public static final String KYC_PRODUCT_TYPE_PARAM = "KYC_PRODUCT_TYPE_PARAM";

    public enum KYC_PRODUCT_TYPE {

        ENROLL_TEST(4), BASIC_KYC(5), ENHANCED_KYC(5), BIOMETRIC_KYC(1),
        DOCUMENT_VERIFICATION(6), SMART_SELFIE_AUTH(2),BVN_CONSENT(-1);

        private int mJobType = -1;

        KYC_PRODUCT_TYPE(int jobType) {
            mJobType = jobType;
        }

        public int getJobType() {
            return mJobType;
        }
    }

    protected KYC_PRODUCT_TYPE mKYCProductType = KYC_PRODUCT_TYPE.ENROLL_TEST;
    private Intent mCurrentIntent = null;
    private static final int PERMISSION_ALL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void startKYCProcess() {
        mCurrentIntent = buildIntent();
        mCurrentIntent.putExtra(KYC_PRODUCT_TYPE_PARAM, mKYCProductType);
        coreStartKYCProcess();
    }

    protected Intent buildIntent() {
        return new Intent(this, SIDSelfieActivity.class);
    }

    protected void coreStartKYCProcess() {
        String[] permissions = getPermissions();
        if (permissionGranted(permissions)) {
            proceedWithJob();
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }
    }

    private void proceedWithJob() {
        SIDGeoInfos.getInstance().init(this);
        startActivity(mCurrentIntent);
        mCurrentIntent = null;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ALL && (grantResults.length > 0) &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            coreStartKYCProcess();
        } else {
            Log.i(this.getClass().getSimpleName(), "Permission not granted");
            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
            mCurrentIntent = null;
        }
    }

    // Get all permissions defined in Android Manifest
    private String[] getPermissions() {
        try {
            return getPackageManager()
                    .getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
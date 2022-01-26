package com.demo.smileid.sid_sdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos;
import com.smileidentity.libsmileid.core.consent.DlgListener;
import com.smileidentity.libsmileid.core.consent.ConsentDialog.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseSIDActivity extends AppCompatActivity implements DlgListener {
    protected boolean mUseMultipleEnroll = false, mUseOffLineAuth = false;
    protected int jobType = -1;
    protected boolean mConsentRequired = false;
    private Intent mCurrentIntent = null;
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
        mCurrentIntent = new Intent(this, SIDSelfieActivity.class);
        mCurrentIntent.putExtra(SIDStringExtras.EXTRA_ENROLL_MODE, isEnrollMode);
        mCurrentIntent.putExtra(SIDStringExtras.EXTRA_HAS_ID, hasId);
        mCurrentIntent.putExtra(SIDStringExtras.EXTRA_USE_258, use258);
        mCurrentIntent.putExtra(SIDStringExtras.EXTRA_REENROLL, reenroll);
        mCurrentIntent.putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, jobType);
        mCurrentIntent.putExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL, mUseMultipleEnroll);
        mCurrentIntent.putExtra(SIDStringExtras.EXTRA_HAS_NO_ID_CARD, hasNoIdCard);
        mCurrentIntent.putExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, mUseOffLineAuth);
        coreStartSelfieCapture();
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

    protected void coreStartSelfieCapture() {
        if (permissionGranted(PERMISSIONS)) {
            if (mConsentRequired) {
                requestUserConsent();
            } else {
                proceedWithJob();
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void proceedWithJob() {
        SIDGeoInfos.getInstance().init(this);
        startActivity(mCurrentIntent);
        mConsentRequired = false;
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

    protected void requestUserConsent() {
        //To be replaced by a partner-set values as returned by the backend
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_purse);

        //Partner's name shouldn't be hardcoded
        try {
            new Builder("USER_TAG", bitmap, "AM Loans Inc.", "www.google.com")
                .setListener(this).build(this).showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decline(String tag) {
        mConsentRequired = false;
        mCurrentIntent = null;
        //A more appropriate message should be provided
        String message = "You need to provide consent in order to proceed";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void consentProvided(String tag) {
        proceedWithJob();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ALL && (grantResults.length > 0) &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            coreStartSelfieCapture();
        } else {
            mConsentRequired = false;
            mCurrentIntent = null;
        }
    }
}
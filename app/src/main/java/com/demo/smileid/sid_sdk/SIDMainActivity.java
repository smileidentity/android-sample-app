package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.demo.smileid.sid_sdk.sidNet.InternetStateBroadCastReceiver;
import com.demo.smileid.sid_sdk.sidNet.SIDNetworkingUtils;
import com.smileidentity.libsmileid.core.RetryOnFailurePolicy;
import com.smileidentity.libsmileid.core.SIDConfig;
import com.smileidentity.libsmileid.core.SIDNetworkRequest;
import com.smileidentity.libsmileid.model.PartnerParams;
import com.smileidentity.libsmileid.model.SIDMetadata;
import com.smileidentity.libsmileid.model.SIDNetData;
import java.util.concurrent.TimeUnit;
import static com.demo.smileid.sid_sdk.SIDStringExtras.EXTRA_TAG_PREFERENCES_AUTH_TAGS;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_JOB_ID;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_USER_ID;

public class SIDMainActivity extends BaseSIDActivity implements
        InternetStateBroadCastReceiver.OnConnectionReceivedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sid_main);
    }

    public void enrollWithIdNo(View view) {
        resetJob();
        startSelfieCapture(true, true, false, false, true);
    }

    public void enrollWithIdCard(View view) {
        resetJob();
        startSelfieCapture(true);
    }

    public void enroll(View view) {
        resetJob();
        startSelfieCapture(true, false);
    }

    public void reEnrollWithId(View view) {
        resetJob();
        startSelfieCapture(true, true);
    }

    public void reEnroll(View view) {
        resetJob();
        startSelfieCapture(true, false);
    }

    public void updateUseImage(View view) {
        resetJob();
        jobType = 8;
        startSelfieCapture(false, false);
    }

    public void multipleEnroll(View view) {
        resetJob();
        mUseMultipleEnroll = true;
        startSelfieCapture(true, false);
    }

    public void validateId(View view) {
        resetJob();
        startActivity(new Intent(this, SIDIDValidationActivity.class));
    }

    public void authenticate(View view) {
        resetJob();

        if (!hasSavedUser()) {
            enrollFirstDialog();
            return;
        }

        startSelfieCapture(false);
    }

    public void authenticateWithSavedData(View view) {
        resetJob();

        if (!hasSavedUser()) {
            enrollFirstDialog();
            return;
        }

        if (hasOfflineTags()) {
            showOfflineAuthDialog();
        } else {
            mUseOffLineAuth = true;
            startSelfieCapture(false);
        }
    }

    private void resetJob() {
        jobType = -1;
        mUseMultipleEnroll = false;
        mUseOffLineAuth = false;
    }

    private void showOfflineAuthDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.msg_use_offline_jobs);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                R.string.lbl_use_offline_jobs,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(SIDMainActivity.this, SIDAuthResultActivity.class);
                        intent.putExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, true);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                R.string.lbl_add_offline_jobs,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mUseOffLineAuth = true;
                        startSelfieCapture(false);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void enrollFirstDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.msg_enrollment_required);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Register (Enroll)",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startSelfieCapture(true, false);
                    }
                });

        builder1.setNegativeButton(
                R.string.btn_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private boolean hasOfflineTags() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String tags = sharedPreferences.getString(EXTRA_TAG_PREFERENCES_AUTH_TAGS, null);

        if (tags != null) {
            return tags.split(",").length > 0;
        }

        return false;
    }

    private boolean hasSavedUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String tags = sharedPreferences.getString(SHARED_PREF_USER_ID, null);
        return !TextUtils.isEmpty(tags);
    }

    private void getLastJobStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(SHARED_PREF_USER_ID, "");
        String jobId = sharedPreferences.getString(SHARED_PREF_JOB_ID, "");
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(jobId)) {
            Toast.makeText(this, "Please run a job first to use job  status", Toast.LENGTH_LONG).show();
            return;
        }
        SIDMetadata metadata = new SIDMetadata();
        PartnerParams params = metadata.getPartnerParams();
        params.setUserId(userId);
        params.setJobId(jobId);

        RetryOnFailurePolicy retryOnFailurePolicy = new RetryOnFailurePolicy();
        retryOnFailurePolicy.setRetryCount(10);
        retryOnFailurePolicy.setRetryTimeout(TimeUnit.SECONDS.toMillis(15));

        SIDNetData data = new SIDNetData(this,SIDNetData.Environment.TEST);
        SIDConfig.Builder builder = new SIDConfig.Builder(this)
                .setRetryOnfailurePolicy(retryOnFailurePolicy)
                .setMode(SIDConfig.Mode.ENROLL)
                .setSmileIdNetData(data)
                .setGeoInformation(null)
                .setSIDMetadata(metadata)
                .setIsJobStatusQuery(true)
                .setJobType(1)
                .useIdCard(false);
        SIDConfig mConfig = builder.build();

        SIDNetworkRequest sidNetworkRequest = new SIDNetworkRequest(this);


        if (SIDNetworkingUtils.haveNetworkConnection(this)) {
            sidNetworkRequest.submit(mConfig);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInternetStateChanged(boolean recovered) {

    }
}
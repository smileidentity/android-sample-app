package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.demo.smileid.sid_sdk.sidNet.InternetStateBroadCastReceiver;
import com.demo.smileid.sid_sdk.sidNet.SIDNetworkingUtils;
/*import com.smileid.smileidui.CaptureType;
import com.smileid.smileidui.SIDCaptureManager;*/
import com.smileidentity.libsmileid.core.RetryOnFailurePolicy;
import com.smileidentity.libsmileid.core.SIDConfig;
import com.smileidentity.libsmileid.core.SIDNetworkRequest;
import com.smileidentity.libsmileid.core.SIDResponse;
import com.smileidentity.libsmileid.exception.SIDException;
import com.smileidentity.libsmileid.model.PartnerParams;
import com.smileidentity.libsmileid.model.SIDMetadata;
import com.smileidentity.libsmileid.model.SIDNetData;

import java.util.concurrent.TimeUnit;

import static com.demo.smileid.sid_sdk.SIDStringExtras.EXTRA_TAG_PREFERENCES_AUTH_TAGS;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_JOB_ID;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_USER_ID;
//import static com.smileid.smileidui.IntentHelper.SMILE_REQUEST_RESULT_TAG;
//import static com.smileid.smileidui.IntentHelper.SMILE_REQUEST_RESULT_TAG;

public class SIDSmileIDActivity extends BaseSIDActivity implements View.OnClickListener,
        SIDNetworkRequest.OnCompleteListener,
        SIDNetworkRequest.OnErrorListener,
        SIDNetworkRequest.OnUpdateListener,
        SIDNetworkRequest.OnEnrolledListener,
        InternetStateBroadCastReceiver.OnConnectionReceivedListener {

    private static final int SMILE_ID_CARD_REQUEST_CODE = 777;
    private static final int SMILE_SELFIE_REQUEST_CODE = 778;

    Button mIDCardEnrollSmileUI;
    Button mSelfieEnrollSmileUI;
    Button mEnrollBtn;
    Button mEnrollBtnNoIDCard;
    Button mAuthBtn;
    Button mEnrollNoId;
    Button mAuthUsingSavedData;
    Button mAuth258Btn;
    Button mReenRoll;
    Button mReenRollNoId;
    Button mEnroll260;
    Button mEnroll257;
    Button mMultipleEnroll;
    Button mUpdateEnrolledImage;
    Button mIDValidation;
    Button mJobStatus;
    SIDNetworkRequest mSINetworkrequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sid_smile_id_layout);
        mEnrollBtn = findViewById(R.id.enroll_btn);
        mEnrollBtnNoIDCard = findViewById(R.id.enroll_no_card_btn);
        mAuthBtn = findViewById(R.id.auth_btn);
        mEnrollNoId = findViewById(R.id.enroll_no_id);
        mAuthUsingSavedData = findViewById(R.id.use_saved_data_btn);
        mAuth258Btn = findViewById(R.id.auth_258_btn);
        mEnroll260 = findViewById(R.id.enroll_260_btn);
        mEnroll257 = findViewById(R.id.enroll_257_btn);
        mReenRoll = findViewById(R.id.reenroll_user);
        mReenRollNoId = findViewById(R.id.reenroll_user_without_id);
        mMultipleEnroll = findViewById(R.id.multiple_enroll);
        mUpdateEnrolledImage = findViewById(R.id.update_enroll_image);
        mIDValidation = findViewById(R.id.iv_validation);
        mJobStatus = findViewById(R.id.job_status);
        mIDCardEnrollSmileUI = findViewById(R.id.enroll_id_card_smile_ui);
        mSelfieEnrollSmileUI = findViewById(R.id.enroll_smile_ui);


        mUpdateEnrolledImage.setOnClickListener(this);
        mEnrollBtn.setOnClickListener(this);
        mEnrollBtnNoIDCard.setOnClickListener(this);
        mAuthBtn.setOnClickListener(this);
        mEnrollNoId.setOnClickListener(this);
        mAuthUsingSavedData.setOnClickListener(this);
        mAuth258Btn.setOnClickListener(this);
        mReenRoll.setOnClickListener(this);
        mReenRollNoId.setOnClickListener(this);
        mEnroll257.setOnClickListener(this);
        mEnroll260.setOnClickListener(this);
        mMultipleEnroll.setOnClickListener(this);
        mIDValidation.setOnClickListener(this);
        mJobStatus.setOnClickListener(this);
        mIDCardEnrollSmileUI.setOnClickListener(this);
        mSelfieEnrollSmileUI.setOnClickListener(this);

        mSINetworkrequest = new SIDNetworkRequest(this);
        mSINetworkrequest.setOnCompleteListener(this);
        mSINetworkrequest.set0nErrorListener(this);
        mSINetworkrequest.setOnUpdateListener(this);
        mSINetworkrequest.setOnEnrolledListener(this);
    }

    @Override
    public void onClick(View v) {
        jobType = -1;
        mUseMultipleEnroll = false;
        mUseOffLineAuth = false;
        switch (v.getId()) {
            case R.id.iv_validation:
                Intent intent = new Intent(this, SIDIDValidationActivity.class);
                startActivity(intent);
                break;
            case R.id.enroll_btn:
                startSelfieCapture(true);
                break;
            case R.id.enroll_no_card_btn:
                startSelfieCapture(true, true, false, false, true);
                break;
            case R.id.reenroll_user:
                startSelfieCapture(true, true, false, true);
                break;
            case R.id.reenroll_user_without_id:
                startSelfieCapture(true, false, false, true);
                break;
            case R.id.auth_btn:
                if (!hasSavedUser()) {
                    enrolFirstDialog();
                    return;
                }
                startSelfieCapture(false);
                break;
            case R.id.enroll_no_id:
                startSelfieCapture(true, false);
                break;
            case R.id.use_saved_data_btn:
                if (!hasSavedUser()) {
                    enrolFirstDialog();
                    return;
                }
                if (hasOfflineTags()) {
                    showOfflineAuthDialog();
                } else {
                    mUseOffLineAuth = true;
                    startSelfieCapture(false);
                }
                break;
            case R.id.auth_258_btn:
                if (!hasSavedUser()) {
                    enrolFirstDialog();
                    return;
                }
                startSelfieCapture(false, false, true, false);
                break;
            case R.id.enroll_257_btn:
                jobType = 257;
                startSelfieCapture(true);
                break;
            case R.id.enroll_260_btn:
                jobType = 260;
                startSelfieCapture(true, false, false, false);
                break;
            case R.id.update_enroll_image:
                jobType = 8;
                startSelfieCapture(false, false, false, false);
                break;
            case R.id.multiple_enroll:
                mUseMultipleEnroll = true;
                startSelfieCapture(true, false);
                break;
            case R.id.job_status:
                getLastJobStatus();
                break;
            /*case R.id.enroll_id_card_smile_ui:
                new SIDCaptureManager.Builder(this, CaptureType.SELFIE_AND_ID_CAPTURE, SMILE_ID_CARD_REQUEST_CODE).build().start();
                break;
            case R.id.enroll_smile_ui:
                new SIDCaptureManager.Builder(this, CaptureType.SELFIE, SMILE_SELFIE_REQUEST_CODE).build().start();
                break;*/
        }


    }

    private void showOfflineAuthDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Would you like to use existing or add new offline jobs?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Use existing jobs",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(SIDSmileIDActivity.this, SIDAuthResultActivity.class);
                        intent.putExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, true);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "Add to existing Jobs",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mUseOffLineAuth = true;
                        startSelfieCapture(false);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void enrolFirstDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("You have to enrol (register) first before you can authenticate");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Register (Enroll)",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startSelfieCapture(true, false);
                    }
                });

        builder1.setNegativeButton(
                "Cancel",
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

        SIDNetData data = new SIDNetData(this, SIDNetData.Environment.PROD);
        SIDConfig.Builder builder = new SIDConfig.Builder(this)
                .setRetryOnfailurePolicy(retryOnFailurePolicy)
                .setMode(SIDConfig.Mode.ENROLL)
                .setSmileIdNetData(data)
                .setGeoInformation(null)
                .setSIDMetadata(metadata)
                .setIsJobStatusQuery(true)
                .setJobType(1);
        SIDConfig mConfig = builder.build();

        SIDNetworkRequest sidNetworkRequest = new SIDNetworkRequest(this);


        if (SIDNetworkingUtils.haveNetworkConnection(this)) {
            sidNetworkRequest.submit(mConfig);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onInternetStateChanged(boolean recovered) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onUpdate(int progress) {
    }

    @Override
    public void onError(SIDException e) {

    }

    @Override
    public void onEnrolled(SIDResponse response) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == SMILE_SELFIE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, SIDEnrollResultActivity.class);
                intent.putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, 4);
                intent.putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, data.getStringExtra(SMILE_REQUEST_RESULT_TAG));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Oops Smile ID UI Selfie did not return a success", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == SMILE_ID_CARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, SIDEnrollResultActivity.class);
                intent.putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, 1);
                intent.putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, data.getStringExtra(SMILE_REQUEST_RESULT_TAG));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Oops Smile ID UI Selfie and ID Card did not return a success", Toast.LENGTH_LONG).show();
            }
        }*/
    }
}

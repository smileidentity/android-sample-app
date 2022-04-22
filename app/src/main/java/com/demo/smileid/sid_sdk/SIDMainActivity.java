package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos;
import com.demo.smileid.sid_sdk.sidNet.InternetStateBroadCastReceiver;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.smileid.smileidui.CaptureType;
import com.smileid.smileidui.SIDCaptureManager;
import com.smileid.smileidui.SIDSelfieCaptureConfig;
import static com.demo.smileid.sid_sdk.SIDStringExtras.EXTRA_TAG_PREFERENCES_AUTH_TAGS;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_USER_ID;
import static com.smileid.smileidui.IntentHelper.SMILE_REQUEST_RESULT_TAG;
import static com.demo.smileid.sid_sdk.DocVerifyOptionDialog.DOC_VER_OPTION;
import static com.demo.smileid.sid_sdk.DocVerifyOptionDialog.DOC_VER_TYPE;
import static com.demo.smileid.sid_sdk.DocVerifyOptionDialog.DOC_VER_TYPE.SELFIE_PLUS_ID_CARD;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class SIDMainActivity extends BaseSIDActivity implements
        InternetStateBroadCastReceiver.OnConnectionReceivedListener {

    private static final int SMILE_ID_CARD_REQUEST_CODE = 777;
    private static final int SMILE_SELFIE_REQUEST_CODE = 778;
    private static final int SMILE_ID_UI_SELFIE_PERMISSION_REQUEST = 779;
    private static final int SMILE_ID_UI_SELFIE_ID_PERMISSION_REQUEST = 780;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sid_activity_main);
    }

    private void resetJob() {
        jobType = -1;
        mUseMultipleEnroll = false;
        mUseOffLineAuth = false;
    }

    public void smileUIIDCardRegister(View view) {
        if (permissionGranted(PERMISSIONS)) {
            /*resetJob();
            mConsentRequired = true;
            requestUserConsent();*/
            SIDCaptureManager.Builder sidCaptureManager = new SIDCaptureManager.Builder(this,
                CaptureType.SELFIE_AND_ID_CAPTURE, SMILE_ID_CARD_REQUEST_CODE);
//
//            SIDSelfieCaptureConfig.Builder config = new SIDSelfieCaptureConfig.Builder();
//            config.setOverlayColor("#FF0000");
//            config.setOverlayAlpha(200);
//            config.setOverlayThickness(10);
//            config.setOverlayDotted(true);
//
//            HashMap<String, Object> styleMap = new HashMap<String, Object>() {
//                {
//                    put("text_size", 24);
//                    put("text_color", Color.RED);
//                    put("font_style", 0);
//                    put("font_family", "Arial");
//                }
//            };
//
//            config.setPromptStyle(new JSONObject(styleMap).toString());
//            config.setReviewPromptStyle(new JSONObject(styleMap).toString());
//            config.setReviewTipStyle(new JSONObject(styleMap).toString());

//            sidCaptureManager.setSidSelfieConfig(config.build());
            sidCaptureManager.build().start();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, SMILE_ID_UI_SELFIE_PERMISSION_REQUEST);
        }
    }

    public void smileUIRegister(View view) {
        if (permissionGranted(PERMISSIONS)) {
            SIDCaptureManager.Builder sidCaptureManager = new SIDCaptureManager.Builder(this,
                CaptureType.SELFIE, SMILE_SELFIE_REQUEST_CODE);

            /*SIDSelfieCaptureConfig.Builder config = new SIDSelfieCaptureConfig.Builder();
            config.setCaptureTitle("CAPTURING...");
            config.setReviewTitle("REVIEW...");
            config.setOverlayColor("#000000");
            config.setOverlayAlpha(50);
            config.setOverlayThickness(10);
            config.setOverlayWidth(260);
            config.setOverlayHeight(350);
            config.setOverlayDotted(true);
            config.setPromptDefault("Here we go...");
            config.setCapturingProgressColor("#0000FF");
            config.setCapturedProgressColor("#00FF00");
            config.setCaptureFullScreen(true);

            HashMap<String, Object> styleMap = new HashMap<String, Object>() {
                {
                    put("text_size", 12);
                    put("text_color", "#0000FF");
                    put("font_style", "bold");
                }
            };

            config.setTitleStyle(new JSONObject(styleMap).toString());

            config.setReviewConfirmButtonStyle(new JSONObject(styleMap).toString());
            config.setReviewConfirmButtonColor("#00FFFF,#FF0000,#788096");

            styleMap = new HashMap<String, Object>() {
                {
                    put("text_size", 12);
                    put("text_color", "#00FF00,#FFFFFF,#0000FF");
                    put("font_style", "normal");
                    put("width", 240);
                    put("height", 48);
                }
            };

            config.setReviewConfirmButtonStyle(new JSONObject(styleMap).toString());

            styleMap = new HashMap<String, Object>() {
                {
                    put("text_size", 18);
                    put("text_color", "#FF0000,#FFFFFF,#0000FF");
                    put("font_style", "normal");
                    put("width", 360);
                    put("height", 120);
                }
            };

            config.setReviewRetakeButtonStyle(new JSONObject(styleMap).toString());
            config.setReviewRetakeButtonColor("#00FFFF,#FF0000,#788096");
            config.setReviewRetakeButton("ANOTHER ONE");

            sidCaptureManager.setSidSelfieConfig(config.build());*/
            sidCaptureManager.build().start();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, SMILE_ID_UI_SELFIE_PERMISSION_REQUEST);
        }
    }

    public void enroll(View view) {
        resetJob();
        jobType = 4;
        startSelfieCapture(true, false);
    }

    public void enrollWithIdNo(View view) {
        resetJob();
        mConsentRequired = true;
        jobType = 1;
        startSelfieCapture(true, true, false, false, true);
    }

    public void enrollWithIdCard(View view) {
        resetJob();
        jobType = 1;
        startSelfieCapture(true);
    }

    public void reEnroll(View view) {
        resetJob();
        jobType = 4;
        startSelfieCapture(true, false, false, true);
    }

    public void reEnrollWithId(View view) {
        resetJob();
        jobType = 1;
        startSelfieCapture(true, true, false, true);
    }

    public void updateUseImage(View view) {
        resetJob();
        jobType = 8;
        startSelfieCapture(false, false, false, false);
    }

    public void multipleEnroll(View view) {
        resetJob();
        mUseMultipleEnroll = true;
        jobType = 4;
        startSelfieCapture(true, false);
    }

    public void validateId(View view) {
        mConsentRequired = true;
        resetJob();
        jobType = 5;
        requestUserConsent();
    }

    @Override
    public void consentProvided(String tag) {
        /*if (jobType == 6) {
            mConsentRequired = false;
            new DocVerifyOptionDialog(this, new DocVerifyOption()).showDialog();
        } else */if (jobType == 5) {
            mConsentRequired = false;
            startActivity(new Intent(this, SIDIDValidationActivity.class));
        } else {
            super.consentProvided(tag);
        }
    }

    private class DocVerifyOption implements DocVerifyOptionDialog.DlgListener {

        @Override
        public void verificationSelected(DOC_VER_TYPE type, DOC_VER_OPTION option) {
            if (type == SELFIE_PLUS_ID_CARD) {
                startSelfieCapture(true);
            } else {
                String tag = getTag();
                ArrayList<String> tags = new ArrayList<>(Arrays.asList(tag));
                SIDGeoInfos.getInstance().init(SIDMainActivity.this);
                Intent mCurrentIntent = new Intent(SIDMainActivity.this, SIDIDCardActivity.class);
                mCurrentIntent.putExtra(SIDStringExtras.EXTRA_REENROLL, false);
                mCurrentIntent.putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, jobType);
                mCurrentIntent.putExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL, mUseMultipleEnroll);
                mCurrentIntent.putExtra(SIDStringExtras.EXTRA_ENROLL_TAG_LIST, tags);
                mCurrentIntent.putExtra(SIDStringExtras.EXTRA_HAS_NO_ID_CARD, false);
                mCurrentIntent.putExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL_ADD_ID_INFO, false);
                mCurrentIntent.putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, tag);
                startActivity(mCurrentIntent);
            }
        }
    }

    public void authenticate(View view) {
        resetJob();
        jobType = 2;

        if (!hasSavedUser()) {
            enrolFirstDialog();
            return;
        }

        startSelfieCapture(false);
    }

    public void authenticateWithSavedData(View view) {
        resetJob();

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
    }

    public void verifyDoc(View view) {
        /*mConsentRequired = true;
        resetJob();
        jobType = 6;
        requestUserConsent();*/

        mConsentRequired = true;
        resetJob();
        jobType = 6;
        startSelfieCapture(true);
    }

    public void getImagePaths(View view) {
        new ImagePathsDialog(this, null).showDialog();
    }

    private void showOfflineAuthDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to use existing or add new offline jobs?");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Use existing jobs",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(SIDMainActivity.this, SIDAuthResultActivity.class);
                        intent.putExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, true);
                        intent.putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, 2);
                        startActivity(intent);
                    }
                });

        builder.setNegativeButton(
                "Add to existing Jobs",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mUseOffLineAuth = true;
                        startSelfieCapture(false);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void enrolFirstDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have to enrol (register) first before you can authenticate");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Register (Enroll)",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startSelfieCapture(true, false);
                    }
                });

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
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

    @Override
    public void onInternetStateChanged(boolean recovered) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SMILE_SELFIE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startActivity(
                        new Intent(this, SIDEnrollResultActivity.class) {
                            {
                                putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, 4);
                                putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, data.getStringExtra(SMILE_REQUEST_RESULT_TAG));
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Oops Smile ID UI Selfie did not return a success", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == SMILE_ID_CARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startActivity(
                        new Intent(this, SIDEnrollResultActivity.class) {
                            {
                                putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, 1);
                                putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, data.getStringExtra(SMILE_REQUEST_RESULT_TAG));
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Oops Smile ID UI Selfie and ID Card did not return a success", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMILE_ID_UI_SELFIE_ID_PERMISSION_REQUEST
                || requestCode == SMILE_ID_UI_SELFIE_PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == SMILE_ID_UI_SELFIE_ID_PERMISSION_REQUEST) {
                        smileUIIDCardRegister(null);
                    } else {
                        smileUIRegister(null);
                    }
                }
            } else {
                Toast.makeText(this, "Oops you did not allow a required permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        AppData.getInstance(this).resetTempConsent();
    }

    private String getTag() {
        return String.format(Misc.USER_TAG, DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}
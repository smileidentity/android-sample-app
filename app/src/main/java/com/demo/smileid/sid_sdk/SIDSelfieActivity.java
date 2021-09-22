package com.demo.smileid.sid_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.smileidentity.libsmileid.core.CameraSourcePreview;
import com.smileidentity.libsmileid.core.SelfieCaptureConfig;
import com.smileidentity.libsmileid.core.SmartSelfieManager;
import com.smileidentity.libsmileid.core.captureCallback.FaceState;
import com.smileidentity.libsmileid.core.captureCallback.OnFaceStateChangeListener;
import com.smileidentity.libsmileid.utils.AppData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.demo.smileid.sid_sdk.SIDStringExtras.EXTRA_TAG_PREFERENCES_AUTH_TAGS;

public class SIDSelfieActivity extends AppCompatActivity implements OnFaceStateChangeListener,
        SmartSelfieManager.OnCompleteListener {

    private ConstraintLayout mClSelfieBtnContainer;
    private TextView mTvPromptReposition;
    private SmartSelfieManager mSmartSelfieManager;

    private boolean mIsEnrollMode, mHasId, mUse258, mHasNoIdCard, mReEnrollUser,
            mMultipleEnroll = false, mUseOffLineAuth = false;

    private int mEnrollType;
    private String mCurrentTag;
    private ArrayList<String> mTagArrayList = new ArrayList<>();
    Map<String, Boolean> selfieTagsSessions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_selfie);

        Intent intent = getIntent();
        mIsEnrollMode = intent.getBooleanExtra(SIDStringExtras.EXTRA_ENROLL_MODE, true);
        mReEnrollUser = intent.getBooleanExtra(SIDStringExtras.EXTRA_REENROLL, false);
        mHasId = intent.getBooleanExtra(SIDStringExtras.EXTRA_HAS_ID, true);
        mHasNoIdCard = intent.getBooleanExtra(SIDStringExtras.EXTRA_HAS_NO_ID_CARD, true);
        mUse258 = intent.getBooleanExtra(SIDStringExtras.EXTRA_USE_258, false);
        mEnrollType = intent.getIntExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, -1);
        mMultipleEnroll = intent.getBooleanExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL, false);
        mUseOffLineAuth = intent.getBooleanExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, false);

        mTvPromptReposition = findViewById(R.id.tvPromptReposition);
        mClSelfieBtnContainer = findViewById(R.id.clSelfieBtnContainer);

        if (mUseOffLineAuth) {
            findViewById(R.id.tvContinueWithId).setVisibility(View.GONE);
            findViewById(R.id.tvSaveAuthLater).setVisibility(View.VISIBLE);
        }

        mSmartSelfieManager = new SmartSelfieManager(getCaptureConfig());
        mSmartSelfieManager.setOnCompleteListener(this);
        mSmartSelfieManager.setOnFaceStateChangeListener(this);
        setBrightnessToMax(this);
        mSmartSelfieManager.captureSelfie(getTag());
        selfieTagsSessions.put(mCurrentTag, false);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.capture).setVisible(true);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!selfieTagsSessions.isEmpty() && selfieTagsSessions.containsKey(mCurrentTag) && selfieTagsSessions.get(mCurrentTag)) {
            mSmartSelfieManager.captureSelfie(getTag());
            selfieTagsSessions.put(mCurrentTag, false);
        } else {
            mTagArrayList.remove(mCurrentTag);
        }
        mSmartSelfieManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartSelfieManager.pause();
    }

    @Override
    public void onFaceStateChange(FaceState faceState) {
        int drawable = 0;
        String text = "";

        switch (faceState) {
            case DO_SMILE:
                if ((mMultipleEnroll || mUseOffLineAuth) && !mClSelfieBtnContainer.isShown()) {
                    findViewById(R.id.tvCapture).setVisibility(View.VISIBLE);
                } else {
                    text = getString(R.string.default_toast_smile);
                    drawable = R.drawable.ic_smile;
                }
                break;

            case CAPTURING:
                text = getString(R.string.lbl_selfie_capturing);
                drawable = R.drawable.ic_capture;
                break;

            case DO_SMILE_MORE:
                text = getString(R.string.default_toast_smile_more);
                drawable = R.drawable.ic_smile;
                break;

            case NO_FACE_FOUND:
                text = getString(R.string.lbl_selfie_reposition);
                drawable = R.drawable.ic_reposition;
                break;

            case DO_MOVE_CLOSER:
                text = getString(R.string.default_toast_text_move_closer);
                drawable = R.drawable.ic_reposition;
                break;
        }

        mTvPromptReposition.setText(text);
        mTvPromptReposition.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }

    private void setBrightnessToMax(Activity activity) {
        // for brightness
        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
        layout.screenBrightness = 1F;
        activity.getWindow().setAttributes(layout);
        // keep our app screen on
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onError(Throwable e) {
        String error = "Could not initialise selfie camera";

        if (e != null && e.getMessage() != null) {
            error = e.getMessage();
        }

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSmartSelfieManager.stop();
    }

    @Override
    public void onComplete(Bitmap fullPreviewFrame) {
        if (fullPreviewFrame != null) {
            //Process returned full preview frame
        }

        if (mIsEnrollMode) {
            if (!mMultipleEnroll)
                startEnrollMode(false);
            else {
                mTagArrayList.add(mCurrentTag);
                selfieTagsSessions.put(mCurrentTag, true);
                showRetakeSelfieButton();
                return;
            }
        } else {
            if (!mUseOffLineAuth) {
                startActivity(
                    new Intent(this, SIDAuthResultActivity.class) {
                        {
                            putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, mEnrollType);
                            putExtra(SIDStringExtras.EXTRA_USE_258, mUse258);
                            putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
                        }
                    }
                );
            } else {
                mTagArrayList.add(mCurrentTag);
                selfieTagsSessions.put(mCurrentTag, true);
                showRetakeSelfieButton();
                return;
            }
        }

        mSmartSelfieManager.stop();
        finish();
    }

    private void showRetakeSelfieButton() {
        findViewById(R.id.cspCamera).setVisibility(View.GONE);
        mClSelfieBtnContainer.setVisibility(View.VISIBLE);
        mTvPromptReposition.setVisibility(View.GONE);
        findViewById(R.id.tvCapture).setVisibility(View.GONE);
    }

    private void startEnrollMode(final boolean continueWithId) {
        Class clazz = (mHasId && !mHasNoIdCard) ? SIDIDCardActivity.class :
                SIDEnrollResultActivity.class;

        AppData.getInstance(this).saveConsentData(mCurrentTag);

        startActivity(
            new Intent(this, clazz) {
                {
                    putExtra(SIDStringExtras.EXTRA_REENROLL, mReEnrollUser);
                    putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, mEnrollType);
                    putExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL, mMultipleEnroll);
                    putExtra(SIDStringExtras.EXTRA_ENROLL_TAG_LIST, mTagArrayList);
                    putExtra(SIDStringExtras.EXTRA_HAS_NO_ID_CARD, mHasNoIdCard);
                    putExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL_ADD_ID_INFO, continueWithId);
                    putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
                }
            }
        );
    }

    private void startAuthMode() {
        startActivity(
            new Intent(this, SIDAuthResultActivity.class) {
                {
                    putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, mEnrollType);
                    putExtra(SIDStringExtras.EXTRA_USE_258, mUse258);
                    putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
                    putExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, mUseOffLineAuth);
                }
            }
        );
    }

    private void saveAuthTagsForLater() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String tagsString = sharedPreferences.getString(EXTRA_TAG_PREFERENCES_AUTH_TAGS, null);

        if (mUseOffLineAuth) {
            StringBuilder sb = new StringBuilder();

            if (tagsString != null) {
                sb = new StringBuilder(tagsString);
            }

            for (int i = 0; i < mTagArrayList.size(); i++) {
                sb.append(mTagArrayList.get(i)).append(",");
            }

            sharedPreferences.edit().putString(EXTRA_TAG_PREFERENCES_AUTH_TAGS, sb.toString()).apply();
        }
    }

    private SelfieCaptureConfig getCaptureConfig() {
        return new SelfieCaptureConfig.Builder(this)
                .setCameraType(/*mMultipleEnroll ? SelfieCaptureConfig.BACK_CAMERA : */SelfieCaptureConfig.FRONT_CAMERA)
                .setPreview((CameraSourcePreview) findViewById(R.id.cspCamera))
                .setManualSelfieCapture((mMultipleEnroll || mUseOffLineAuth))
                .setFlashScreenOnShutter(!mMultipleEnroll && !mUseOffLineAuth)
                .build();
    }

    public void continueProcess(View view) {
        if (mUseOffLineAuth) {
            saveAuthTagsForLater();
            startAuthMode();
        } else {
            startEnrollMode(false);
        }

        finish();
    }

    public void continueSaveWithId(View view) {
        startEnrollMode(true);
        finish();
    }

    public void takeAnotherSelfie(View view) {
        findViewById(R.id.cspCamera).setVisibility(View.VISIBLE);
        mTvPromptReposition.setVisibility(View.VISIBLE);
        mClSelfieBtnContainer.setVisibility(View.GONE);

        if (mMultipleEnroll) {
            mSmartSelfieManager.captureSelfie(getTag());
            selfieTagsSessions.put(mCurrentTag, false);
        }

        mSmartSelfieManager.resume();
    }

    public void captureSelfie(View view) {
        mSmartSelfieManager.takePicture();
    }

    public void saveAndAuthLater(View view) {
        saveAuthTagsForLater();
        finish();
    }

    private String getTag() {
        return mCurrentTag = String.format(Misc.USER_TAG, DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}
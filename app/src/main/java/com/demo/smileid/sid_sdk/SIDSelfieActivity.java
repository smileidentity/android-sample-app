package com.demo.smileid.sid_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.smileidentity.libsmileid.core.CameraSourcePreview;
import com.smileidentity.libsmileid.core.SelfieCaptureConfig;
import com.smileidentity.libsmileid.core.SmartSelfieManager;
import com.smileidentity.libsmileid.core.captureCallback.FaceState;
import com.smileidentity.libsmileid.core.captureCallback.OnFaceStateChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import static com.demo.smileid.sid_sdk.SIDStringExtras.EXTRA_TAG_PREFERENCES_AUTH_TAGS;

public class SIDSelfieActivity extends AppCompatActivity implements OnFaceStateChangeListener,
        SmartSelfieManager.OnCompleteListener {

    private CameraSourcePreview mPreview;
    private TextView mPromptTv;
    private SmartSelfieManager smartSelfieManager;
    
    private boolean mIsEnrollMode, mHasId, mUse258, mHasNoIdCard, mReEnrollUser,
        mMultipleEnroll = false, mUseOffLineAuth = false;
    
    private int mEnrollType;
    private View mMultiEnrollContainer, mCapturePictureBtn;
    private ArrayList<String> mTagArrayList = new ArrayList<>();
    private String mCurrentTag;

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

        mPreview = findViewById(R.id.cspCamera);
        mPromptTv = findViewById(R.id.tvPromptReposition);
        mMultiEnrollContainer = findViewById(R.id.llSelfieBtnContainer);
        mCapturePictureBtn = findViewById(R.id.tvCapture);

        if (mUseOffLineAuth) {
            findViewById(R.id.tvContinueWithId).setVisibility(View.GONE);
            findViewById(R.id.tvSaveAuthLater).setVisibility(View.VISIBLE);
        }

        smartSelfieManager = new SmartSelfieManager(getCaptureConfig());
        smartSelfieManager.setOnCompleteListener(this);
        smartSelfieManager.setOnFaceStateChangeListener(this);
        setBrightnessToMax(this);
        smartSelfieManager.captureSelfie(getTag());
    }

    public void saveAndAuthLater(View view) {
        saveAuthTagsForLater();
        finish();
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
        mMultiEnrollContainer.setVisibility(View.GONE);
        smartSelfieManager.captureSelfie(getTag());
        smartSelfieManager.resume();
    }

    public void captureSelfie(View view) {
        smartSelfieManager.takePicture();
    }

    @Override
    protected void onResume() {
        super.onResume();
        smartSelfieManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        smartSelfieManager.pause();
    }

    @Override
    public void onFaceStateChange(FaceState faceState) {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_reposition);
        String text = mPromptTv.getText().toString();

        switch (faceState) {
            case DO_SMILE:
                if ((mMultipleEnroll || mUseOffLineAuth) && !mMultiEnrollContainer.isShown()) {
                    mCapturePictureBtn.setVisibility(View.VISIBLE);
                } else {
                    text = getString(R.string.default_toast_smile);
                    drawable = getResources().getDrawable(R.drawable.ic_smile);
                }
                break;

            case CAPTURING:
                text = "";
                drawable = getResources().getDrawable(R.drawable.ic_smile);
                break;

            case DO_SMILE_MORE:
                text = getString(R.string.default_toast_smile_more);
                drawable = getResources().getDrawable(R.drawable.ic_smile);
                break;

            case NO_FACE_FOUND:
                text = getString(R.string.lbl_selfie_reposition);
                drawable = getResources().getDrawable(R.drawable.ic_reposition);
                break;

            case DO_MOVE_CLOSER:
                text = getString(R.string.default_toast_text_move_closer);
                drawable = getResources().getDrawable(R.drawable.ic_reposition);
                break;
        }

        mPromptTv.setText(text);
        mPromptTv.setCompoundDrawables(drawable, null, null, null);
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
        smartSelfieManager.stop();
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
                showRetakeSelfieButton();
                return;
            }
        }

        smartSelfieManager.stop();
        finish();
    }

    private void showRetakeSelfieButton() {
        mMultiEnrollContainer.setVisibility(View.VISIBLE);
        mCapturePictureBtn.setVisibility(View.GONE);
    }

    private void startEnrollMode(final boolean continueWithId) {
        Class clazz = (mHasId && !mHasNoIdCard) ? SIDIDCardActivity.class :
            SIDEnrollResultActivity.class;

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

            for (String tag : mTagArrayList) {
                sb.append(tag).append(",");
            }

            sharedPreferences.edit().putString(EXTRA_TAG_PREFERENCES_AUTH_TAGS, sb.toString()).apply();
        }
    }

    private SelfieCaptureConfig getCaptureConfig() {
        return new SelfieCaptureConfig.Builder(this)
            .setCameraType(/*mMultipleEnroll ? SelfieCaptureConfig.BACK_CAMERA :*/ SelfieCaptureConfig.FRONT_CAMERA)
                .setPreview(mPreview)
                    .setManualSelfieCapture((mMultipleEnroll || mUseOffLineAuth))
                        .setFlashScreenOnShutter(!mMultipleEnroll && !mUseOffLineAuth)
                            .build();
    }

    private String getTag() {
        return mCurrentTag = String.format(Misc.USER_TAG, DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}
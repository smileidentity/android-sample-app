package com.demo.smileid.sid_sdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.demo.smileid.sid_sdk.DocVOptionDialog.DOC_VER_OPTION;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.smileidentity.libsmileid.core.CameraSourcePreview;
import com.smileidentity.libsmileid.core.SelfieCaptureConfig;
import com.smileidentity.libsmileid.core.SmartSelfieManager;
import com.smileidentity.libsmileid.core.captureCallback.FaceState;
import com.smileidentity.libsmileid.core.captureCallback.OnFaceStateChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE;

public class SIDSelfieActivity extends AppCompatActivity implements OnFaceStateChangeListener,
        SmartSelfieManager.OnCompleteListener {

    public final static String DOC_V_PARAM = "DOC_V_PARAM";
    public final static String DOC_V_CAPTURE_TYPE = "DOC_V_CAPTURE_TYPE";
    public final static String DOC_V_USER_SELFIE_OPTION = "DOC_V_USER_SELFIE_OPTION";
    private KYC_PRODUCT_TYPE mKYCProductType = KYC_PRODUCT_TYPE.BASIC_KYC;

    private SmartSelfieManager mSmartSelfieManager;
    private CameraSourcePreview mCameraSourcePreview;
    private TextView mTvPrompt;

    private boolean mEnrolledUser = false;
    private boolean mIsAgentMode = false;
    public static boolean mShowTip = true;
    private String mCurrentTag;
    private ArrayList<String> mTagArrayList = new ArrayList<>();
    private Map<String, Boolean> selfieTagsSessions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_selfie);

        initVars();
        initViews();

        selfieTagsSessions.put(mCurrentTag, false);
    }

    private void initVars() {
        mKYCProductType = (KYC_PRODUCT_TYPE) getIntent().getSerializableExtra(BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM);
    }

    private void initViews() {
        mTvPrompt = findViewById(R.id.tvPrompt);
        mCameraSourcePreview = findViewById(R.id.cspCamera);

//        positionPrompt();
        setToggle();
        setToolTip();
        initSmartSelfieCamera(false);
        positionPrompt();

        new Handler().postDelayed(() -> {
            mShowTip = false;
            setToolTip();
        }, 2000);
    }

    private void positionPrompt() {
        View vSeparator = findViewById(com.smileid.smileidui.R.id.vSeparator);
        TextView tvTips = findViewById(com.smileid.smileidui.R.id.tvTips);
        mCameraSourcePreview.getOverlayPosition();

        Log.d("SELFIE_SIZES", mCameraSourcePreview.getOverlayTop() + " : " +
            mCameraSourcePreview.getOverlayThickness() + " : " + mCameraSourcePreview.getOverlayHeight());

        /*ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.clParent);
        cl.layout(0, 0, cl.getWidth(), cl.getHeight());

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(cl);
        constraintSet.connect(mCameraSourcePreview.getId(), ConstraintSet.LEFT, cl.getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(mCameraSourcePreview.getId(), ConstraintSet.RIGHT, cl.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(mCameraSourcePreview.getId(), ConstraintSet.TOP, cl.getId(), ConstraintSet.TOP, 0);
        constraintSet.applyTo(cl);

        float margin = mCameraSourcePreview.getOverlayPosition();
        margin += 50; //Prompt view's top margin

        constraintSet = new ConstraintSet();
        constraintSet.clone(cl);
        constraintSet.connect(mTvPrompt.getId(), ConstraintSet.LEFT, cl.getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(mTvPrompt.getId(), ConstraintSet.RIGHT, cl.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(mTvPrompt.getId(), ConstraintSet.TOP, cl.getId(), ConstraintSet.TOP, (int) margin);
        constraintSet.applyTo(cl);
        mTvPrompt.setGravity(Gravity.CENTER);

        constraintSet = new ConstraintSet();
        constraintSet.clone(cl);
        constraintSet.constrainPercentWidth(vSeparator.getId(), 0.8f);
        constraintSet.connect(vSeparator.getId(), ConstraintSet.LEFT, cl.getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(vSeparator.getId(), ConstraintSet.RIGHT, cl.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(vSeparator.getId(), ConstraintSet.TOP, mTvPrompt.getId(), ConstraintSet.BOTTOM, 20);
        constraintSet.applyTo(cl);

        constraintSet = new ConstraintSet();
        constraintSet.clone(cl);
        constraintSet.connect(tvTips.getId(), ConstraintSet.LEFT, cl.getId(), ConstraintSet.LEFT, 16);
        constraintSet.connect(tvTips.getId(), ConstraintSet.RIGHT, vSeparator.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(tvTips.getId(), ConstraintSet.TOP, vSeparator.getId(), ConstraintSet.BOTTOM, 14);
        constraintSet.applyTo(cl);*/
    }

    private void setToggle() {
        final CardView cvAgentMode = findViewById(R.id.cvAgentMode);
        final TextView tvAgentMode = findViewById(R.id.tvAgentMode);

        ((Switch) findViewById(R.id.sAgentMode)).setOnCheckedChangeListener(
            (compoundButton, state) -> {
                cvAgentMode.setSelected(state);
                tvAgentMode.setSelected(state);
                tvAgentMode.setText(state ? "Disable Agent Mode" : "Enable Agent Mode");
                mShowTip = false;
                setToolTip();
                toggleLoading(true);
                switchAgentMode(state);
            }
        );
    }

    private void setToolTip() {
        findViewById(R.id.ivTriangle).setVisibility(mShowTip ? View.VISIBLE : View.GONE);
        findViewById(R.id.cvTooltip).setVisibility(mShowTip ? View.VISIBLE : View.GONE);
    }

    private void toggleLoading(boolean visible) {
        findViewById(R.id.clLoading).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void initSmartSelfieCamera(boolean resumeCapture) {
        mSmartSelfieManager = new SmartSelfieManager(getCaptureConfig());
        mSmartSelfieManager.setOnCompleteListener(this);
        mSmartSelfieManager.setOnFaceStateChangeListener(this);
        setBrightnessToMax(this);
        mSmartSelfieManager.captureSelfie(getTag());

        if (resumeCapture) {
            mSmartSelfieManager.resume();
        }
    }

    private void switchAgentMode(boolean isAgentMode) {
        mIsAgentMode = isAgentMode;
        mSmartSelfieManager.pause();
        mSmartSelfieManager.stop();

        new Handler().postDelayed(() -> {
            toggleLoading(false);
            initSmartSelfieCamera(true);
        }, 500);
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
    protected void onStop() {
        super.onStop();
        mSmartSelfieManager.stop();
    }

    public void backPressed(View view) {
        super.onBackPressed();
    }

    @Override
    public void onFaceStateChange(FaceState faceState) {
        String text = "";

        switch (faceState) {
            case DO_SMILE:
            case DO_SMILE_MORE:
                text = getString(R.string.default_toast_smile);
                break;

            case CAPTURING:
                text = getString(R.string.lbl_selfie_capturing);
                break;

            case NO_FACE_FOUND:
                text = getString(R.string.lbl_selfie_reposition);
                break;

            case DO_MOVE_CLOSER:
                text = getString(R.string.default_toast_text_move_closer);
                break;

            case FACE_TOO_CLOSE:
                text = getString(R.string.default_toast_text_face_too_close);
                break;

            case BLURRY:
                text = getString(R.string.default_toast_text_blurry);
                break;

            case TOO_DARK:
                text = getString(R.string.default_toast_text_too_dark);
                break;

            case IDLE:
                text = getString(R.string.default_toast_text_idle);
                break;

            case COMPATIBILITY_MODE:
                text = getString(R.string.default_toast_text_compatibility_mode);
                break;
        }

        mTvPrompt.setText(text);
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
    public void onComplete(Bitmap fullPreviewFrame) {
        if (fullPreviewFrame != null) {
            //Process returned full preview frame
        }

        Class clazz = null;

        if (mKYCProductType == KYC_PRODUCT_TYPE.BASIC_KYC) {
            clazz = SIDJobResultActivity.class;
        } else if (mKYCProductType == KYC_PRODUCT_TYPE.ENHANCED_KYC) {
            clazz = SIDIDInfoActivity.class;
        } else if (mKYCProductType == KYC_PRODUCT_TYPE.BIOMETRIC_KYC) {
            clazz = SIDIDCardActivity.class;
        } else if (mKYCProductType == KYC_PRODUCT_TYPE.DOCUMENT_VERIFICATION) {
            Map<String, String> docVOptions = (Map<String, String>) getIntent().getSerializableExtra(
                DOC_V_PARAM);

            if (docVOptions.containsKey(DOC_V_USER_SELFIE_OPTION)) {
                mEnrolledUser = docVOptions.get(DOC_V_USER_SELFIE_OPTION).equalsIgnoreCase(
                    DOC_VER_OPTION.ENROLLED_USER.toString());
            }

            clazz = SIDIDCardActivity.class;
        } else if (mKYCProductType == KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH) {
            clazz = SIDJobResultActivity.class;
        }

        startActivity(
            new Intent(this, clazz) {
                {
                    putExtra(BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM, mKYCProductType);
                    putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
                    putExtra(SIDJobResultActivity.USER_SELFIE_PARAM, mEnrolledUser);
                }
            }
        );

        mSmartSelfieManager.stop();
        finish();
    }

    private SelfieCaptureConfig getCaptureConfig() {
        return new SelfieCaptureConfig.Builder(this)
            .setCameraType(!mIsAgentMode ? SelfieCaptureConfig.FRONT_CAMERA : SelfieCaptureConfig.BACK_CAMERA)
                .setPreview(mCameraSourcePreview)
                    .setManualSelfieCapture(false)
                        .setFlashScreenOnShutter(true)
                            .build();
    }

    private String getTag() {
        return mCurrentTag = String.format(Misc.USER_TAG, DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}
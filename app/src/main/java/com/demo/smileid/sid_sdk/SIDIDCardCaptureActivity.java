package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.smileid.sid_sdk.animation.AnimationUtil;
import com.smileidentity.libsmileid.core.CaptureIDCard;

public class SIDIDCardCaptureActivity extends AppCompatActivity implements CaptureIDCard.IDCaptureCompleteListener,
        View.OnClickListener {

    private TextView mLabelViewNo;
    private TextView mLabelViewYes;
    private TextView mCaptureInfoTv;
    private LinearLayout mViewPreviewIDCard;
    private FrameLayout mCameraFL;
    private SurfaceView mCameraPreview;
    private ImageView mImageViewIDCard;
    private CaptureIDCard mCaptureIDCard;
    private Button mBackBtn;
    private boolean reenrollUser;
    private int enrollType;
    private View mReadPromptTv;
    private boolean doOnce;
    private String mCurrentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.sid_activity_idcard_capture);
        mLabelViewNo = findViewById(R.id.viewNo);
        mLabelViewYes = findViewById(R.id.viewYes);
        mReadPromptTv = findViewById(R.id.read_prompt_tv);
        mCaptureInfoTv = findViewById(R.id.capture_info_tv);
        mViewPreviewIDCard = findViewById(R.id.idCardPreview);
        mCameraFL = findViewById(R.id.cameraPreview);
        mCameraPreview = findViewById(R.id.surfaceView);
        mImageViewIDCard = findViewById(R.id.imageView_idcard);
        mBackBtn = findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
        mLabelViewNo.setOnClickListener(this);
        mLabelViewYes.setOnClickListener(this);
        reenrollUser = getIntent().getBooleanExtra(SIDStringExtras.EXTRA_REENROLL, false);
        enrollType = getIntent().getIntExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, -1);
        mCurrentTag = getIntent().getStringExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);

        mCaptureIDCard = new CaptureIDCard(this, mCurrentTag, mCameraPreview);
        mCaptureIDCard.setOnCompleteListener(this);
        mCaptureIDCard.extractProminentFaceFromID(true);

        LinearLayout.LayoutParams layoutParamsDrawing = new LinearLayout.
                LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        addContentView(mCaptureIDCard.getDrawingView(), layoutParamsDrawing);
    }

    private void initCardAnimation() {
        AnimationUtil.loadAnimation(mCaptureInfoTv, R.anim.pulse_animation, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCaptureInfoTv.setText(R.string.lbl_capture_card_tap_screen_to_capture);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onComplete(Bitmap idCardBitmap, boolean faceFound) {
        if (idCardBitmap != null) {
            mViewPreviewIDCard.setVisibility(View.VISIBLE); // Show preview IDCard view
            mCameraFL.setVisibility(View.GONE);
            mImageViewIDCard.setImageBitmap(idCardBitmap);
            mReadPromptTv.setVisibility(View.VISIBLE);
        }
        if (!doOnce) {
            if (!faceFound) {
                mCaptureInfoTv.setText("No face found on id card! Please Try again");
                initCardAnimation();
                retakePicture();
            }
            doOnce = true;
        }
    }

    @Override
    public void onError(Throwable e) {
        String error = "Could not initialise ID capture camera";
        if (e != null && e.getMessage() != null) {
            error = e.getMessage();
        }
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    @Override
    protected void onStop() {
        mCaptureIDCard.onStop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureIDCard.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCardAnimation();
        mCaptureIDCard.resume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewNo:
                retakePicture();
                break;

            case R.id.viewYes:
                Intent intent = new Intent(this, SIDEnrollResultActivity.class);
                intent.putExtra(SIDStringExtras.EXTRA_HAS_ID, true);
                intent.putExtra(SIDStringExtras.EXTRA_REENROLL, reenrollUser);
                intent.putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, enrollType);
                intent.putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
                startActivity(intent);
                finish();
                break;

            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private void retakePicture() {
        mCameraFL.setVisibility(View.VISIBLE); // Show camera preview
        mViewPreviewIDCard.setVisibility(View.GONE); // hide preview IDCard view
        mReadPromptTv.setVisibility(View.GONE);
    }

    private void setFullScreen() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.smileidentity.libsmileid.core.SmartCardView;
import com.smileidentity.libsmileid.core.captureCallback.IDCardState;

public class SIDIDCardActivity extends AppCompatActivity implements SmartCardView.SmartCardViewCallBack {

    private SmartCardView mSmartCardView;
    private String mCurrentTag;
    private int mEnrollType;
    private boolean mReEnrollUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_id_card);
        mSmartCardView = findViewById(R.id.id_capture);
        mSmartCardView.setListener(this);
        mReEnrollUser = getIntent().getBooleanExtra(SIDStringExtras.EXTRA_REENROLL, false);
        mEnrollType = getIntent().getIntExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, -1);
        mCurrentTag = getIntent().getStringExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSmartCardView.startCapture(mCurrentTag);
    }

    private void setFullScreen() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onSmartCardViewError(Exception error) {
        String errorMessage = "Could not initialise ID capture camera";
        if (error != null && error.getMessage() != null) {
            errorMessage = error.getMessage();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        error.printStackTrace();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartCardView.pauseCapture();
    }

    @Override
    public void onSmartCardViewComplete(Bitmap idCardBitmap, boolean faceFound) {
        Intent intent = new Intent(this, SIDEnrollResultActivity.class);
        intent.putExtra(SIDStringExtras.EXTRA_HAS_ID, true);
        intent.putExtra(SIDStringExtras.EXTRA_REENROLL, mReEnrollUser);
        intent.putExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, mEnrollType);
        intent.putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSmartCardViewClosed() {
        onBackPressed();
    }

    @Override
    public void onIDCardStateChange(IDCardState idCardState) {

    }
}

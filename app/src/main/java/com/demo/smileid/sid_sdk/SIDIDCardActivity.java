package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.smileidentity.libsmileid.core.SmartCardView;
import com.smileidentity.libsmileid.core.captureCallback.IDCardState;
import com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE;

public class SIDIDCardActivity extends AppCompatActivity implements SmartCardView.SmartCardViewCallBack,
    ActionDialog.DlgListener {

    private SmartCardView mSmartCardView;
    private String mCurrentTag;
    private boolean mShowingDlg = false;
    private KYC_PRODUCT_TYPE mKYCProductType = KYC_PRODUCT_TYPE.BASIC_KYC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_id_card);

        mSmartCardView = findViewById(R.id.id_capture);
        mSmartCardView.setListener(this);
        mKYCProductType = (BaseSIDActivity.KYC_PRODUCT_TYPE) getIntent().getSerializableExtra(BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM);
        mCurrentTag = getIntent().getStringExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mShowingDlg) return;

        try {
            mSmartCardView.startCapture(mCurrentTag);
        } catch (Exception e) {

        }
    }

    private void setFullScreen() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        if (mShowingDlg) return;
        mSmartCardView.pauseCapture();
    }

    @Override
    public void onSmartCardViewFrontComplete(Bitmap idCardBitmap, boolean faceFound) {
        new ActionDialog(this, this).showDialog();
        mShowingDlg = true;
        return;
    }

    @Override
    public void onSmartCardViewBackComplete(Bitmap idCardBitmap) {
        skip();
    }

    @Override
    public void onSmartCardViewClosed() {
        onBackPressed();
    }

    @Override
    public void onIDCardStateChange(IDCardState idCardState) {

    }

    @Override
    public void skip() {
        mShowingDlg = false;
        proceedWithResult();
    }

    private void proceedWithResult() {
        Intent intent = new Intent(this, SIDJobResultActivity.class);
        intent.putExtra(BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM, mKYCProductType);
        intent.putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
        startActivity(intent);
        finish();
    }

    @Override
    public void capture() {
        mShowingDlg = true;
        mSmartCardView.setIDBackCapture();
        onResume();
    }
}
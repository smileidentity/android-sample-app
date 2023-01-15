package com.demo.smileid.sid_sdk;

import static com.smileid.smileidui.IntentHelper.ID_CARD_CAPTURE_ERROR;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.smileidentity.libsmileid.core.SmartCardView;
import com.smileidentity.libsmileid.core.captureCallback.IDCardState;
import com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE;
import com.smileidentity.libsmileid.exception.SIDException;

import java.util.Calendar;

public class SIDIDCardActivity extends AppCompatActivity implements SmartCardView.SmartCardViewCallBack,
    ActionDialog.DlgListener {

    private SmartCardView mSmartCardView;
    private String mCurrentTag;
    private boolean mShowingDlg = false;
    private Bundle mParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_id_card);

        mSmartCardView = findViewById(R.id.id_capture);
        mSmartCardView.setListener(this);

        mParams = getIntent().getExtras();
        mCurrentTag = mParams.getString(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);
        mCurrentTag = ((mCurrentTag == null) || (mCurrentTag.isEmpty())) ? getTag() : mCurrentTag;
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
        String heading = getString(R.string.id_card_back_dlg_txt);
        String message = getString(R.string.id_card_back_dlg_txt_2);
        String proceed = getString(R.string.id_card_back_dlg_proceed_btn);
        String skip = getString(R.string.id_card_back_dlg_skip_btn);

        new ActionDialog.Builder(this).setDlgListener(this).setHeadingTxt(heading)
            .setContentTxt(message).setPositiveBtnTxt(proceed).setNegativeBtnTxt(skip).build()
                .showDialog();

        mShowingDlg = true;
        return;
    }

    @Override
    public void onSmartCardViewBackComplete(Bitmap idCardBitmap) {
        cancel();
    }

    @Override
    public void onSmartCardViewClosed() {
        onBackPressed();
    }

    @Override
    public void onIDCardStateChange(IDCardState idCardState) {

    }

    @Override
    public void cancel() {
        mShowingDlg = false;
        proceedWithResult();
    }

    private void proceedWithResult() {
        if ((mParams != null) && (!mParams.containsKey(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO))) {
            mParams.putString(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
        }

        startActivity(
            new Intent(this, SIDSelfieActivity.class) {
                {
                    putExtras(mParams);
                }
            }
        );

        finish();
    }

    @Override
    public void proceed() {
        mShowingDlg = true;
        mSmartCardView.setIDBackCapture();
        try {
            mSmartCardView.startCapture(mCurrentTag);
        } catch (SIDException e) {
            e.printStackTrace();
        }
    }

    private String getTag() {
        return mCurrentTag = String.format(
            Misc.USER_TAG, DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}
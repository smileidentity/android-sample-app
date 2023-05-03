package com.demo.smileid.sid_sdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.demo.smileid.sid_sdk.DocVOptionDialog.DOC_VER_TYPE;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.smileid.smileidui.CaptureType;
import com.smileid.smileidui.IntentHelper;
import com.smileid.smileidui.SIDCaptureManager;
import com.smileidentity.libsmileid.core.consent.ConsentActivity;
import com.smileidentity.libsmileid.core.consent.util.SIDConsentConfig;

import java.util.Calendar;
import java.util.HashMap;

public class GetStartedActivity extends BaseSIDActivity {

    private static final int SMILE_UI_REQUEST_CODE = 778;
    private static final int USER_CONSENT_REQUEST_CODE = 123;
    public static final String REQUIRE_CONSENT = "REQUIRE_CONSENT";
    private Bundle mParams = null;
    private SIDConsentConfig sidConsentConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        findViewById(R.id.ivBackArrow).setOnClickListener(v -> super.onBackPressed());
        mParams = getIntent().getExtras();
        if (mParams != null && mParams.containsKey(KYC_PRODUCT_TYPE_PARAM)) {
            mKYCProductType =
                    (KYC_PRODUCT_TYPE) mParams.get(KYC_PRODUCT_TYPE_PARAM);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sidConsentConfig = new SIDConsentConfig(this,
                USER_CONSENT_REQUEST_CODE);
    }

    protected void requestUserConsent() {
        // To be replaced by a partner-set values as returned by the backend
        Intent intent = new Intent(this, ConsentActivity.class);
        intent.putExtra(ConsentActivity.TAG,
                mParams.getString(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO));
        intent.putExtra(ConsentActivity.PARTNER_LOGO,
                BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_purse));
        intent.putExtra(ConsentActivity.PARTNER_NAME, "AM Loans Inc.");
        intent.putExtra(ConsentActivity.PRIVACY_LINK, "www.google.com");
        startActivityForResult(intent, USER_CONSENT_REQUEST_CODE);
    }

    public void getStarted(View view) {
        if (mParams != null) {
            if (mKYCProductType == KYC_PRODUCT_TYPE.BVN_CONSENT) {
                if (mParams.getBoolean(REQUIRE_CONSENT)) {
                    mParams.remove(REQUIRE_CONSENT);
                    sidConsentConfig.show("a_test_tag", "Smile Test",
                            R.drawable.ic_purse, false, "https://www.google" +
                                    ".com",
                            "NG", "BVN_MFA");
                }
                return;
            }

        }
        if ((mParams != null) && (mParams.getBoolean(REQUIRE_CONSENT))) {
            mParams.remove(REQUIRE_CONSENT);
            requestUserConsent();
        } else if (mKYCProductType != null) {
            switch (mKYCProductType) {
                case DOCUMENT_VERIFICATION:
                    proceedWithDocV();
                    break;
                case SMART_SELFIE_AUTH:
                    showSmartSelfieDialog();
                    break;
                case BASIC_KYC:
                case ENHANCED_KYC:
                case BIOMETRIC_KYC:
                    proceedWithIDInfo();
                    break;
                default:
                    proceedWithSelfie();
            }
        }
    }

    private void proceedWithDocV() {
        // Default to Non-Enrolled User Selfie+ID
        HashMap<String, String> docVParams = new HashMap<>();
        docVParams.put(DOC_V_CAPTURE_TYPE,
                DOC_VER_TYPE.SELFIE_PLUS_ID_CARD.toString());
        docVParams.put(DOC_V_USER_SELFIE_OPTION,
                DocVOptionDialog.DOC_VER_OPTION.NON_ENROLLED_USER.toString());
        mParams.putSerializable(DOC_V_PARAM, docVParams);
        setCountryAndIDType();
    }

    private void setCountryAndIDType() {
        CCAndIdTypeDialog.DlgListener listener =
                new CCAndIdTypeDialog.DlgListener() {
                    @Override
                    public void submit(String countryCode, String idType) {
                        if (mParams != null) {
                            mParams.putString(SIDJobResultActivity.DOC_COUNTRY_PARAM, countryCode);
                            mParams.putString(SIDJobResultActivity.DOC_ID_TYPE_PARAM, idType);
                        }
                        useSmileUIScreen(CaptureType.SELFIE_AND_ID_CAPTURE);
                    }

                    @Override
                    public void cancel() {
                        Toast.makeText(
                                        GetStartedActivity.this,
                                        "To verify this " + "document, kindly" +
                                                " " + "select a country and " +
                                                "an ID type",
                                        Toast.LENGTH_LONG)
                                .show();
                        finish();
                    }
                };

        new CCAndIdTypeDialog(this, true, listener).showDialog();
    }

    private void showSmartSelfieDialog() {
        new SmartAuthOptionDialog(
                this,
                (type) -> {
                    if (mParams != null) {
                        HashMap<String, String> docVParams =
                                new HashMap() {
                                    {
                                        put(SMART_AUTH_CAPTURE_TYPE,
                                                type.toString());
                                    }
                                };

                        mParams.putSerializable(SMART_AUTH_PARAM, docVParams);
                        proceedWithSelfie();
                    }
                })
                .showDialog();
    }

    private void proceedWithIDInfo() {
        idInfoActivityLauncher.launch(new Intent(this,
                SIDIDInfoActivity.class) {
            {
                putExtras(mParams);
            }
        });
    }

    private void proceedWithSelfie() {
        useSmileUIScreen(CaptureType.SELFIE);
    }

    public void useSmileUIScreen(CaptureType captureType) {
        SIDCaptureManager.Builder sidCaptureManager =
                new SIDCaptureManager.Builder(
                        this, captureType,
                        SMILE_UI_REQUEST_CODE)
                        .setTag(mParams.getString(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO));
        sidCaptureManager.build().start();
    }

    private void goToResultScreen() {
        startActivity(new Intent(this, SIDJobResultActivity.class) {{
            putExtras(mParams);
        }});
        finish();
    }

    ActivityResultLauncher<Intent> idInfoActivityLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            mParams.putSerializable(SIDJobResultActivity.USER_ID_INFO_PARAM,
                                    result.getData().getSerializableExtra(SIDJobResultActivity.USER_ID_INFO_PARAM));
                            if (mKYCProductType == BaseSIDActivity.KYC_PRODUCT_TYPE.BIOMETRIC_KYC) {
                                useSmileUIScreen(CaptureType.SELFIE);
                            } else {
                                goToResultScreen();
                            }
                        }
                    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SMILE_UI_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                goToResultScreen();
            } else {
                Toast.makeText(
                                this, "Oops Smile ID UI Selfie did not return" +
                                        " " + "a success", Toast.LENGTH_LONG)
                        .show();
            }

            finish();
        }

        if (requestCode == USER_CONSENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getStarted(null);
            } else {
                finish();
                Toast.makeText(
                                this,
                                getString(R.string.consent_screen_consent_declined_error), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

}

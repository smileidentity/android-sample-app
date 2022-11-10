package com.demo.smileid.sid_sdk;

import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.BIOMETRIC_KYC;
import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.DOCUMENT_VERIFICATION;
import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.ENHANCED_KYC;
import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.ENROLL_TEST;
import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH;
import static com.demo.smileid.sid_sdk.SIDSelfieActivity.DOC_V_CAPTURE_TYPE;
import static com.demo.smileid.sid_sdk.SIDSelfieActivity.DOC_V_PARAM;
import static com.demo.smileid.sid_sdk.SIDSelfieActivity.DOC_V_USER_SELFIE_OPTION;
import static com.demo.smileid.sid_sdk.SIDSelfieActivity.SMART_AUTH_CAPTURE_TYPE;
import static com.demo.smileid.sid_sdk.SIDSelfieActivity.SMART_AUTH_PARAM;
import static com.smileid.smileidui.IntentHelper.SMILE_REQUEST_RESULT_TAG;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.demo.smileid.sid_sdk.DocVOptionDialog.DOC_VER_TYPE;
import com.smileid.smileidui.CaptureType;
import com.smileid.smileidui.SIDCaptureManager;
import com.smileidentity.libsmileid.core.consent.ConsentActivity;

import java.util.HashMap;

public class GetStartedActivity extends BaseSIDActivity {

    private static final int SMILE_SELFIE_REQUEST_CODE = 778;
    private static final int USER_CONSENT_REQUEST_CODE = 123;
    public static final String REQUIRE_CONSENT = "REQUIRE_CONSENT";
    private Bundle mParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_started);
        findViewById(R.id.ivBackArrow).setOnClickListener(v -> super.onBackPressed());

        mParams = getIntent().getExtras();
    }

    protected void requestUserConsent() {
        //To be replaced by a partner-set values as returned by the backend
        Intent intent = new Intent(this, ConsentActivity.class);
        intent.putExtra(ConsentActivity.TAG, "USER_TAG");
        intent.putExtra(ConsentActivity.PARTNER_LOGO, BitmapFactory.decodeResource(getResources(), R.drawable.ic_purse));
        intent.putExtra(ConsentActivity.PARTNER_NAME, "AM Loans Inc.");
        intent.putExtra(ConsentActivity.PRIVACY_LINK, "www.google.com");
        startActivityForResult(intent, USER_CONSENT_REQUEST_CODE);
    }

    public void getStarted(View view) {
        if ((mParams != null) && (mParams.getBoolean(REQUIRE_CONSENT))) {
            mParams.remove(REQUIRE_CONSENT);
            requestUserConsent();
        } else {
            KYC_PRODUCT_TYPE productType = (KYC_PRODUCT_TYPE) mParams.get(KYC_PRODUCT_TYPE_PARAM);

            switch (productType) {
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
        docVParams.put(DOC_V_CAPTURE_TYPE, DOC_VER_TYPE.SELFIE_PLUS_ID_CARD.toString());
        docVParams.put(DOC_V_USER_SELFIE_OPTION, DocVOptionDialog.DOC_VER_OPTION.NON_ENROLLED_USER.toString());
        mParams.putSerializable(DOC_V_PARAM, docVParams);
        proceedWithIDCard();
    }

    private void showSmartSelfieDialog() {
    new SmartAuthOptionDialog(
            this,
            (type) -> {
              if (mParams != null) {
                HashMap<String, String> docVParams =
                    new HashMap() {
                      {
                        put(SMART_AUTH_CAPTURE_TYPE, type.toString());
                      }
                    };

                mParams.putSerializable(SMART_AUTH_PARAM, docVParams);
                proceedWithSelfie();
              }
            })
        .showDialog();
    }

    private void proceedWithIDCard() {
        go2Screen(SIDIDCardActivity.class);
    }

    private void proceedWithIDInfo() {
        go2Screen(SIDIDInfoActivity.class);
    }

    private void proceedWithSelfie() {
//        useLocalScreen();
        useSmileUIScreen();
    }

    private void useLocalScreen() {
        go2Screen(SIDSelfieActivity.class);
    }

    private void go2Screen(Class clazz) {
        finish();

        startActivity(
            new Intent(this, clazz) {
                {
                    putExtras(mParams);
                }
            }
        );
    }

    public void useSmileUIScreen() {
        SIDCaptureManager.Builder sidCaptureManager = new SIDCaptureManager.Builder(this,
            CaptureType.SELFIE, SMILE_SELFIE_REQUEST_CODE);

        sidCaptureManager.build().start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SMILE_SELFIE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Class clazz = null;

                if ((mKYCProductType == ENROLL_TEST) ||
                        (mKYCProductType == SMART_SELFIE_AUTH)) {
                    clazz = SIDJobResultActivity.class;
                } else if (mKYCProductType == ENHANCED_KYC) {
                    clazz = SIDIDInfoActivity.class;
                } else if ((mKYCProductType == BIOMETRIC_KYC) ||
                        (mKYCProductType == DOCUMENT_VERIFICATION)) {
                    clazz = SIDIDCardActivity.class;
                }

                if (clazz == null) return;

                startActivity(
                    new Intent(this, clazz) {
                        {
                            putExtra(BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM, mKYCProductType);
                            putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO,
                                data.getStringExtra(SMILE_REQUEST_RESULT_TAG));
                        }
                    }
                );
            } else {
                Toast.makeText(this, "Oops Smile ID UI Selfie did not return a success", Toast.LENGTH_LONG).show();
            }

            finish();
        }

        if (requestCode == USER_CONSENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                moveForward();
                getStarted(null);
            } else {
                finish();
                Toast.makeText(this, getString(R.string.consent_screen_consent_declined_error),
                    Toast.LENGTH_LONG).show();
            }
        }
    }
}
package com.demo.smileid.sid_sdk;

import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.BASIC_KYC;
import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.BIOMETRIC_KYC;
import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.DOCUMENT_VERIFICATION;
import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.ENHANCED_KYC;
import static com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH;
import static com.demo.smileid.sid_sdk.SIDSelfieActivity.DOC_V_PARAM;
import static com.demo.smileid.sid_sdk.SIDSelfieActivity.DOC_V_CAPTURE_TYPE;
import static com.demo.smileid.sid_sdk.SIDSelfieActivity.DOC_V_USER_SELFIE_OPTION;
import static com.smileid.smileidui.IntentHelper.SMILE_REQUEST_RESULT_TAG;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.demo.smileid.sid_sdk.DocVOptionDialog.DOC_VER_TYPE;
import com.smileid.smileidui.CaptureType;
import com.smileid.smileidui.SIDCaptureManager;
import java.util.HashMap;

public class GetStartedActivity extends BaseSIDActivity {

    private static final int SMILE_SELFIE_REQUEST_CODE = 778;
    private static final int USER_CONSENT_REQUEST_CODE = 123;
    public static final String REQUIRE_CONSENT = "REQUIRE_CONSENT";
    private Bundle mParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

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
            boolean isDocVJob = (mParams != null) && (mParams.containsKey(KYC_PRODUCT_TYPE_PARAM));
            Log.d("JOB_PARAM", "" + isDocVJob);
            isDocVJob &= (mParams.get(KYC_PRODUCT_TYPE_PARAM) == DOCUMENT_VERIFICATION);
            Log.d("JOB_PARAM", mParams.get(KYC_PRODUCT_TYPE_PARAM).toString() + " : " + isDocVJob);

            if (isDocVJob) {
                showDocVOptionDlg();
                return;
            }

            proceedWithSelfie();
        }
    }

    private void showDocVOptionDlg() {
        new DocVOptionDialog(this, (type, option) -> {
            if (mParams != null) {
                HashMap<String, String> docVParams = new HashMap() {
                    {
                        put(DOC_V_CAPTURE_TYPE, type.toString());

                        if (type == DOC_VER_TYPE.SELFIE_PLUS_ID_CARD) {
                            put(DOC_V_USER_SELFIE_OPTION, option.toString());
                        }
                    }
                };

                mParams.putSerializable(DOC_V_PARAM, docVParams);

                if (type == DOC_VER_TYPE.SELFIE_PLUS_ID_CARD) {
                    proceedWithSelfie();
                } else {
                    proceedWithIDCard();
                }
            }
        }).showDialog();
    }

    private void proceedWithIDCard() {
        finish();

        startActivity(
            new Intent(this, SIDIDCardActivity.class) {
                {
                    putExtras(mParams);
                }
            }
        );
    }

    private void proceedWithSelfie() {
        finish();
        useLocalScreen();
//        useSmileUIScreen();
    }

    private void useLocalScreen() {
        startActivity(
            new Intent(this, SIDSelfieActivity.class) {
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

                if ((mKYCProductType == BASIC_KYC) ||
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
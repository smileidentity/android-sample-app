package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos;
import com.demo.smileid.sid_sdk.sidNet.InternetStateBroadCastReceiver;
import com.demo.smileid.sid_sdk.sidNet.SIDNetworkingUtils;
import com.smileidentity.libsmileid.core.SIDConfig;
import com.smileidentity.libsmileid.core.SIDNetworkRequest;
import com.smileidentity.libsmileid.core.SIDResponse;
import com.smileidentity.libsmileid.core.SIDTagManager;
import com.smileidentity.libsmileid.exception.SIDException;
import com.smileidentity.libsmileid.model.GeoInfos;
import com.smileidentity.libsmileid.model.PartnerParams;
import com.smileidentity.libsmileid.model.SIDMetadata;
import com.smileidentity.libsmileid.model.SIDNetData;
import com.smileidentity.libsmileid.model.SIDUserIdInfo;
import com.smileidentity.libsmileid.utils.AppData;
import java.util.HashMap;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_JOB_ID;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_USER_ID;

public class SIDJobResultActivity extends BaseSIDActivity implements SIDNetworkRequest.OnCompleteListener,
        SIDNetworkRequest.OnErrorListener,
        SIDNetworkRequest.OnUpdateListener,
        SIDNetworkRequest.OnEnrolledListener,
        SIDNetworkRequest.OnAuthenticatedListener,
        SIDNetworkRequest.OnDocVerificationListener,
        InternetStateBroadCastReceiver.OnConnectionReceivedListener {

    public final static String USER_ID_INFO_PARAM = "USER_ID_INFO_PARAM";
    public final static String USER_SELFIE_PARAM = "USER_SELFIE_PARAM";

    private TextView mTvResult, mTvConfidenceValue;
    private ProgressBar mPbLoading;

    private String mSelectedCountryName = "", mSelectedIdCard = "", mCurrentTag;
    private SIDNetworkRequest mSIDNetworkRequest;
    private SharedPreferences mSharedPreferences;
    private InternetStateBroadCastReceiver mInternetStateBR;
    private boolean mEnrolledUser = false;
    private SIDConfig mConfig;
    private HashMap<String, String> mSIDUserIdInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_result);

        initVars();
        initViews();
        buildNetObserver();
        buildNetRequest();
        uploadNow(null);
    }

    private void initVars() {
        Intent intent = getIntent();

        if (intent != null) {
            mEnrolledUser = intent.getBooleanExtra(USER_SELFIE_PARAM, false);
            mCurrentTag = intent.getStringExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);
            mKYCProductType = (KYC_PRODUCT_TYPE) intent.getSerializableExtra(BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM);
            mSIDUserIdInfo = (HashMap<String, String>) intent.getSerializableExtra(USER_ID_INFO_PARAM);

            if (mSIDUserIdInfo != null) {
                mSelectedCountryName = mSIDUserIdInfo.get(SIDUserIdInfo.COUNTRY);
                mSelectedIdCard = mSIDUserIdInfo.get(SIDUserIdInfo.ID_TYPE);
            }
        }

        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    }

    private void initViews() {
        mTvResult = findViewById(R.id.tvResult);
        mPbLoading = findViewById(R.id.pbLoading);
        mTvConfidenceValue = findViewById(R.id.tvConfidenceValue);
    }

    private void buildNetObserver() {
        mInternetStateBR = new InternetStateBroadCastReceiver();
        mInternetStateBR.setOnConnectionReceivedListener(this);
    }

    private void buildNetRequest() {
        mSIDNetworkRequest = new SIDNetworkRequest(this);
        mSIDNetworkRequest.setOnCompleteListener(this);
        mSIDNetworkRequest.set0nErrorListener(this);
        mSIDNetworkRequest.setOnUpdateListener(this);
        mSIDNetworkRequest.setOnEnrolledListener(this);
        mSIDNetworkRequest.setOnAuthenticatedListener(this);
        mSIDNetworkRequest.setOnDocVerificationListener(this);
        mSIDNetworkRequest.initialize();
    }

    public void uploadNow(View view) {
        if (mKYCProductType == KYC_PRODUCT_TYPE.DOCUMENT_VERIFICATION) {
            setCountryAndIDType();
        } else {
            upload(mCurrentTag);
        }
    }

    private SIDMetadata setUserIdInfo(SIDMetadata metadata) {
        SIDUserIdInfo userIdInfo = metadata.getSidUserIdInfo();
        userIdInfo.setCountry(mSelectedCountryName);

        if (mSIDUserIdInfo != null) {
            userIdInfo.setFirstName(mSIDUserIdInfo.get(SIDUserIdInfo.FIRST_NAME));
            userIdInfo.setLastName(mSIDUserIdInfo.get(SIDUserIdInfo.LAST_NAME));
            userIdInfo.setIdNumber(mSIDUserIdInfo.get(SIDUserIdInfo.ID_NUMBER));
            userIdInfo.additionalValue(SIDUserIdInfo.DOB, mSIDUserIdInfo.get(SIDUserIdInfo.DOB));
        }

        userIdInfo.setIdType(mSelectedIdCard.replace(" ", "_"));
        return metadata;
    }

    private void setCountryAndIDType() {
        CCAndIdTypeDialog.DlgListener listener = new CCAndIdTypeDialog.DlgListener() {
            @Override
            public void submit(String countryCode, String idType) {
                mSelectedCountryName = countryCode;
                mSelectedIdCard = idType;
                upload(mCurrentTag);
            }

            @Override
            public void cancel() {
                Toast.makeText(SIDJobResultActivity.this, "To verify this document, kindly select a country and an ID type", Toast.LENGTH_LONG).show();
            }
        };

        new CCAndIdTypeDialog(this, listener).showDialog();
    }

    private void upload(String tag) {
        SIDMetadata metadata = new SIDMetadata();

        if ((mKYCProductType == KYC_PRODUCT_TYPE.BASIC_KYC) ||
                (mKYCProductType == KYC_PRODUCT_TYPE.ENHANCED_KYC) ||
                    (mKYCProductType == KYC_PRODUCT_TYPE.DOCUMENT_VERIFICATION)) {
            setUserIdInfo(metadata);
        }

        SIDConfig sidConfig = createConfig(tag, metadata);

        if (SIDNetworkingUtils.haveNetworkConnection(this)) {
            mPbLoading.setVisibility(View.VISIBLE);
            mSIDNetworkRequest.submit(sidConfig);
        } else {
            SIDTagManager.getInstance(this).saveConfig(tag, sidConfig.getJobType(),
                sidConfig.getMode(), sidConfig.getGeoInformation(), sidConfig.getSIDMetadata(),
                    this);

            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private SIDConfig createConfig(String tag, SIDMetadata metadata) {
        SIDNetData data = new SIDNetData(this, AppData.getInstance(this).getSDKEnvir());

        if (!TextUtils.isEmpty(getSavedUserId())) {
            setPartnerParamsForReenroll(metadata);
        }

        GeoInfos infos = SIDGeoInfos.getInstance().getGeoInformation();

        mConfig = new SIDConfig.Builder(this) {
            {
                setSmileIdNetData(data);
                setGeoInformation(infos);
                useEnrolledImage(mEnrolledUser);
                setJobType(mKYCProductType.getJobType());
                setSIDMetadata(metadata != null ? metadata : new SIDMetadata());

                setMode((mKYCProductType == KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH) ?
                    SIDConfig.Mode.AUTHENTICATION : SIDConfig.Mode.ENROLL);
            }
        }.build(mCurrentTag);

        return mConfig;
    }

    @Override
    public void onComplete() {
        mPbLoading.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "Job Complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(SIDException e) {
        mPbLoading.setVisibility(View.INVISIBLE);
        mTvResult.setTextColor(Color.RED);
        mTvResult.setText(e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onUpdate(int progress) {
        mTvResult.setText("progress " + String.valueOf(progress) + " % ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mInternetStateBR.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mInternetStateBR.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSIDNetworkRequest.registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSIDNetworkRequest.unregisterListeners();
    }

    private void setPartnerParamsForReenroll(SIDMetadata metadata) {
        PartnerParams params = metadata.getPartnerParams();
        params.setUserId(getSavedUserId());
    }

    @Override
    public void onEnrolled(SIDResponse response) {
        saveUserId(response.getPartnerParams().getUserId(), response.getPartnerParams().getJobId());
        mPbLoading.setVisibility(View.INVISIBLE);
        String message;
        boolean approved = false;

        switch (response.getResultCodeState()) {
            case SIDResponse.SID_RESPONSE_ENROL_REJECTED:
            case SIDResponse.SID_RESPONSE_ID_REJECTED:
            case SIDResponse.SID_RESPONSE_ENROL_WID_REJECTED:
                //Enroll/Register was rejected
                message = getString(R.string.demo_enrolled_failed);
                break;
            case SIDResponse.SID_RESPONSE_ENROL_WID_PROV_APPROVED:
            case SIDResponse.SID_RESPONSE_ENROL_PROVISIONAL_APPROVAL:
                //Enroll/Register was provisionally approved
                approved = true;
                message = getString(R.string.demo_provisionally_enrolled);
                break;
            case SIDResponse.SID_RESPONSE_IMAGE_NOT_USABLE:
                //Enroll/Register uploaded images were unsuable
                message = getString(R.string.demo_enrolled_image_unusable);
                break;
            case SIDResponse.SID_RESPONSE_ENROL_APPROVED:
            case SIDResponse.SID_RESPONSE_ENROL_WID_APPROVED:
                message = getString(R.string.demo_enrolled_successfully);
                break;
            default:
                message = getString(R.string.demo_enrolled_failed);
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (!TextUtils.isEmpty(response.getResultText())) {
            stringBuilder.append("Result Text : ").append(response.getResultText())
                .append(System.getProperty("line.separator"));
        }

        if (response.getConfidenceValue() > 0) {
            stringBuilder.append(getString(R.string.demo_enrolled_confidence_value, response.getConfidenceValue()));
        }

        mTvConfidenceValue.setText(message);
        mTvResult.setVisibility(TextUtils.isEmpty(stringBuilder) ? View.GONE : View.VISIBLE);
        mTvResult.setText(stringBuilder);

        go2Next(approved, message);
    }

    @Override
    public void onAuthenticated(SIDResponse response) {
        String message;
        boolean approved = false;

        switch (response.getResultCodeState()) {
            case SIDResponse.SID_RESPONSE_AUTH_REJECTED:
            case SIDResponse.SID_RESPONSE_ID_REJECTED:
                //Auth was rejected
                message = getString(R.string.demo_auth_failed);
                break;

            case SIDResponse.SID_RESPONSE_UPDATE_PHOTO_REJECTED:
                //update photo was rejected
                message = getString(R.string.demo_update_image_failed);
                break;

            case SIDResponse.SID_RESPONSE_AUTH_PROVISIONAL_APPROVAL:
                //auth was provisionally approved
                message = getString(R.string.demo_provisionally_authed);
                approved = true;
                break;

            case SIDResponse.SID_RESPONSE_UPDATE_PHOTO_PROV_APPROVAL:
                //update photo was provisionally approved
                message = getString(R.string.demo_update_photo_provisional);
                break;

            case SIDResponse.SID_RESPONSE_IMAGE_NOT_USABLE:
                //auth uploaded images were unsuable
                message = getString(R.string.demo_auth_image_unusable);
                break;

            case SIDResponse.SID_RESPONSE_UPDATE_PHOTO_APPROVED:
                //update photo approved
                message = getString(R.string.demo_update_image_success);
                break;

            case SIDResponse.SID_RESPONSE_AUTH_APPROVED:
                //auth approved
                message = getString(R.string.demo_auth_successfully);
                approved = true;
                break;

            default:
                message = getString(R.string.demo_auth_failed);
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (!TextUtils.isEmpty(response.getResultText())) {
            stringBuilder.append("Result Text : ").append(response.getResultText())
                .append(System.getProperty("line.separator"));
        }

        if (response.getConfidenceValue() > 0) {
            stringBuilder.append(getString(R.string.demo_enrolled_confidence_value, response.getConfidenceValue()));
        }

        mTvConfidenceValue.setText(message);
        mTvResult.setVisibility(TextUtils.isEmpty(stringBuilder) ? View.GONE : View.VISIBLE);
        mTvResult.setText(stringBuilder);

        go2Next(approved, message);
    }

    private void go2Next(boolean result, final String message) {
        Class clazz = (result) ? SIDJobCompletedActivity.class : SIDJobFailedActivity.class;

        Runnable runnable = () -> {
            finish();

            startActivity(
                new Intent(SIDJobResultActivity.this, clazz) {
                    {
                        putExtra(SIDJobFailedActivity.FAILED_MSG, message);
                    }
                }
            );
        };

        new Handler().postDelayed(runnable, 2000);
    }

    private void saveUserId(String userId, String jobId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SHARED_PREF_USER_ID, userId);
        editor.putString(SHARED_PREF_JOB_ID, jobId);
        editor.apply();
    }

    //Retrieve user id to be saved
    private String getSavedUserId() {
        return mSharedPreferences.getString(SHARED_PREF_USER_ID, "");
    }

    @Override
    public void onInternetStateChanged(boolean recovered) {
        /*if (recovered && mSAutoUpload.isChecked()) {
            upload(mCurrentTag);
        }*/
    }

    @Override
    public void onDocVerified(SIDResponse response) {
        StringBuilder stringBuilder = new StringBuilder();

        if (!TextUtils.isEmpty(response.getResultText())) {
            stringBuilder.append("Result Text : ").append(response.getResultText())
                .append(System.getProperty("line.separator"));
        }

        if (response.getConfidenceValue() > 0) {
            stringBuilder.append(getString(R.string.demo_enrolled_confidence_value, response.getConfidenceValue()));
        }

        go2Next(true, "");
    }
}

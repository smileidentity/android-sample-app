package com.demo.smileid.sid_sdk;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos;
import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.demo.smileid.sid_sdk.sidNet.InternetStateBroadCastReceiver;
import com.demo.smileid.sid_sdk.sidNet.SIDNetworkingUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.smileidentity.libsmileid.core.SIDConfig;
import com.smileidentity.libsmileid.core.SIDNetworkRequest;
import com.smileidentity.libsmileid.core.SIDResponse;
import com.smileidentity.libsmileid.core.SIDTagManager;
import com.smileidentity.libsmileid.core.idcard.IdCard;
import com.smileidentity.libsmileid.exception.SIDException;
import com.smileidentity.libsmileid.model.GeoInfos;
import com.smileidentity.libsmileid.model.PartnerParams;
import com.smileidentity.libsmileid.model.SIDMetadata;
import com.smileidentity.libsmileid.model.SIDNetData;
import com.smileidentity.libsmileid.model.SIDUserIdInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_JOB_ID;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_USER_ID;

public class SIDEnrollResultActivity extends BaseSIDActivity implements SIDNetworkRequest.OnCompleteListener,
        SIDNetworkRequest.OnErrorListener,
        SIDNetworkRequest.OnUpdateListener,
        SIDNetworkRequest.OnEnrolledListener,
        InternetStateBroadCastReceiver.OnConnectionReceivedListener {

    private TextView mTvResult, mTvConfidenceValue;
    private Switch mSAutoUpload;
    private ProgressBar mPbLoading;
    private CountryCodePicker mCcpCountryPicker;
    private Spinner mSIdType;
    private boolean mHasId = false, mReEnrollUser = false, mHasNoIdCard, mMultipleEnroll,
            mContinueWithIdInfo;
    private int mEnrollType;
    private String mSelectedCountryName = "", mSelectedIdCard, mCurrentTag;
    private SIDNetworkRequest mSIDNetworkRequest;
    private SharedPreferences mSharedPreferences;
    private InternetStateBroadCastReceiver mInternetStateBR;
    private SIDConfig mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_enroll_result);

        Intent intent = getIntent();
        mHasId = intent.getBooleanExtra(SIDStringExtras.EXTRA_HAS_ID, false);
        mHasNoIdCard = intent.getBooleanExtra(SIDStringExtras.EXTRA_HAS_NO_ID_CARD, false);
        mReEnrollUser = intent.getBooleanExtra(SIDStringExtras.EXTRA_REENROLL, false);
        mEnrollType = intent.getIntExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, -1);
        mMultipleEnroll = intent.getBooleanExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL, false);
        mContinueWithIdInfo = intent.getBooleanExtra(SIDStringExtras.EXTRA_MULTIPLE_ENROLL_ADD_ID_INFO, false);
        mCurrentTag = intent.getStringExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);

        mTvResult = findViewById(R.id.tvResult);
        mPbLoading = findViewById(R.id.pbLoading);
        mTvConfidenceValue = findViewById(R.id.tvConfidenceValue);
        mSAutoUpload = findViewById(R.id.sAutoUpload);

        //SIDUserIDInfo testing
        mCcpCountryPicker = findViewById(R.id.ccpCountry);
        mCcpCountryPicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                getSelectedCountryName();
                populateIdCard();
            }
        });

        findViewById(R.id.clNoCardLayout).setVisibility((mHasNoIdCard || mContinueWithIdInfo) ?
                View.VISIBLE : View.GONE);

        if (mContinueWithIdInfo) {
            mSAutoUpload.setVisibility(View.GONE);
            findViewById(R.id.tvEnrollUploadNow).setVisibility(View.GONE);
        }

        findViewById(R.id.clMultipleEnrollActions).setVisibility((mMultipleEnroll) ? View.VISIBLE :
                View.GONE);

        mSIdType = findViewById(R.id.spIdType);
        mSIdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedIdCard = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getSelectedCountryName();
        populateIdCard();

        mSAutoUpload.setVisibility(mMultipleEnroll && !mContinueWithIdInfo ? View.VISIBLE : View.GONE);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mInternetStateBR = new InternetStateBroadCastReceiver();
        mInternetStateBR.setOnConnectionReceivedListener(this);

        mSIDNetworkRequest = new SIDNetworkRequest(this);
        mSIDNetworkRequest.setOnCompleteListener(this);
        mSIDNetworkRequest.set0nErrorListener(this);
        mSIDNetworkRequest.setOnUpdateListener(this);
        mSIDNetworkRequest.setOnEnrolledListener(this);
        mSIDNetworkRequest.initialize();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.id_number).setVisible(true);
        return true;
    }

    public void uploadNow(View view) {
        upload(mCurrentTag);
    }

    public void showDateDialog(View view) {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);

                ((TextInputLayout) findViewById(R.id.tiDOB)).getEditText().setText(
                        new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(
                                selectedDate.getTime()));
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                Calendar.DAY_OF_MONTH)).show();
    }

    public void skipIdInfo(View view) {
        mUseMultipleEnroll = true;
        startSelfieCapture(true, false);
        finish();
    }

    public void setIdInfo(View view) {
        if (isIdInfoValid()) {
            mUseMultipleEnroll = true;
            saveUserIdInfo();
            startSelfieCapture(true, false);
            finish();
        }
    }

    private String getSelectedCountryName() {
        return mSelectedCountryName = mCcpCountryPicker.getSelectedCountryName();
    }

    private void saveUserIdInfo() {
        SIDTagManager sidTagManager = SIDTagManager.getInstance(this);
        SIDMetadata metadata = new SIDMetadata();
        setUserIdInfo(metadata);
        sidTagManager.saveConfig(mCurrentTag, mContinueWithIdInfo ? 1 : 4, SIDConfig.Mode.ENROLL, null, metadata, this);
    }

    private boolean isIdInfoValid() {
        List<String> errors = new ArrayList<>();

        if (errors.size() > 0) {
            for (int i = 0; i < errors.size(); i++) {
                Toast.makeText(this, errors.get(i), Toast.LENGTH_SHORT).show();
            }
        }

        return (errors.size() == 0);
    }

    private SIDMetadata setUserIdInfo(SIDMetadata metadata) {
        SIDUserIdInfo userIdInfo = metadata.getSidUserIdInfo();
        userIdInfo.setCountry(mSelectedCountryName);
        userIdInfo.setFirstName(((TextInputLayout) findViewById(R.id.tiFirstName)).getEditText().getText().toString());
        userIdInfo.setLastName(((TextInputLayout) findViewById(R.id.tiLastName)).getEditText().getText().toString());
        userIdInfo.setIdNumber(((TextInputLayout) findViewById(R.id.tiIdNumber)).getEditText().getText().toString());
        userIdInfo.setIdType(mSelectedIdCard.replace(" ", "_"));
        userIdInfo.additionalValue("dob", ((TextInputEditText) findViewById(R.id.tietDOB)).getText().toString());
        return metadata;
    }

    private void upload(String tag) {
        SIDMetadata metadata = new SIDMetadata();

        if (mHasNoIdCard) {
            if (!isIdInfoValid()) {
                return;
            }

            setUserIdInfo(metadata);
            findViewById(R.id.clNoCardLayout).setVisibility(View.GONE);
        }

        SIDConfig sidConfig = createConfig(tag, metadata);

        if (SIDNetworkingUtils.haveNetworkConnection(this)) {
            mPbLoading.setVisibility(View.VISIBLE);

            if (mMultipleEnroll) {
                mSIDNetworkRequest.submitAll(sidConfig);
            } else {
                mSIDNetworkRequest.submit(sidConfig);
            }
        } else {

            //No internet connection so you can cache this job and
            // later use submitAll() to submit all offline jobs

            SIDTagManager.getInstance(this).saveConfig(tag, sidConfig.getJobType(), sidConfig.getMode(), sidConfig.getGeoInformation(), sidConfig.getSIDMetadata(), this);
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private SIDConfig createConfig(String tag) {
        return createConfig(tag, null);
    }

    private SIDConfig createConfig(String tag, SIDMetadata metadata) {
        SIDNetData data = new SIDNetData(this, SIDNetData.Environment.TEST);

        if (mReEnrollUser && !TextUtils.isEmpty(getSavedUserId())) {
            //USe the PartnerParams object to set the user id of the user to be reernolled.
            // Should be declared before the configuration is submitted
            setPartnerParamsForReenroll(metadata);
        }

        GeoInfos infos = SIDGeoInfos.getInstance().getGeoInformation();

        SIDConfig.Builder builder;

        builder = new SIDConfig.Builder(this)
                .setMode(SIDConfig.Mode.ENROLL)
                .setSmileIdNetData(data)
                .setGeoInformation(infos)
                .setSIDMetadata(metadata != null ? metadata : new SIDMetadata())
                .setJobType(mEnrollType);

        mConfig = builder.build(mCurrentTag);
        return mConfig;
    }


    @Override
    public void onComplete() {
        Toast.makeText(this, "Job Complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(SIDException e) {
        mPbLoading.setVisibility(View.GONE);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

        if (mMultipleEnroll) {
            mTvResult.setTextColor(Color.RED);
            mTvResult.setText("TAG: " + e.getFailedTag() + "FAILED - remaining tags:" + mConfig.getIdleTags(this).size());
        }

        e.printStackTrace();
    }

    @Override
    public void onUpdate(int progress) {
        if (Color.BLACK != mTvResult.getCurrentTextColor()) {
            mTvResult.setTextColor(Color.BLACK);
        }

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
        //Get smile job id
        //response.getStatusResponse().getResult().getSmileJobID();
        saveUserId(response.getPartnerParams().getUserId(), response.getPartnerParams().getJobId());
        mPbLoading.setVisibility(View.GONE);
        int color;
        String message;

        switch (response.getResultCodeState()) {
            case SIDResponse.SID_RESPONSE_ENROL_REJECTED:
            case SIDResponse.SID_RESPONSE_ID_REJECTED:
            case SIDResponse.SID_RESPONSE_ENROL_WID_REJECTED:
                //Enroll/Register was rejected
                color = Color.RED;
                message = getString(R.string.demo_enrolled_failed);
                break;
            case SIDResponse.SID_RESPONSE_ENROL_WID_PROV_APPROVED:
            case SIDResponse.SID_RESPONSE_ENROL_PROVISIONAL_APPROVAL:
                //Enroll/Register was provisionally approved
                message = getString(R.string.demo_provisionally_enrolled);
                color = Color.GRAY;
                break;
            case SIDResponse.SID_RESPONSE_IMAGE_NOT_USABLE:
                //Enroll/Register uploaded images were unsuable
                color = Color.RED;
                message = getString(R.string.demo_enrolled_image_unusable);
                break;
            case SIDResponse.SID_RESPONSE_ENROL_APPROVED:
            case SIDResponse.SID_RESPONSE_ENROL_WID_APPROVED:
                color = Color.GREEN;
                message = getString(R.string.demo_enrolled_successfully);
                break;
            default:
                color = Color.RED;
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

        mTvResult.setTextColor(color);
        mTvResult.setText(message);
        mTvConfidenceValue.setVisibility(TextUtils.isEmpty(stringBuilder) ? View.GONE : View.VISIBLE);
        mTvConfidenceValue.setText(stringBuilder);
        findViewById(R.id.tvEnrollUploadNow).setVisibility(View.GONE);
    }

    //Emulates partner data source.
    //The user id of the enrollee is to be saved for later use
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
        if (recovered && mSAutoUpload.isChecked()) {
            upload(mCurrentTag);
        }
    }

    private void initSpinner(List<String> idTypes) {
        ArrayAdapter dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, idTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSIdType.setAdapter(dataAdapter);
    }

    private void populateIdCard() {
        IdCard idCard = IdTypeUtil.idCards(mSelectedCountryName);
        initSpinner(idCard.getIdCards());
    }
}
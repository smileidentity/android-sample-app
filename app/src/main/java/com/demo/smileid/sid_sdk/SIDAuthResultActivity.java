package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos;
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import static com.demo.smileid.sid_sdk.SIDStringExtras.EXTRA_TAG_PREFERENCES_AUTH_TAGS;

public class SIDAuthResultActivity extends AppCompatActivity implements SIDNetworkRequest.OnCompleteListener,
        SIDNetworkRequest.OnUpdateListener, SIDNetworkRequest.OnErrorListener,
        SIDNetworkRequest.OnAuthenticatedListener {

    private SIDNetworkRequest mSINetworkRequest;
    private TextView mTvResult, mTvConfidenceValue;
    private boolean mUseOfflineAuth, mUse258;
    private int mJobType = -1;
    //Provided userid and job id
    private SharedPreferences mSharedPreferences;
    private String mCurrentTag;
    private Queue<String> mTagsQueue = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_auth_result);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        mUse258 = intent.getBooleanExtra(SIDStringExtras.EXTRA_USE_258, false);
        mJobType = intent.getIntExtra(SIDStringExtras.EXTRA_ENROLL_TYPE, -1);
        mCurrentTag = intent.getStringExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);
        mUseOfflineAuth = intent.getBooleanExtra(SIDStringExtras.EXTRA_TAG_OFFLINE_AUTH, false);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        mTvResult = findViewById(R.id.tvResult);
        mTvConfidenceValue = findViewById(R.id.tvConfidenceValue);

        if (mUseOfflineAuth) {
            ((TextView) findViewById(R.id.tvUploadNow)).setText("Auth using saved data");
            getOfflineAuthTags();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.id_number).setVisible(true);
        return true;
    }

    private void createNetworkRequestManager() {
        mSINetworkRequest = new SIDNetworkRequest(this);
        mSINetworkRequest.setOnCompleteListener(this);
        mSINetworkRequest.setOnUpdateListener(this);
        mSINetworkRequest.setOnAuthenticatedListener(this);
        mSINetworkRequest.set0nErrorListener(this);
        mSINetworkRequest.initialize();
    }

    private void getOfflineAuthTags() {
        String tags = mSharedPreferences.getString(EXTRA_TAG_PREFERENCES_AUTH_TAGS, null);

        if (tags != null) {
            String[] tagsArray = tags.split(",");

            for (int i = 0; i < tagsArray.length; i++) {
                mTagsQueue.add(tagsArray[i]);
            }
        }
    }

    private void upload(SIDConfig config) {
        if (SIDNetworkingUtils.haveNetworkConnection(this)) {
            findViewById(R.id.pbLoading).setVisibility(View.VISIBLE);
            mSINetworkRequest.submit(config);
        } else {
            //No internet connection so you can cache this job and
            // later use submitAll() to submit all offline jobs
            SIDTagManager.getInstance(this).saveConfig(config.getSubmittedTag(), config.getJobType(), config.getMode(), config.getGeoInformation(), config.getSIDMetadata(), this);

            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private SIDConfig createConfig(SIDMetadata metadata) {
        SIDNetData data = new SIDNetData(this, SIDNetData.Environment.TEST);
        GeoInfos geoInfos = SIDGeoInfos.getInstance().getGeoInformation();
        //Uncomment to set user Provided partner Parameter
        //setPartnerParams();
        if (!TextUtils.isEmpty(getSavedUserId())) {
            //USe the PartnerParams object to set the user id of the user to be reernolled.
            // Should be declared before the configuration is submitted
            setPartnerParamsForReEnroll(metadata);
        }

        SIDConfig.Builder builder = new SIDConfig.Builder(this);
        builder.setSmileIdNetData(data)
                .setGeoInformation(geoInfos)
                .setJobType(mJobType)
                .setSIDMetadata(metadata)
                .setMode(SIDConfig.Mode.AUTHENTICATION);

        if (mUse258) {//Set the job type to 258 if user selected Auth 258 mode from the main screen
            builder.setJobType(258);
        }

        if (mUseOfflineAuth && mTagsQueue.size() > 0) {
            return builder.build(mTagsQueue.poll());
        } else {
            return builder.build(mCurrentTag);
        }
    }

    @NonNull
    private SIDConfig createConfig() {
        return createConfig(new SIDMetadata());
    }

    private void setPartnerParamsForReEnroll(SIDMetadata metadata) {
        PartnerParams params = metadata.getPartnerParams();
        params.setUserId(getSavedUserId());
    }

    //Retrieve user id to be saved
    private String getSavedUserId() {
        return mSharedPreferences.getString("SHARED_PREF_USER_ID", "");
    }

    @Override
    public void onComplete() {
        findViewById(R.id.pbLoading).setVisibility(View.GONE);
    }

    @Override
    public void onError(SIDException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        findViewById(R.id.pbLoading).setVisibility(View.GONE);
    }

    @Override
    public void onUpdate(int progress) {
        if (Color.BLACK != mTvResult.getCurrentTextColor()) {
            mTvResult.setTextColor(Color.BLACK);
        }

        mTvResult.setText("progress " + String.valueOf(progress) + " % ");
    }

    @Override
    public void onAuthenticated(SIDResponse response) {
        int color;
        String message;

        switch (response.getResultCodeState()) {
            case SIDResponse.SID_RESPONSE_AUTH_REJECTED:
            case SIDResponse.SID_RESPONSE_ID_REJECTED:
                //Auth was rejected
                color = Color.RED;
                message = getString(R.string.demo_auth_failed);
                break;
            case SIDResponse.SID_RESPONSE_UPDATE_PHOTO_REJECTED:
                //update photo was rejected
                color = Color.RED;
                message = getString(R.string.demo_update_image_failed);
                break;
            case SIDResponse.SID_RESPONSE_AUTH_PROVISIONAL_APPROVAL:
                //auth was provisionally approved
                message = getString(R.string.demo_provisionally_authed);
                color = Color.GRAY;
                break;
            case SIDResponse.SID_RESPONSE_UPDATE_PHOTO_PROV_APPROVAL:
                //update photo was provisionally approved
                message = getString(R.string.demo_update_photo_provisional);
                color = Color.GRAY;
                break;
            case SIDResponse.SID_RESPONSE_IMAGE_NOT_USABLE:
                //auth uploaded images were unsuable
                color = Color.RED;
                message = getString(R.string.demo_auth_image_unusable);
                break;
            case SIDResponse.SID_RESPONSE_UPDATE_PHOTO_APPROVED:
                //update photo approved
                color = Color.GREEN;
                message = getString(R.string.demo_update_image_success);
                break;
            case SIDResponse.SID_RESPONSE_AUTH_APPROVED:
                //auth approved
                color = Color.GREEN;
                message = getString(R.string.demo_auth_successfully);
                break;
            default:
                color = Color.RED;
                message = getString(R.string.demo_auth_failed);
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (!TextUtils.isEmpty(response.getResultText())) {
            stringBuilder.append("Result Text : ")
                    .append(response.getResultText())
                    .append(System.getProperty("line.separator"));
        }

        if (response.getConfidenceValue() > 0) {
            stringBuilder.append(getString(R.string.demo_enrolled_confidence_value, response.getConfidenceValue()));
        }

        mTvResult.setTextColor(color);
        mTvResult.setText(message);
        mTvConfidenceValue.setVisibility(View.VISIBLE);
        mTvConfidenceValue.setText(stringBuilder);
        findViewById(R.id.tvUploadNow).setVisibility(View.GONE);

        if (mUseOfflineAuth) {
            saveAuthTagsForLater();

            if (mTagsQueue.size() > 0) {
                mTvConfidenceValue.setVisibility(View.INVISIBLE);
                mTvResult.setTextColor(Color.BLACK);
                createNetworkRequestManager();
                upload(createConfig());
            }
        }
    }

    private void saveAuthTagsForLater() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String tags = null;

        if (mTagsQueue.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Iterator it = mTagsQueue.iterator();

            while (it.hasNext()) {
                sb.append(it.next()).append(",");
            }

            tags = sb.toString();
        }

        sharedPreferences.edit().putString(EXTRA_TAG_PREFERENCES_AUTH_TAGS, tags).apply();
    }

    public void uploadNow(View view) {
        createNetworkRequestManager();
        upload(createConfig());
    }
}

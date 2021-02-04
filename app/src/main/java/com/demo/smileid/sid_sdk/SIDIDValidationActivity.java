package com.demo.smileid.sid_sdk;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.demo.smileid.sid_sdk.sidNet.SIDNetworkingUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.smileidentity.libsmileid.core.RetryOnFailurePolicy;
import com.smileidentity.libsmileid.core.SIDConfig;
import com.smileidentity.libsmileid.core.SIDNetworkRequest;
import com.smileidentity.libsmileid.core.idcard.IdCard;
import com.smileidentity.libsmileid.exception.SIDException;
import com.smileidentity.libsmileid.model.SIDMetadata;
import com.smileidentity.libsmileid.model.SIDNetData;
import com.smileidentity.libsmileid.model.SIDUserIdInfo;
import com.smileidentity.libsmileid.net.model.idValidation.IDValidationResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class SIDIDValidationActivity extends AppCompatActivity implements View.OnClickListener, SIDNetworkRequest.OnCompleteListener, SIDNetworkRequest.OnIDValidationListener, SIDNetworkRequest.OnErrorListener {

    private TextView mTvActions, mTvFullName, mTvGender;
    private ImageView mIvPhoto;
    private LinearLayout layout_id_info;
    private RelativeLayout result_layout;
    private ProgressBar progressBar;
    private Spinner mSpIdType;
    private TextInputLayout tiIdNumber, tiFirstName, tiLastName, tiDOB, tiMiddleName;
    private String mSelectedCountryName = "", mSelectedIdCard;
    private CountryCodePicker ccp;
    private Button mUploadNowBtn;
    private String mCurrentTag;
    private SIDNetworkRequest mSINetworkRequest;
    private SIDConfig mConfig;
    private Button btnRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sid_validation_activity);
        mTvActions = findViewById(R.id.tv_actions);
        mTvFullName = findViewById(R.id.tv_full_name);
        mTvGender = findViewById(R.id.tv_gender);
        mIvPhoto = findViewById(R.id.iv_id_photo);
        layout_id_info = findViewById(R.id.layout_id_info);
        result_layout = findViewById(R.id.result_layout);
        progressBar = findViewById(R.id.pbLoading);
        btnRestart = findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(this);


        mSpIdType = findViewById(R.id.spIdType);
        mSpIdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedIdCard = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tiMiddleName = findViewById(R.id.ti_middle_name);
        tiIdNumber = findViewById(R.id.tiIdNumber);
        tiFirstName = findViewById(R.id.tiFirstName);
        tiLastName = findViewById(R.id.tiLastName);
        tiDOB = findViewById(R.id.tiDOB);
        tiDOB.setOnClickListener(this);
        tiDOB.getEditText().setOnClickListener(this);

        ccp = findViewById(R.id.ccpCountry);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                getSelectedCountryName();
                populateIdCard();
            }
        });

        getSelectedCountryName();
        populateIdCard();

        mUploadNowBtn = findViewById(R.id.tvEnrollUploadNow);
        mUploadNowBtn.setOnClickListener(this);

        mSINetworkRequest = new SIDNetworkRequest(this);
        mSINetworkRequest.setOnCompleteListener(this);
        mSINetworkRequest.setOnIDValidationListener(this);
        mSINetworkRequest.set0nErrorListener(this);
        mSINetworkRequest.initialize();

        getTag();

    }

    private boolean isIdInfoValid() {
        List<String> errors = new ArrayList<>();
        if (TextUtils.isEmpty(mSelectedCountryName)) {
            errors.add("Country name is required");
        }

        if (TextUtils.isEmpty(mSelectedIdCard)) {
            errors.add("ID type is required");
        }

        if (TextUtils.isEmpty(tiIdNumber.getEditText().getText())) {
            errors.add("ID number is required");
        }

        if (errors.size() > 0) {
            for (int i = 0; i < errors.size(); i++) {
                Toast.makeText(this, errors.get(i), Toast.LENGTH_SHORT).show();
            }
        }
        return (errors.size() == 0);
    }

    private void saveUserIdInfo() {
        //SIDTagManager sidTagManager = SIDTagManager.getInstance(this);
        SIDMetadata metadata = new SIDMetadata();
        setUserIdInfo(metadata);
        //sidTagManager.saveConfig(mCurrentTag, 5, SIDConfig.Mode.ENROLL, null, metadata, false);
    }


    private SIDMetadata setUserIdInfo(SIDMetadata metadata) {

        SIDUserIdInfo userIdInfo = metadata.getSidUserIdInfo();
        userIdInfo.setCountry(ccp.getSelectedCountryNameCode());
        userIdInfo.setIdNumber(tiIdNumber.getEditText().getText().toString());
        userIdInfo.setIdType(mSelectedIdCard.replace(" ", "_"));
        if (!TextUtils.isEmpty(tiFirstName.getEditText().getText())) {
            userIdInfo.setFirstName(tiFirstName.getEditText().getText().toString());
        }
        if (!TextUtils.isEmpty(tiMiddleName.getEditText().getText())) {
            userIdInfo.setMiddleName(tiMiddleName.getEditText().getText().toString());
        }
        if (!TextUtils.isEmpty(tiLastName.getEditText().getText())) {
            userIdInfo.setLastName(tiLastName.getEditText().getText().toString());
        }
        if (!TextUtils.isEmpty(tiDOB.getEditText().getText())) {
            userIdInfo.additionalValue("dob", tiDOB.getEditText().getText().toString());
        }
        return metadata;
    }

    private String getSelectedCountryName() {
        return mSelectedCountryName = ccp.getSelectedCountryName();
    }

    private void initSpinner(List<String> idTypes) {
        ArrayAdapter dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, idTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpIdType.setAdapter(dataAdapter);
    }

    private void populateIdCard() {
        IdCard idCard = IdTypeUtil.idCards(mSelectedCountryName);
        initSpinner(idCard.getIdCards());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvEnrollUploadNow:
                if (isIdInfoValid()) {
                    saveUserIdInfo();
                    upload();
                }
                break;
            case R.id.btnRestart:
                layout_id_info.setVisibility(View.VISIBLE);
                result_layout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                mUploadNowBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.tiDOB:
            case R.id.tietDOB:
                showDateDialog();
                break;
        }
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);

                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                tiDOB.getEditText().setText(fmt.format(selectedDate.getTime()));
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void upload() {
        SIDMetadata metadata = new SIDMetadata();


        if (!isIdInfoValid()) {
            return;
        }
        setUserIdInfo(metadata);
        SIDConfig sidConfig = createConfig(mCurrentTag, metadata);
        if (SIDNetworkingUtils.haveNetworkConnection(this)) {
            layout_id_info.setVisibility(View.GONE);
            mUploadNowBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            mSINetworkRequest.submit(sidConfig);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private SIDConfig createConfig(String tag, SIDMetadata metadata) {
        SIDNetData data = new SIDNetData(this,SIDNetData.Environment.TEST);

        SIDConfig.Builder builder = new SIDConfig.Builder(this)
                .setRetryOnfailurePolicy(getRetryOnFailurePolicy())
                .setMode(SIDConfig.Mode.ENROLL)
                .setSmileIdNetData(data)
                .setGeoInformation(null)
                .setSIDMetadata(metadata)
                .setJobType(5)
                .useIdCard(false);
        mConfig = builder.build(getTag());
        return mConfig;
    }

    private RetryOnFailurePolicy getRetryOnFailurePolicy() {
        RetryOnFailurePolicy retryOnFailurePolicy = new RetryOnFailurePolicy();
        retryOnFailurePolicy.setRetryCount(10);
        retryOnFailurePolicy.setRetryTimeout(TimeUnit.SECONDS.toMillis(15));
        return retryOnFailurePolicy;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onIdValidated(IDValidationResponse result) {
        progressBar.setVisibility(View.GONE);
        if (result == null || !result.isRequestSucceeded() || !result.isIdValidated()) {
            if (result == null) {
                Toast.makeText(this, "ID info validation failed please try again", Toast.LENGTH_SHORT).show();
            }
            if (result != null && !result.isIdValidated()) {
                Toast.makeText(this, "ID info validation failed " + result.getResultText(), Toast.LENGTH_SHORT).show();
            }
            layout_id_info.setVisibility(View.VISIBLE);
            result_layout.setVisibility(View.GONE);
            mUploadNowBtn.setVisibility(View.VISIBLE);
        } else {
            layout_id_info.setVisibility(View.GONE);
            result_layout.setVisibility(View.VISIBLE);
            HashMap<String, String> actionsResult = result.getActions();
            StringBuilder actions = new StringBuilder("ACTIONS : ").append(System.getProperty("line.separator"));
            if (actionsResult != null && actionsResult.size() > 0) {
                for (String key : result.getActions().keySet()) {
                    actions.append(key).append(" : ").append(result.getActions().get(key)).append(System.getProperty("line.separator"));
                }


            } else {
                actions.append("NOT AVAILABLE").append(System.getProperty("line.separator"));
            }
            mTvActions.setText(actions);

            StringBuilder fullName = new StringBuilder("FULL NAME : ").append(System.getProperty("line.separator"));
            if (!TextUtils.isEmpty(result.getFullName())) {
                fullName.append(result.getFullName());
            } else {
                fullName.append("NOT AVAILABLE");
            }
            mTvFullName.setText(fullName);

            StringBuilder gender = new StringBuilder("GENDER : ").append(System.getProperty("line.separator"));
            if (!TextUtils.isEmpty(result.getGender())) {
                gender.append(result.getGender()).append(System.getProperty("line.separator"));
            } else {
                gender.append("NOT AVAILABLE");
            }

            mTvGender.setText(gender);


            if (!TextUtils.isEmpty(result.getPhoto())) {
                byte[] decodedString = Base64.decode(result.getPhoto(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mIvPhoto.setImageBitmap(decodedByte);
            } else {
                gender.append(" PHOTO NOT AVAILABLE");
                mTvGender.setText(gender);
            }
        }


    }

    @Override
    public void onError(SIDException e) {
        if (e != null && !TextUtils.isEmpty(e.getMessage())) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ID info validation failed please try again", Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
        layout_id_info.setVisibility(View.VISIBLE);
        result_layout.setVisibility(View.GONE);
        mUploadNowBtn.setVisibility(View.VISIBLE);
    }

    private String getTag() {
        return mCurrentTag = String.format(Misc.USER_TAG, DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}

package com.demo.smileid.sid_sdk;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.demo.smileid.sid_sdk.sidNet.SIDNetworkingUtils;
import com.google.android.material.textfield.TextInputEditText;
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

public class SIDIDValidationActivity extends AppCompatActivity implements
        SIDNetworkRequest.OnCompleteListener, SIDNetworkRequest.OnIDValidationListener, SIDNetworkRequest.OnErrorListener {

    private String mSelectedCountryName = "", mSelectedIdCard, mCurrentTag;
    private SIDNetworkRequest mSINetworkRequest;
    private SIDConfig mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_validation_activity);

        ((Spinner) findViewById(R.id.spIdType)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedIdCard = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ((CountryCodePicker) findViewById(R.id.ccpCountry)).setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                getSelectedCountryName();
                populateIdCard();
            }
        });

        getSelectedCountryName();
        populateIdCard();

        mSINetworkRequest = new SIDNetworkRequest(this);
        mSINetworkRequest.setOnCompleteListener(this);
        mSINetworkRequest.setOnIDValidationListener(this);
        mSINetworkRequest.set0nErrorListener(this);
        mSINetworkRequest.initialize();

        getTag();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.id_number).setVisible(true);
        return true;
    }

    private boolean isIdInfoValid() {
        List<String> errors = new ArrayList<>();

        if (TextUtils.isEmpty(mSelectedCountryName)) {
            errors.add("Country name is required");
        }

        if (TextUtils.isEmpty(mSelectedIdCard)) {
            errors.add("ID type is required");
        }

        if (TextUtils.isEmpty(((TextInputLayout) findViewById(R.id.tiIdNumber)).getEditText().getText())) {
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
        userIdInfo.setCountry(mSelectedCountryName);
        userIdInfo.setFirstName(((TextInputLayout) findViewById(R.id.tiFirstName)).getEditText().getText().toString());
        userIdInfo.setLastName(((TextInputLayout) findViewById(R.id.tiLastName)).getEditText().getText().toString());
        userIdInfo.setIdNumber(((TextInputLayout) findViewById(R.id.tiIdNumber)).getEditText().getText().toString());
        userIdInfo.setIdType(mSelectedIdCard.replace(" ", "_"));
        userIdInfo.additionalValue("dob", ((TextInputEditText) findViewById(R.id.tietDOB)).getText().toString());
        return metadata;
    }

    private String getSelectedCountryName() {
        return mSelectedCountryName = ((CountryCodePicker) findViewById(R.id.ccpCountry)).getSelectedCountryName();
    }

    private void initSpinner(List<String> idTypes) {
        ArrayAdapter dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, idTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spIdType)).setAdapter(dataAdapter);
    }

    private void populateIdCard() {
        IdCard idCard = IdTypeUtil.idCards(mSelectedCountryName);
        initSpinner(idCard.getIdCards());
    }

    public void restartCapture(View view) {
        findViewById(R.id.clIdInfo).setVisibility(View.VISIBLE);
        findViewById(R.id.clResult).setVisibility(View.GONE);
        findViewById(R.id.pbLoading).setVisibility(View.GONE);
        findViewById(R.id.tvUploadNow).setVisibility(View.VISIBLE);
    }

    public void uploadNow(View view) {
        if (isIdInfoValid()) {
            saveUserIdInfo();
            upload();
        }
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

    private void upload() {
        SIDMetadata metadata = new SIDMetadata();

        if (!isIdInfoValid()) {
            return;
        }

        setUserIdInfo(metadata);
        SIDConfig sidConfig = createConfig(mCurrentTag, metadata);

        if (SIDNetworkingUtils.haveNetworkConnection(this)) {
            findViewById(R.id.clIdInfo).setVisibility(View.GONE);
            findViewById(R.id.tvUploadNow).setVisibility(View.GONE);
            findViewById(R.id.pbLoading).setVisibility(View.VISIBLE);
            mSINetworkRequest.submit(sidConfig);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private SIDConfig createConfig(String tag, SIDMetadata metadata) {
        SIDNetData data = new SIDNetData(this, SIDNetData.Environment.TEST);

        SIDConfig.Builder builder = new SIDConfig.Builder(this)
                .setRetryOnfailurePolicy(getRetryOnFailurePolicy())
                .setMode(SIDConfig.Mode.ENROLL)
                .setSmileIdNetData(data)
                .setGeoInformation(null)
                .setSIDMetadata(metadata)
                .setJobType(5);
        mConfig = builder.build(getTag());
        return mConfig;
    }

    private RetryOnFailurePolicy getRetryOnFailurePolicy() {
        return new RetryOnFailurePolicy() {
            {
                setRetryCount(10);
                setRetryTimeout(TimeUnit.SECONDS.toMillis(15));
            }
        };
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onIdValidated(IDValidationResponse result) {
        findViewById(R.id.pbLoading).setVisibility(View.GONE);

        if (result == null || !result.isRequestSucceeded() || !result.isIdValidated()) {
            if (result == null) {
                Toast.makeText(this, "ID info validation failed please try again", Toast.LENGTH_SHORT).show();
            }

            if (result != null && !result.isIdValidated()) {
                Toast.makeText(this, "ID info validation failed " + result.getResultText(), Toast.LENGTH_SHORT).show();
            }

            findViewById(R.id.clIdInfo).setVisibility(View.VISIBLE);
            findViewById(R.id.clResult).setVisibility(View.GONE);
            findViewById(R.id.tvUploadNow).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.clIdInfo).setVisibility(View.GONE);
            findViewById(R.id.clResult).setVisibility(View.VISIBLE);
            HashMap<String, String> actionsResult = result.getActions();
            StringBuilder actions = new StringBuilder("ACTIONS : ").append(System.getProperty("line.separator"));

            if (actionsResult != null && actionsResult.size() > 0) {
                for (String key : result.getActions().keySet()) {
                    actions.append(key).append(" : ").append(result.getActions().get(key)).append(System.getProperty("line.separator"));
                }
            } else {
                actions.append("NOT AVAILABLE").append(System.getProperty("line.separator"));
            }

            ((TextView) findViewById(R.id.tv_actions)).setText(actions);

            StringBuilder fullName = new StringBuilder("FULL NAME : ").append(System.getProperty("line.separator"));

            if (!TextUtils.isEmpty(result.getFullName())) {
                fullName.append(result.getFullName());
            } else {
                fullName.append("NOT AVAILABLE");
            }

            ((TextView) findViewById(R.id.tv_full_name)).setText(fullName);

            StringBuilder gender = new StringBuilder("GENDER : ").append(System.getProperty("line.separator"));

            if (!TextUtils.isEmpty(result.getGender())) {
                gender.append(result.getGender()).append(System.getProperty("line.separator"));
            } else {
                gender.append("NOT AVAILABLE");
            }

            ((TextView) findViewById(R.id.tv_gender)).setText(gender);

            if (!TextUtils.isEmpty(result.getPhoto())) {
                byte[] decodedString = Base64.decode(result.getPhoto(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ((ImageView) findViewById(R.id.iv_id_photo)).setImageBitmap(decodedByte);
            } else {
                gender.append(" PHOTO NOT AVAILABLE");
                ((TextView) findViewById(R.id.tv_gender)).setText(gender);
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

        findViewById(R.id.pbLoading).setVisibility(View.GONE);
        findViewById(R.id.clIdInfo).setVisibility(View.VISIBLE);
        findViewById(R.id.clResult).setVisibility(View.GONE);
        findViewById(R.id.tvUploadNow).setVisibility(View.VISIBLE);
    }

    private String getTag() {
        return mCurrentTag = String.format(Misc.USER_TAG, DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}
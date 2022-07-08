package com.demo.smileid.sid_sdk;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;
import com.smileidentity.libsmileid.core.idcard.IdCard;
import com.demo.smileid.sid_sdk.DropDownAdapter.DropDownObject;
import com.smileidentity.libsmileid.model.SIDUserIdInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE;

public class SIDIDInfoActivity extends AppCompatActivity {

    private String mSelectedCountryName = "", mSelectedIdCard, mCurrentTag;
    private KYC_PRODUCT_TYPE mKYCProductType = KYC_PRODUCT_TYPE.BASIC_KYC;
    private CountryCodePicker mCcpCountryPicker;
    private Spinner mSIdType;
    private TextView mTvInputCountry;
    private TextView mTvInputIdType;
    private EditText mEdtIdNbr;
    private EditText mEdtFirstName;
    private EditText mEdtLastName;
    private TextView mTvInputDoB;
    private boolean mWasInputIdClicked = false;
    private HashMap<String, String> mSidUserIdInfo = new HashMap<String, String>();

    private static final String SUPPORTED_COUNTRIES = "DZ,AO,BJ,BW,BF,BI,CM,CV,TD,KM,CG,CI,CD,DJ," +
        "EG,GQ,ER,ET,GA,GM,GH,GN,GW,KE,LS,LR,LY,MG,MW,ML,MU,MA,MZ,NA,NE,NG,RW,ST,SN,SC,SL,SO,ZA," +
            "SS,SD,TG,TN,UG,TZ,ZM,ZW,AL,AD,AT,BY,BE,BA,BG,HR,CZ,DK,EE,FI,FR,DE,GR,VA,HU,IS,IE," +
                "IT,XK,LV,LI,LT,LU,MT,MC,ME,NL,NO,PL,PT,MD,RO,SM,RS,SK,SI,ES,SE,CH,MK,UA,GB,BS," +
                    "BM,CA,JM,US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.sid_activity_id_info);

        Intent intent = getIntent();

        if (intent != null) {
            mKYCProductType = (KYC_PRODUCT_TYPE) getIntent().getSerializableExtra(
                BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM);

            mCurrentTag = getIntent().getStringExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);
        }

        initViews();
    }

    private void initViews() {
        mTvInputCountry = findViewById(R.id.tvInputCountry);
        TextView mTvLblIdType = findViewById(R.id.tvInputIdType);
        mTvInputIdType = findViewById(R.id.tvInputIdType);
        mEdtIdNbr = findViewById(R.id.edtIdNbr);
        mEdtFirstName = findViewById(R.id.edtFirstName);
        mEdtLastName = findViewById(R.id.edtLastName);
        mTvInputDoB = findViewById(R.id.tvInputDoB);

        mCcpCountryPicker = findViewById(R.id.ccpCountry);
        mCcpCountryPicker.setCustomMasterCountries(SUPPORTED_COUNTRIES);
        mCcpCountryPicker.setOnCountryChangeListener(() -> {
            mTvLblIdType.setVisibility(View.VISIBLE);
            mTvInputIdType.setVisibility(View.VISIBLE);
            CCPCountry country = mCcpCountryPicker.getSelectedCountry();
            DropDownObject dropDownObject = new DropDownObject(country.getFlagID(), country.getEnglishName());
            applyChoice(mTvInputCountry, dropDownObject, true);
            getSelectedCountryName();
            populateIdCard();
        });

        mSIdType = findViewById(R.id.spIdType);
        mSIdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedIdCard = ((DropDownObject) parent.getItemAtPosition(position)).getLabel();
                mTvInputIdType.setText(mSelectedIdCard);

                if (!mWasInputIdClicked) return;

                findViewById(R.id.tvLblIdNbr).setVisibility(View.VISIBLE);
                findViewById(R.id.edtIdNbr).setVisibility(View.VISIBLE);
                findViewById(R.id.tvLblFirstName).setVisibility(View.VISIBLE);
                findViewById(R.id.edtFirstName).setVisibility(View.VISIBLE);
                findViewById(R.id.tvLblLastName).setVisibility(View.VISIBLE);
                findViewById(R.id.edtLastName).setVisibility(View.VISIBLE);
                findViewById(R.id.tvLblDoB).setVisibility(View.VISIBLE);
                findViewById(R.id.tvInputDoB).setVisibility(View.VISIBLE);
                findViewById(R.id.tvContinueBtn).setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void applyChoice(TextView tvLang, DropDownObject dropDownObject, boolean isCountryFlag) {
        tvLang.setText(dropDownObject.getLabel());
        Drawable left = null;
        Drawable right = null;

        if (isCountryFlag) {
            left = getResources().getDrawable(dropDownObject.getFlagResId());
            right = getResources().getDrawable(R.drawable.ic_down_arrow);
        } else {
            tvLang.setTextSize(14);
            tvLang.setPadding(tvLang.getPaddingLeft(), 4, tvLang.getTotalPaddingRight(), 4);
        }

        tvLang.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
    }

    private String getSelectedCountryName() {
        return mSelectedCountryName = mCcpCountryPicker.getSelectedCountryName();
    }

    private void populateIdCard() {
        IdCard idCard = IdTypeUtil.idCards(mSelectedCountryName);
        initSpinner(idCard.getIdCards());
    }

    private void initSpinner(List<String> idTypes) {
        ArrayList<DropDownObject> ids = new ArrayList<DropDownObject>() {
            {
                for (String idType : idTypes) {
                    add(new DropDownObject(-1, idType));
                }
            }
        };

        DropDownAdapter dataAdapter = new DropDownAdapter(this, ids);
        dataAdapter.setListener(this::applyChoice);
        mSIdType.setAdapter(dataAdapter);
    }

    public void openCountryPicker(View view) {
        mCcpCountryPicker.launchCountrySelectionDialog();
    }

    public void openIdTypePicker(View view) {
        mWasInputIdClicked = true;
        mSIdType.performClick();
    }

    public void showDateDialog(View view) {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);

                mTvInputDoB.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(
                    selectedDate.getTime()));
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
            Calendar.DAY_OF_MONTH)).show();
    }

    public void backPressed(View view) {
        super.onBackPressed();
    }

    public void go2Next(View view) {
        if (!isIdInfoValid()) {
            Toast.makeText(this, "Kindly provide complete ID information",
                Toast.LENGTH_LONG).show();

            return;
        }

        finish();

        startActivity(
            new Intent(this, SIDJobResultActivity.class) {
                {
                    putExtra(BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM, mKYCProductType);
                    putExtra(SIDJobResultActivity.USER_ID_INFO_PARAM, mSidUserIdInfo);
                    putExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO, mCurrentTag);
                }
            }
        );
    }

    private boolean isIdInfoValid() {
        if (mTvInputCountry.getText().toString().equalsIgnoreCase(
            getString(R.string.lbl_enroll_result_country))) return false;

        mSidUserIdInfo.put(SIDUserIdInfo.COUNTRY, mSelectedCountryName);

        if (mTvInputIdType.getText().toString().equalsIgnoreCase(
            getString(R.string.lbl_enroll_result_country))) return false;

        mSidUserIdInfo.put(SIDUserIdInfo.ID_TYPE, mSelectedIdCard.replace(" ", "_"));

        if (mEdtIdNbr.getText().toString().isEmpty()) return false;
        mSidUserIdInfo.put(SIDUserIdInfo.ID_NUMBER, mEdtIdNbr.getText().toString());

        if (mEdtFirstName.getText().toString().isEmpty()) return false;
        mSidUserIdInfo.put(SIDUserIdInfo.FIRST_NAME, mEdtFirstName.getText().toString());

        if (mEdtLastName.getText().toString().isEmpty()) return false;
        mSidUserIdInfo.put(SIDUserIdInfo.LAST_NAME, mEdtLastName.getText().toString());

        if (mTvInputDoB.getText().toString().equalsIgnoreCase(
            getString(R.string.sid_info_screen_input_dob_hint))) return false;

        mSidUserIdInfo.put("dob", mTvInputDoB.getText().toString());

        return true;
    }
}
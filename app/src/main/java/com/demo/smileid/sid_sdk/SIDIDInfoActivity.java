package com.demo.smileid.sid_sdk;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.smileid.sid_sdk.ItemListAdapter.ItemSelectedInterface;
import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.hbb20.CCPCountry;
import com.smileidentity.libsmileid.model.SIDUserIdInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SIDIDInfoActivity extends AppCompatActivity implements ItemSelectedInterface {

    private String mSelectedCountryName = "", mSelectedIdCard = "", mCurrentTag;
    private static final String SUPPORTED_COUNTRIES = "GH,KE,NG,ZA,UG";
    private TextView mTvInputCountry, mTvLblIdType, mTvInputIdType, mTvInputDoB;
    private EditText mEdtIdNbr, mEdtFirstName, mEdtLastName;
    private BottomDialogHelper mCountryDialog, mIdDialog;
    private IdListAdapter mIdListAdapter = null;
    private CountryListAdapter mAdapter = null;
    private HashMap<String, String> mSidUserIdInfo = new HashMap();
    private Bundle mParams = null;
    private boolean showExtraFields = false;
    private BaseSIDActivity.KYC_PRODUCT_TYPE mKYCProductType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_id_info);

        mParams = getIntent().getExtras();

        if (mParams != null) {
            mCurrentTag =
                    mParams.getString(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO);
            mKYCProductType =
                    (BaseSIDActivity.KYC_PRODUCT_TYPE) mParams.getSerializable(BaseSIDActivity.KYC_PRODUCT_TYPE_PARAM);
        }

        mAdapter = new CountryListAdapter(SUPPORTED_COUNTRIES);

        initViews();
    }

    private void initViews() {
        mTvInputCountry = findViewById(R.id.tvInputCountry);
        mTvLblIdType = findViewById(R.id.tvLblIdType);
        mTvInputIdType = findViewById(R.id.tvInputIdType);
        mEdtIdNbr = findViewById(R.id.edtIdNbr);
        mEdtFirstName = findViewById(R.id.edtFirstName);
        mEdtLastName = findViewById(R.id.edtLastName);
        mTvInputDoB = findViewById(R.id.tvInputDoB);

        mCountryDialog = new BottomDialogHelper(this,
                R.layout.layout_country_list);
        setCountryDialog();

        mIdDialog = new BottomDialogHelper(this, R.layout.layout_id_list);
        setIdDialog();
    }

    private void setCountryDialog() {
        RecyclerView rv =
                mCountryDialog.getContentView().findViewById(R.id.rvCountries);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setListener(this);
        rv.setAdapter(mAdapter);
        filterCountryList("");

        TextWatcher textWatcher =
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        filterCountryList(s.toString());
                    }
                };

        EditText edtSearch =
                mCountryDialog.getContentView().findViewById(R.id.edtCountry);
        edtSearch.addTextChangedListener(textWatcher);
        mCountryDialog.setDismissListener(dialog -> edtSearch.setText(""));

        mCountryDialog
                .getContentView()
                .findViewById(R.id.ivBtnCancel)
                .setOnClickListener(v -> mCountryDialog.dismissDialog());
    }

    private void filterCountryList(String constraint) {
        mAdapter.filterList(constraint);
    }

    private void setIdDialog() {
        RecyclerView rv = mIdDialog.getContentView().findViewById(R.id.rvIds);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mIdListAdapter = new IdListAdapter();
        mIdListAdapter.setListener(this);
        rv.setAdapter(mIdListAdapter);

        mIdDialog
                .getContentView()
                .findViewById(R.id.ivBtnCancel)
                .setOnClickListener(v -> mIdDialog.dismissDialog());
    }

    public void buildItem(TextView tvLang, Object object,
                          boolean isCountryFlag) {
        int flagId = -1;
        String name = "";
        Drawable left = null;

        if (object instanceof CCPCountry) {
            CCPCountry country = (CCPCountry) object;
            name = country.getName();
            flagId = country.getFlagID();

            if ((isCountryFlag) && (flagId != (-1))) {
                left = getResources().getDrawable(country.getFlagID());
            }
        } else {
            name = object.toString();
        }

        tvLang.setText(name);
        tvLang.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
    }

    public void applyChoice(Object object) {
        if (object instanceof CCPCountry) {
            mCountryDialog.dismissDialog();

            mSelectedCountryName = ((CCPCountry) object).getName();
            mSelectedIdCard = "";
            mIdListAdapter.setIdList(IdTypeUtil.idCards(mSelectedCountryName).getIdCards());
            mIdListAdapter.notifyDataSetChanged();
            mTvInputCountry.setText(mSelectedCountryName);
            mTvInputCountry.setTextColor(
                    Color.parseColor(mSelectedCountryName.isEmpty() ?
                            "#9AA6AC" : "#252C32"));
            mTvInputIdType.setText("");
            mTvInputIdType.setTextColor(Color.parseColor("#9AA6AC"));
            mTvLblIdType.setVisibility(View.VISIBLE);
            mTvInputIdType.setVisibility(View.VISIBLE);
        } else {
            mIdDialog.dismissDialog();

            mSelectedIdCard = object.toString();
            mTvInputIdType.setText(mSelectedIdCard);
            mTvInputIdType.setTextColor(
                    Color.parseColor(mSelectedIdCard.isEmpty() ? "#9AA6AC" :
                            "#252C32"));
            findViewById(R.id.tvLblIdNbr).setVisibility(View.VISIBLE);
            findViewById(R.id.edtIdNbr).setVisibility(View.VISIBLE);
            showExtraFields =
                    mKYCProductType == BaseSIDActivity.KYC_PRODUCT_TYPE.BASIC_KYC ||
                            mSelectedCountryName.toLowerCase(Locale.ROOT).equals("nigeria")
                                    && mSelectedIdCard.toLowerCase(Locale.ROOT).equals("" + "drivers license");
            findViewById(R.id.tvLblFirstName).setVisibility(showExtraFields ?
                    View.VISIBLE : View.GONE);
            findViewById(R.id.edtFirstName).setVisibility(showExtraFields ?
                    View.VISIBLE : View.GONE);
            findViewById(R.id.tvLblLastName).setVisibility(showExtraFields ?
                    View.VISIBLE : View.GONE);
            findViewById(R.id.edtLastName).setVisibility(showExtraFields ?
                    View.VISIBLE : View.GONE);
            findViewById(R.id.tvLblDoB).setVisibility(showExtraFields ?
                    View.VISIBLE : View.GONE);
            findViewById(R.id.tvInputDoB).setVisibility(showExtraFields ?
                    View.VISIBLE : View.GONE);
            findViewById(R.id.tvContinueBtn).setVisibility(View.VISIBLE);
        }
    }

    public void openCountryPicker(View view) {
        mCountryDialog.showDialog();
    }

    public void openIdTypePicker(View view) {
        mIdDialog.showDialog();
    }

    public void showDateDialog(View view) {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(
                this,
                AlertDialog.THEME_HOLO_LIGHT,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, monthOfYear, dayOfMonth);

                    mTvInputDoB.setText(
                            new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void backPressed(View view) {
        super.onBackPressed();
    }

    public void go2Next(View view) {
        if (!isIdInfoValid()) {
            Toast.makeText(
                            this,
                            getString(R.string.sid_info_screen_incomplete_info_error), Toast.LENGTH_LONG)
                    .show();

            return;
        }

        finish();

        if ((mCurrentTag == null) || (mCurrentTag.isEmpty())) {
            buildTag();
        }

        if ((mParams != null) && (!mParams.containsKey(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO))) {
            mParams.putString(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO,
                    mCurrentTag);
        }

        mParams.putSerializable(SIDJobResultActivity.USER_ID_INFO_PARAM,
                mSidUserIdInfo);

        if (mKYCProductType == BaseSIDActivity.KYC_PRODUCT_TYPE.BIOMETRIC_KYC) {
            // For Biometric KYC, we ask for ID info before Selfie
            startActivity(new Intent(this, SIDSelfieActivity.class).putExtras(mParams));
        } else {
            startActivity(new Intent(this, SIDJobResultActivity.class) {{
                putExtras(mParams);
            }});
        }

    }

    private boolean isIdInfoValid() {
        if (mTvInputCountry
                .getText()
                .toString()
                .equalsIgnoreCase(getString(R.string.lbl_enroll_result_country)))
            return false;

        mSidUserIdInfo.put(SIDUserIdInfo.COUNTRY, mSelectedCountryName);

        if (mTvInputIdType
                .getText()
                .toString()
                .equalsIgnoreCase(getString(R.string.lbl_enroll_result_country)))
            return false;

        mSidUserIdInfo.put(SIDUserIdInfo.ID_TYPE, mSelectedIdCard.replace(" "
                , "_"));

        if (mEdtIdNbr.getText().toString().isEmpty()) return false;

        if (mEdtIdNbr
                .getText()
                .toString()
                .equalsIgnoreCase(getString(R.string.sid_info_screen_input_id_type_lbl)))
            return false;

        mSidUserIdInfo.put(SIDUserIdInfo.ID_NUMBER, mEdtIdNbr.getText().toString());

        if (showExtraFields) {
            if (mEdtFirstName.getText().toString().isEmpty()) return false;
            mSidUserIdInfo.put(SIDUserIdInfo.FIRST_NAME, mEdtFirstName.getText().toString());

            if (mEdtLastName.getText().toString().isEmpty()) return false;
            mSidUserIdInfo.put(SIDUserIdInfo.LAST_NAME, mEdtLastName.getText().toString());

            if (mTvInputDoB
                    .getText()
                    .toString()
                    .equalsIgnoreCase(getString(R.string.sid_info_screen_input_dob_hint)))
                return false;

            mSidUserIdInfo.put(SIDUserIdInfo.DOB, mTvInputDoB.getText().toString());
        }

        return true;
    }

    private void buildTag() {
        mCurrentTag =
                String.format(
                        Misc.USER_TAG,
                        DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}

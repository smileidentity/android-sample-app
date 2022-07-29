package com.demo.smileid.sid_sdk;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.smileid.sid_sdk.BaseSIDActivity.KYC_PRODUCT_TYPE;
import com.demo.smileid.sid_sdk.ItemListAdapter.ItemSelectedInterface;
import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.demo.smileid.sid_sdk.sidNet.Misc;
import com.hbb20.CCPCountry;
import com.smileidentity.libsmileid.model.SIDUserIdInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SIDIDInfoActivity extends AppCompatActivity implements ItemSelectedInterface {

    private String mSelectedCountryName = "", mSelectedIdCard, mCurrentTag;
    private KYC_PRODUCT_TYPE mKYCProductType = KYC_PRODUCT_TYPE.ENROLL_TEST;
    private TextView mTvInputCountry, mTvLblIdType, mTvInputIdType, mTvInputDoB;
    private EditText mEdtIdNbr, mEdtFirstName, mEdtLastName;
    private BottomDialogHelper mCountryDialog, mIdDialog;
    private IdListAdapter mIdListAdapter = null;
    private HashMap<String, String> mSidUserIdInfo = new HashMap();

    private static final String SUPPORTED_COUNTRIES = "DZ,AO,BJ,BW,BF,BI,CM,CV,TD,KM,CG,CI,CD,DJ," +
        "EG,GQ,ER,ET,GA,GM,GH,GN,GW,KE,LS,LR,LY,MG,MW,ML,MU,MA,MZ,NA,NE,NG,RW,ST,SN,SC,SL,SO,ZA," +
            "SS,SD,TG,TN,UG,TZ,ZM,ZW,AL,AD,AT,BY,BE,BA,BG,HR,CZ,DK,EE,FI,FR,DE,GR,VA,HU,IS,IE," +
                "IT,XK,LV,LI,LT,LU,MT,MC,ME,NL,NO,PL,PT,MD,RO,SM,RS,SK,SI,ES,SE,CH,MK,UA,GB,BS," +
                    "BM,CA,JM,US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mTvLblIdType = findViewById(R.id.tvLblIdType);
        mTvInputIdType = findViewById(R.id.tvInputIdType);
        mEdtIdNbr = findViewById(R.id.edtIdNbr);
        mEdtFirstName = findViewById(R.id.edtFirstName);
        mEdtLastName = findViewById(R.id.edtLastName);
        mTvInputDoB = findViewById(R.id.tvInputDoB);

        mCountryDialog = new BottomDialogHelper(this, R.layout.layout_country_list);
        setCountryDialog();

        mIdDialog = new BottomDialogHelper(this, R.layout.layout_id_list);
        setIdDialog();
    }

    private void setCountryDialog() {
        mCountryDialog.setCancellable(false);
        RecyclerView rv = mCountryDialog.getContentView().findViewById(R.id.rvCountries);
        rv.setLayoutManager(new LinearLayoutManager(this));
        CountryListAdapter adapter = new CountryListAdapter();
        adapter.setListener(this);
        rv.setAdapter(adapter);
    }

    private void setIdDialog() {
        mIdDialog.setCancellable(false);
        RecyclerView rv = mIdDialog.getContentView().findViewById(R.id.rvIds);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mIdListAdapter = new IdListAdapter();
        mIdListAdapter.setListener(this);
        rv.setAdapter(mIdListAdapter);
    }

    public void buildItem(TextView tvLang, Object object, boolean isCountryFlag) {
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
            mCountryDialog.setCancellable(true);
            mCountryDialog.dismissDialog();
            mIdDialog.setCancellable(false);

            mSelectedCountryName = ((CCPCountry) object).getName();
            mIdListAdapter.setIdList(IdTypeUtil.idCards(mSelectedCountryName).getIdCards());
            mIdListAdapter.notifyDataSetChanged();
            mTvInputCountry.setText(mSelectedCountryName);
            mTvLblIdType.setVisibility(View.VISIBLE);
            mTvInputIdType.setVisibility(View.VISIBLE);
        } else {
            mIdDialog.setCancellable(true);
            mIdDialog.dismissDialog();

            mSelectedIdCard = object.toString();
            mTvInputIdType.setText(mSelectedIdCard);

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
    }

    public void openCountryPicker(View view) {
        mCountryDialog.showDialog();
    }

    public void openIdTypePicker(View view) {
        mIdDialog.showDialog();
    }

    public void showDateDialog(View view) {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, monthOfYear, dayOfMonth);

            mTvInputDoB.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(
                selectedDate.getTime()));
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

        if ((mCurrentTag == null) || (mCurrentTag.isEmpty())) {
            buildTag();
        }

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

        mSidUserIdInfo.put(SIDUserIdInfo.DOB, mTvInputDoB.getText().toString());

        return true;
    }

    class CountryListAdapter extends ItemListAdapter {

        private ArrayList<CCPCountry> mCountries = new ArrayList<>(CCPCountry.getLibraryMasterCountriesEnglish());

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
            viewHolder.populate(mCountries.get(i));
            viewHolder.itemView.setOnClickListener(v -> mListener.applyChoice(mCountries.get(i)));
        }

        @Override
        public int getItemCount() {
            return mCountries.size();
        }
    }

    class IdListAdapter extends ItemListAdapter {

        private ArrayList<String> mIds = new ArrayList<>();

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
            viewHolder.populate(mIds.get(i));
            viewHolder.itemView.setOnClickListener(v -> mListener.applyChoice(mIds.get(i)));
        }

        @Override
        public int getItemCount() {
            return mIds.size();
        }

        public void setIdList(List<String> ids) {
            mIds.addAll(ids);
        }
    }

    private void buildTag() {
        mCurrentTag = String.format(
            Misc.USER_TAG, DateFormat.format("MM_dd_hh_mm_ss", Calendar.getInstance().getTime()).toString());
    }
}
package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;
import com.demo.smileid.sid_sdk.DropDownAdapter.DropDownObject;
import java.util.ArrayList;
import java.util.List;

public class CCAndIdTypeDialog {

    private Dialog mDialog;
    private CountryCodePicker mCcpCountryPicker;
    private Spinner mSIdType;
    private TextView mTvLblCountry;
    private TextView mTvInputCountry;
    private TextView mTvLblIdType;
    private TextView mTvInputIdType;
    private TextView mTvSubmit;
    private DlgListener mListener;
    private String mSelectedCountryName = "", mSelectedIdType;

    private static final String SUPPORTED_COUNTRIES = "DZ,AO,BJ,BW,BF,BI,CM,CV,TD,KM,CG,CI,CD,DJ," +
        "EG,GQ,ER,ET,GA,GM,GH,GN,GW,KE,LS,LR,LY,MG,MW,ML,MU,MA,MZ,NA,NE,NG,RW,ST,SN,SC,SL,SO,ZA," +
            "SS,SD,TG,TN,UG,TZ,ZM,ZW,AL,AD,AT,BY,BE,BA,BG,HR,CZ,DK,EE,FI,FR,DE,GR,VA,HU,IS,IE," +
                "IT,XK,LV,LI,LT,LU,MT,MC,ME,NL,NO,PL,PT,MD,RO,SM,RS,SK,SI,ES,SE,CH,MK,UA,GB,BS," +
                    "BM,CA,JM,US";

    public interface DlgListener {
        void submit(String countryCode, String idType);
        void cancel();
    }

    private void buildIdTypeDialog(Context context, View root) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(root);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);

        mDialog.findViewById(R.id.tvSubmit).setOnClickListener(v -> {
            mDialog.cancel();
            if (mListener == null) return;
            mListener.cancel();
        });
    }

    private void setupViews() {
        mTvLblCountry = mDialog.findViewById(R.id.tvLblCountry);

        mTvInputCountry = mDialog.findViewById(R.id.tvInputCountry);
        mTvInputCountry.setOnClickListener(v -> mCcpCountryPicker.launchCountrySelectionDialog());

        mCcpCountryPicker = mDialog.findViewById(R.id.ccpCountry);
        mCcpCountryPicker.setCustomMasterCountries(SUPPORTED_COUNTRIES);
        mCcpCountryPicker.setOnCountryChangeListener(() -> {
            mTvLblIdType.setVisibility(View.VISIBLE);
            mTvInputIdType.setVisibility(View.VISIBLE);
            CCPCountry country = mCcpCountryPicker.getSelectedCountry();
            DropDownObject dropDownObject = new DropDownObject(country.getFlagID(), country.getEnglishName());
            applyChoice(mTvInputCountry, dropDownObject, true);
            getSelectedCountryName();
            initSpinner(IdTypeUtil.idCards(mSelectedCountryName).getIdCards());
        });

        mTvLblIdType = mDialog.findViewById(R.id.tvLblIdType);

        mTvInputIdType = mDialog.findViewById(R.id.tvInputIdType);
        mTvInputIdType.setOnClickListener(v -> mSIdType.performClick());

        mSIdType = mDialog.findViewById(R.id.spIdType);
        mSIdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTvSubmit.setVisibility(View.VISIBLE);
                DropDownObject idType = (DropDownObject) parent.getItemAtPosition(position);
                mSelectedIdType = idType.getLabel();
                DropDownObject dropDownObject = new DropDownObject(-1, idType.getLabel());
                applyChoice(mTvInputIdType, dropDownObject, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTvSubmit = mDialog.findViewById(R.id.tvSubmit);
        mTvSubmit.setOnClickListener(v -> {
            mDialog.cancel();
            if (mListener == null) return;
            mListener.submit(mSelectedCountryName, mSelectedIdType);
        });
    }

    private void applyChoice(TextView tvLang, DropDownAdapter.DropDownObject dropDownObject, boolean isCountryFlag) {
        tvLang.setText(dropDownObject.getLabel());
        Drawable left = null;
        Drawable right = null;

        if (isCountryFlag) {
            left = mDialog.getContext().getResources().getDrawable(dropDownObject.getFlagResId());
            right = mDialog.getContext().getResources().getDrawable(R.drawable.ic_down_arrow);
        } else {
            tvLang.setTextSize(14);
            tvLang.setPadding(tvLang.getPaddingLeft(), 4, tvLang.getTotalPaddingRight(), 4);
        }

        tvLang.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
    }

    public CCAndIdTypeDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_cc_id_type_dlg, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        buildIdTypeDialog(context, root);

        mListener = listener;

        setupViews();
    }

    private String getSelectedCountryName() {
        return mSelectedCountryName = mCcpCountryPicker.getSelectedCountryName();
    }

    private void initSpinner(List<String> idTypes) {
        ArrayList<DropDownObject> ids = new ArrayList<DropDownObject>() {
            {
                for (String idType : idTypes) {
                    add(new DropDownObject(-1, idType));
                }
            }
        };

        DropDownAdapter dataAdapter = new DropDownAdapter(mDialog.getContext(), ids);
        dataAdapter.setListener(this::applyChoice);
        mSIdType.setAdapter(dataAdapter);
    }

    public void showDialog() {
        mDialog.show();
    }
}
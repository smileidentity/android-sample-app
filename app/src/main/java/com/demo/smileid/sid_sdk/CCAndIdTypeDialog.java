package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.hbb20.CountryCodePicker;
import com.smileidentity.libsmileid.core.idcard.IdCard;

import java.util.List;

public class CCAndIdTypeDialog {

    private Dialog mDialog;
    private CountryCodePicker mCcpCountryPicker;
    private Spinner mSIdType;
    private DlgListener mListener;
    private String mSelectedCountryName = "", mSelectedCountryCode = "", mSelectedIdType;

    public interface DlgListener {
        void submit(String countryCode, String idType);
        void cancel();
    }

    public CCAndIdTypeDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_cc_id_type_dlg, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(root);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);

        mDialog.findViewById(R.id.tvSubmit).setOnClickListener(v -> {
             if (mListener == null) return;
             mDialog.cancel();
             mListener.submit(mSelectedCountryCode, mSelectedIdType);
        });

        mDialog.findViewById(R.id.ivBtnCancel).setOnClickListener(v -> {
            mDialog.cancel();
            mListener.cancel();
        });

        mListener = listener;

        mCcpCountryPicker = mDialog.findViewById(R.id.ccpCountry);
        mCcpCountryPicker.setOnCountryChangeListener(() -> {
            getSelectedCountryName();
            populateIdCard();
        });

        mSIdType = mDialog.findViewById(R.id.spIdType);
        mSIdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedIdType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getSelectedCountryName();
        populateIdCard();
    }

    private String getSelectedCountryName() {
        mSelectedCountryCode = mCcpCountryPicker.getSelectedCountryNameCode();
        return mSelectedCountryName = mCcpCountryPicker.getSelectedCountryName();
    }

    private void populateIdCard() {
        IdCard idCard = IdTypeUtil.idCards(mSelectedCountryName);
        initSpinner(idCard.getIdCards());
    }

    private void initSpinner(List<String> idTypes) {
        ArrayAdapter dataAdapter = new ArrayAdapter<>(mDialog.getContext(), android.R.layout.simple_spinner_item, idTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSIdType.setAdapter(dataAdapter);
    }

    public void showDialog() {
        mDialog.show();
    }
}
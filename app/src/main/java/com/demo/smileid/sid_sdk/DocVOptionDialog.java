package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.demo.smileid.sid_sdk.DropDownAdapter.DropDownObject;
import java.util.ArrayList;

public class DocVOptionDialog {

    private Dialog mDialog;
    private DlgListener mListener;
    private TextView mTvCaptureType;
    private Spinner mSCaptureType;
    private TextView mTvUserTypes;
    private RadioGroup mRgUserTypes;
    private TextView mTvSubmit;

    public interface DlgListener {
        void verificationSelected(DOC_VER_TYPE type, DOC_VER_OPTION option);
    }

    public enum DOC_VER_TYPE {
        SELFIE_PLUS_ID_CARD,
        ID_CARD_ONLY
    }

    public enum DOC_VER_OPTION {
        ENROLLED_USER,
        NON_ENROLLED_USER
    }

    private DOC_VER_TYPE mType = DOC_VER_TYPE.ID_CARD_ONLY;
    private DOC_VER_OPTION mOption = DOC_VER_OPTION.ENROLLED_USER;

    private void buildDocVOptionDialog(Context context, View root) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(root);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);
    }

    private void setupViews() {
        mTvCaptureType = mDialog.findViewById(R.id.tvCaptureType);

        mTvCaptureType.setOnClickListener(v -> {
            mSCaptureType.performClick();
        });

        mSCaptureType = mDialog.findViewById(R.id.spCaptureType);

        mSCaptureType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mType = (position != 0) ? DOC_VER_TYPE.SELFIE_PLUS_ID_CARD : DOC_VER_TYPE.ID_CARD_ONLY;
                mTvUserTypes.setVisibility((mType == DOC_VER_TYPE.ID_CARD_ONLY) ? View.GONE : View.VISIBLE);
                mRgUserTypes.setVisibility((mType == DOC_VER_TYPE.ID_CARD_ONLY) ? View.GONE : View.VISIBLE);
                mTvSubmit.setVisibility(View.VISIBLE);

                DropDownObject captureType = (DropDownObject) parent.getItemAtPosition(position);
                DropDownObject dropDownObject = new DropDownObject(-1, captureType.getLabel());
                applyChoice(mTvCaptureType, dropDownObject, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTvUserTypes = mDialog.findViewById(R.id.tvUserTypes);

        mRgUserTypes = mDialog.findViewById(R.id.rgUserTypes);

        mRgUserTypes.setOnCheckedChangeListener((group, checkedId) -> {
            mOption = (checkedId == R.id.rbEnrolled) ? DOC_VER_OPTION.ENROLLED_USER :
                DOC_VER_OPTION.NON_ENROLLED_USER;
        });

        mTvSubmit = mDialog.findViewById(R.id.tvSubmit);

        mTvSubmit.setOnClickListener(v -> {
            mDialog.cancel();
            if (mListener == null) return;
            mListener.verificationSelected(mType, mOption);
        });

        mDialog.findViewById(R.id.ivBtnCancel).setOnClickListener(v -> {
            mDialog.cancel();
        });
    }

    private void applyChoice(TextView tvCaptureType, DropDownAdapter.DropDownObject dropDownObject, boolean isInputBox) {
        Drawable right = null;

        if (isInputBox) {
            right = mDialog.getContext().getResources().getDrawable(R.drawable.ic_down_arrow);
            tvCaptureType.setTextColor(Color.parseColor("#191D31"));
        }

        tvCaptureType.setText(dropDownObject.getLabel());
        tvCaptureType.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
    }

    public DocVOptionDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_doc_verify_option_dlg, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        buildDocVOptionDialog(context, root);

        mListener = listener;

        setupViews();
        initSpinner();
    }

    private void initSpinner() {
        ArrayList<DropDownObject> objects = new ArrayList<DropDownObject>() {
            {
                add(new DropDownObject(-1, "ID Card only"));
                add(new DropDownObject(-1, "Selfie and ID Card"));
            }
        };

        DropDownAdapter dataAdapter = new DropDownAdapter(mDialog.getContext(), objects);
        dataAdapter.setListener(this::applyChoice);
        mSCaptureType.setAdapter(dataAdapter);
    }

    public void showDialog() {
        mDialog.show();
    }
}

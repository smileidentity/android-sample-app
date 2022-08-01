package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DocVOptionDialog {

    private Dialog mDialog;
    private DlgListener mListener;
    private TextView mTvUserTypes;
    private RadioGroup mRgTypes, mRgUserTypes;
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
        mRgTypes = mDialog.findViewById(R.id.rgTypes);

        mRgTypes.setOnCheckedChangeListener((group, checkedId) -> {
            mType = (checkedId == R.id.rbSelfieAndCard) ? DOC_VER_TYPE.SELFIE_PLUS_ID_CARD : DOC_VER_TYPE.ID_CARD_ONLY;
            mTvUserTypes.setVisibility((mType == DOC_VER_TYPE.ID_CARD_ONLY) ? View.GONE : View.VISIBLE);
            mRgUserTypes.setVisibility((mType == DOC_VER_TYPE.ID_CARD_ONLY) ? View.GONE : View.VISIBLE);
            mTvSubmit.setVisibility(View.VISIBLE);
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
    }

    public DocVOptionDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_doc_verify_option_dlg, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        buildDocVOptionDialog(context, root);

        mListener = listener;

        setupViews();
    }

    public void showDialog() {
        mDialog.show();
    }
}

package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

public class DocVerifyOptionDialog {

    private Dialog mDialog;
    private DlgListener mListener;

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

    public DocVerifyOptionDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_doc_verify_option_dlg, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(root);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);

        ((RadioGroup) mDialog.findViewById(R.id.rgTypes)).setOnCheckedChangeListener((group, checkedId) -> {
            mDialog.findViewById(R.id.tvOptions).setVisibility((checkedId != R.id.rbIdCardOnly) ?
                View.VISIBLE : View.GONE);

            mDialog.findViewById(R.id.rgOptions).setVisibility((checkedId != R.id.rbIdCardOnly) ?
                View.VISIBLE : View.GONE);

            mType = (checkedId != R.id.rbIdCardOnly) ? DOC_VER_TYPE.SELFIE_PLUS_ID_CARD :
                DOC_VER_TYPE.ID_CARD_ONLY;

            mOption = DOC_VER_OPTION.ENROLLED_USER;
        });

        ((RadioGroup) mDialog.findViewById(R.id.rgOptions)).setOnCheckedChangeListener((group, checkedId) -> {
            mOption = (checkedId == R.id.rbEnrolled) ? DOC_VER_OPTION.ENROLLED_USER :
                DOC_VER_OPTION.NON_ENROLLED_USER;
        });

        mDialog.findViewById(R.id.tvSubmit).setOnClickListener(v -> {
             if (mListener == null) return;
             mDialog.cancel();
             mListener.verificationSelected(mType, mOption);
        });

        mDialog.findViewById(R.id.ivBtnCancel).setOnClickListener(v -> {
            mDialog.cancel();
        });

        mListener = listener;
    }

    public void showDialog() {
        mDialog.show();
    }
}
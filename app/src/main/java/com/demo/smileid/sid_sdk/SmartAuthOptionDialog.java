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

public class SmartAuthOptionDialog {

    private Dialog mDialog;
    private DlgListener mListener;
    private TextView mTvUserTypes;
    private RadioGroup mRgTypes;
    private TextView mTvSubmit;

    public interface DlgListener {
        void authTypeSelected(AUTH_TYPE type);
    }

    public enum AUTH_TYPE {
        ENROLL,
        AUTH
    }


    private AUTH_TYPE mType = AUTH_TYPE.ENROLL;

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
            mType = (checkedId == R.id.rbNewEnroll) ? AUTH_TYPE.ENROLL : AUTH_TYPE.AUTH;
            mTvSubmit.setVisibility(View.VISIBLE);
        });

        mTvSubmit = mDialog.findViewById(R.id.tvSubmit);

        mTvSubmit.setOnClickListener(v -> {
            mDialog.cancel();
            if (mListener == null) return;
            mListener.authTypeSelected(mType);
        });
    }

    public SmartAuthOptionDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_smart_auth_option_dlg,
                null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        buildDocVOptionDialog(context, root);

        mListener = listener;

        setupViews();
    }

    public void showDialog() {
        mDialog.show();
    }
}

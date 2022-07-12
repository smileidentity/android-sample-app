package com.demo.smileid.sid_sdk;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

public class ActionDialog {

    private Dialog mDialog;
    private DlgListener mDlgListener;

    public ActionDialog(Activity activity, DlgListener dlgListener) {
        mDialog = new Dialog(activity);
        mDlgListener = dlgListener;
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.id_card_back_layout);
        mDialog.setCancelable(false);

        mDialog.findViewById(R.id.tvSkip).setOnClickListener(v -> {
            mDialog.cancel();

            if (mDlgListener != null) {
                mDlgListener.skip();
            }
        });

        mDialog.findViewById(R.id.tvProceed).setOnClickListener(v -> {
            mDialog.cancel();

            if (mDlgListener != null) {
                mDlgListener.capture();
            }
        });
    }

    public void showDialog() {
        mDialog.show();
    }

    public interface DlgListener {

        void skip();
        void capture();
    }
}
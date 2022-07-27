package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

public class ActionDialog {

    private Dialog mDialog;
    private TextView mTvSkip, mTvProceed;
    private DlgListener mDlgListener;

    public ActionDialog(Context context, DlgListener dlgListener, String headingTxt,
        String contentTxt, String positiveBtnTxt, String negativeBtnTxt) {

        mDlgListener = dlgListener;

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_action_dlg);
        mDialog.setCancelable(false);

        mTvSkip = mDialog.findViewById(R.id.tvSkip);
        mTvProceed = mDialog.findViewById(R.id.tvProceed);

        if ((headingTxt != null) && (!headingTxt.isEmpty())) {
            ((TextView) mDialog.findViewById(R.id.tvHeading)).setText(headingTxt);
        }

        if ((contentTxt != null) && (!contentTxt.isEmpty())) {
            ((TextView) mDialog.findViewById(R.id.tvMsg)).setText(contentTxt);
        }

        if ((negativeBtnTxt != null) && (!negativeBtnTxt.isEmpty())) {
            mTvSkip.setText(negativeBtnTxt);
        }

        mTvSkip.setOnClickListener(v -> {
            mDialog.cancel();

            if (mDlgListener != null) {
                mDlgListener.cancel();
            }
        });

        if ((positiveBtnTxt != null) && (!positiveBtnTxt.isEmpty())) {
            mTvProceed.setText(positiveBtnTxt);
        }

        mTvProceed.setOnClickListener(v -> {
            mDialog.cancel();

            if (mDlgListener != null) {
                mDlgListener.proceed();
            }
        });
    }

    public void showDialog() {
        mDialog.show();
    }

    public interface DlgListener {

        void cancel();
        void proceed();
    }

    public static class Builder {

        private Context mContext;
        private DlgListener mDlgListener;
        private String mHeadingTxt, mContentTxt, mPositiveBtnTxt, mNegativeBtnTxt;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setDlgListener(DlgListener dlgListener) {
            mDlgListener = dlgListener;
            return this;
        }

        public Builder setHeadingTxt(String headingTxt) {
            mHeadingTxt = headingTxt;
            return this;
        }

        public Builder setContentTxt(String contentTxt) {
            mContentTxt = contentTxt;
            return this;
        }

        public Builder setPositiveBtnTxt(String positiveBtnTxt) {
            mPositiveBtnTxt = positiveBtnTxt;
            return this;
        }

        public Builder setNegativeBtnTxt(String negativeBtnTxt) {
            mNegativeBtnTxt = negativeBtnTxt;
            return this;
        }

        public ActionDialog build() {
            return new ActionDialog(mContext, mDlgListener, mHeadingTxt, mContentTxt,
                mPositiveBtnTxt, mNegativeBtnTxt);
        }
    }
}
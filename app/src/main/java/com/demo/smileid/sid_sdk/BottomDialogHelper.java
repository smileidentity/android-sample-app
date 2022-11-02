package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomDialogHelper {

  private BottomSheetDialog mBottomSheetDialog = null;
  private View mContentView = null;

  public BottomDialogHelper(Context context, int contentResID) {
    mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
    mBottomSheetDialog.setDismissWithAnimation(true);
    mBottomSheetDialog.setCanceledOnTouchOutside(true);
    mBottomSheetDialog.setCancelable(true);
    mContentView = mBottomSheetDialog.getLayoutInflater().inflate(contentResID, null);
    mBottomSheetDialog.setContentView(mContentView);
  }

  public void setDismissListener(OnDismissListener onDismissListener) {
    if (mBottomSheetDialog == null) return;
    mBottomSheetDialog.setOnDismissListener(onDismissListener);
  }

  public View getContentView() {
    return mContentView;
  }

  public void showDialog() {
    if (mBottomSheetDialog == null) return;
    mBottomSheetDialog.show();
  }

  public void dismissDialog() {
    if (mBottomSheetDialog == null) return;
    mBottomSheetDialog.dismiss();
  }
}

package com.demo.smileid.sid_sdk;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.ArrayList;
import com.demo.smileid.sid_sdk.DropDownAdapter.DropDownObject;
import com.smileidentity.libsmileid.model.SIDNetData.Environment;
import com.smileidentity.libsmileid.utils.AppData;

public class HomeFragment extends BaseFragment {

    private static final String SANDBOX = "Sandbox";
    private static final String PROD = "Production";
    private static final String SMILE_VIDEO = "https://youtu.be/g1vHLH4gWyo";

    private static final ArrayList<DropDownObject> environments = new ArrayList<DropDownObject>() {
        {
            add(new DropDownObject(R.drawable.online_orange_dot, SANDBOX));
            add(new DropDownObject(R.drawable.online_green_dot, PROD));
        }
    };

    private BottomDialogHelper mBottomDialogHelper = null;
    private TextView mTvEnv = null;

    @Override
    int getLayout() {
        return R.layout.sid_fragment_home;
    }

    @Override
    void setupViews() {
        mTvEnv = getView().findViewById(R.id.tvEnv);
        mTvEnv.setOnClickListener(v -> mBottomDialogHelper.showDialog());

        mBottomDialogHelper = new BottomDialogHelper(getContext(), R.layout.layout_env);

        View view = mBottomDialogHelper.getContentView();
        final RadioButton mRbSandbox = view.findViewById(R.id.rbSandbox);
        final RadioButton mRbProd = view.findViewById(R.id.rbProd);

        OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (!isChecked) return;
            switchEnv(environments.get((mRbSandbox == buttonView) ? 0 : 1));
            RadioButton rb = (mRbSandbox == buttonView) ? mRbProd : mRbSandbox;
            rb.setChecked(false);
            mBottomDialogHelper.dismissDialog();
        };

        mRbSandbox.setOnCheckedChangeListener(listener);
        mRbProd.setOnCheckedChangeListener(listener);

        switchEnv(environments.get(0)); //Setting the default environment

        if (mActionListener == null) return;
        getView().findViewById(R.id.tvResBtn).setOnClickListener(v -> mActionListener.move2Tab(1));
        getView().findViewById(R.id.cpbBasicKYC).setOnClickListener(v -> mActionListener.doBasicKYC());
        getView().findViewById(R.id.cpbEnhancedKYC).setOnClickListener(v -> mActionListener.doEnhancedKYC());
        getView().findViewById(R.id.cpbBiometricKYC).setOnClickListener(v -> mActionListener.doBiometricKYC());
        getView().findViewById(R.id.cpbDocV).setOnClickListener(v -> mActionListener.performDocV());
        getView().findViewById(R.id.cpbSmartSelfie).setOnClickListener(v -> mActionListener.doSmartSelfieAuth());
//        getView().findViewById(R.id.cpbTestSelfie).setOnClickListener(v -> mActionListener.testEnroll());
        getView().findViewById(R.id.tvWatchBtn).setOnClickListener(v -> linkClicked(SMILE_VIDEO));
    }

    public void switchEnv(DropDownObject env) {
        boolean isSandBox = env.getLabel().equalsIgnoreCase(SANDBOX);

        AppData.getInstance(getContext()).setSDKEnvir(isSandBox ? Environment.TEST : Environment.PROD);

        mTvEnv.setText(env.getLabel());
        Drawable left = getResources().getDrawable(env.getFlagResId());
        Drawable right = getResources().getDrawable(R.drawable.ic_down_arrow);
        mTvEnv.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
        mTvEnv.setTextColor(Color.parseColor(isSandBox ? "#F2994A" : "#1DA469"));
    }
}
package com.demo.smileid.sid_sdk;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import com.demo.smileid.sid_sdk.DropDownAdapter.DropDownObject;
import com.smileidentity.libsmileid.model.SIDNetData.Environment;
import com.smileidentity.libsmileid.utils.AppData;

public class HomeFragment extends BaseFragment {

    private static final String SANDBOX = "Sandbox";
    private static final String LIVE = "Live";

    @Override
    int getLayout() {
        return R.layout.sid_fragment_home;
    }

    @Override
    void setupViews() {
        ArrayList<DropDownObject> languages = new ArrayList<DropDownObject>() {
            {
                add(new DropDownObject(R.drawable.online_green_dot, SANDBOX));
                add(new DropDownObject(R.drawable.online_blue_dot, LIVE));
            }
        };

        DropDownAdapter envAdapter = new DropDownAdapter(getContext(), languages);
        envAdapter.setListener(this::setEnvironment);
        ((Spinner) getView().findViewById(R.id.spEnv)).setAdapter(envAdapter);

        ((Spinner) getView().findViewById(R.id.spEnv)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DropDownObject object = (DropDownObject) envAdapter.getItem(position);
                setEnvironment(getView().findViewById(R.id.tvEnv), object, true);
                switchEnv(object.getLabel());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getView().findViewById(R.id.tvEnv).setOnClickListener(v -> getView().findViewById(R.id.spEnv).performClick());

        switchEnv(SANDBOX); //Setting the default environment

        if (mActionListener == null) return;
        getView().findViewById(R.id.tvResBtn).setOnClickListener(v -> mActionListener.move2Tab(1));
        getView().findViewById(R.id.cpbBasicKYC).setOnClickListener(v -> mActionListener.doBasicKYC());
        getView().findViewById(R.id.cpbEnhancedKYC).setOnClickListener(v -> mActionListener.doEnhancedKYC());
        getView().findViewById(R.id.cpbBiometricKYC).setOnClickListener(v -> mActionListener.doBiometricKYC());
        getView().findViewById(R.id.cpbDocV).setOnClickListener(v -> mActionListener.performDocV());
        getView().findViewById(R.id.cpbSmartSelfie).setOnClickListener(v -> mActionListener.doSmartSelfieAuth());
        getView().findViewById(R.id.tvWatchBtn).setOnClickListener(v -> linkClicked(""));
    }

    public void switchEnv(String env) {
        AppData.getInstance(getContext()).setSDKEnvir(env.equalsIgnoreCase(SANDBOX) ?
            Environment.TEST : Environment.PROD);
    }

    private void setEnvironment(TextView tvEnv, DropDownObject environment, boolean isMain) {
        tvEnv.setText(environment.getLabel());
        Drawable left = getResources().getDrawable(environment.getFlagResId());
        Drawable right = isMain ? getResources().getDrawable(R.drawable.ic_down_arrow) : null;
        tvEnv.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
    }
}
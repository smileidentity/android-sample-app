package com.demo.smileid.sid_sdk;

public class HomeFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.sid_fragment_home;
    }

    @Override
    void setupViews() {
        if (mActionListener == null) return;
        getView().findViewById(R.id.tvResBtn).setOnClickListener(v -> mActionListener.move2Tab(1));
        getView().findViewById(R.id.cpbBasicKYC).setOnClickListener(v -> mActionListener.doBasicKYC());
        getView().findViewById(R.id.cpbEnhancedKYC).setOnClickListener(v -> mActionListener.doEnhancedKYC());
        getView().findViewById(R.id.cpbBiometricKYC).setOnClickListener(v -> mActionListener.doBiometricKYC());
        getView().findViewById(R.id.cpbDocV).setOnClickListener(v -> mActionListener.performDocV());
        getView().findViewById(R.id.cpbSmartSelfie).setOnClickListener(v -> mActionListener.doSmartSelfieAuth());
    }
}
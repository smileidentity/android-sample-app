package com.demo.smileid.sid_sdk;

public class SettingsFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.sid_fragment_settings;
    }

    @Override
    void setupViews() {
        getView().findViewById(R.id.tvAboutLink).setOnClickListener(v -> mActionListener.move2Screen(AboutDemoActivity.class));
    }
}
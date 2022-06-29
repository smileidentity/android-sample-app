package com.demo.smileid.sid_sdk;

import android.app.Activity;
import android.content.Intent;

public class SettingsFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.sid_fragment_settings;
    }

    @Override
    void setupViews() {
        getView().findViewById(R.id.tvAboutLink).setOnClickListener(v -> ((Activity) getContext()).startActivity(new Intent(getActivity(), AboutDemoActivity.class)));
    }
}
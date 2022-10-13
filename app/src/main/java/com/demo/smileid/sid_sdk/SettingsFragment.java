package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.net.Uri;

public class SettingsFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.sid_fragment_settings;
    }

    @Override
    void setupViews() {
        getView().findViewById(R.id.tvAboutLink).setOnClickListener(v -> mActionListener.move2Screen(AboutDemoActivity.class));
        getView().findViewById(R.id.tvSupport).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://smileidentity.com/contact-us"))));
        getView().findViewById(R.id.tvViewWebsite).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://smileidentity.com/"))));
    }
}
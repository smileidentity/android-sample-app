package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

public class ResourcesFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.sid_fragment_resources;
    }

    @Override
    void setupViews() {
        getView().findViewById(R.id.crlDocs).setOnClickListener(new ResourceLinkClick("https://docs.smileidentity.com/"));
        getView().findViewById(R.id.crlPolicy).setOnClickListener(new ResourceLinkClick("https://smileidentity.com/privacy-policy"));
        getView().findViewById(R.id.crlFaqs).setOnClickListener(new ResourceLinkClick(""));
        getView().findViewById(R.id.crlIds).setOnClickListener(new ResourceLinkClick("https://docs.smileidentity.com/supported-id-types/for-individuals-kyc/backed-by-id-authority/supported-countries"));
    }

    private class ResourceLinkClick implements View.OnClickListener {

        private String mLink = null;

        public ResourceLinkClick(String link) {
            mLink = link;
        }

        @Override
        public void onClick(View v) {
            if (mLink == null || mLink.isEmpty()) {
                Toast.makeText(getContext(), "URL not provided", Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mLink)));
        }
    }
}
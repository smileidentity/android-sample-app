package com.demo.smileid.sid_sdk;

public class ResourcesFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.sid_fragment_resources;
    }

    @Override
    void setupViews() {
        getView().findViewById(R.id.crlDocs).setOnClickListener(v -> linkClicked("https://docs.smileidentity.com/"));
        getView().findViewById(R.id.crlPolicy).setOnClickListener(v -> linkClicked("https://smileidentity.com/privacy-policy"));
        getView().findViewById(R.id.crlFaqs).setOnClickListener(v -> linkClicked("https://docs.smileidentity.com/further-reading/faqs"));
        getView().findViewById(R.id.crlIds).setOnClickListener(v -> linkClicked("https://docs.smileidentity.com/supported-id-types/for-individuals-kyc"));
    }
}
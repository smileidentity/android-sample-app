package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected TabActionListener mActionListener = null;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    public void setListener(TabActionListener actionListener) {
        mActionListener = actionListener;
    }

    protected void linkClicked(String url) {
        if (mActionListener == null) return;
        mActionListener.openUrl(url);
    }

    abstract int getLayout();

    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupViews();
    }

    abstract void setupViews();

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onDetach() {
        super.onDetach();
    }

    public interface TabActionListener {

        void testEnroll();
        void doBasicKYC();
        void doEnhancedKYC();
        void doBiometricKYC();
        void performDocV();
        void doSmartSelfieAuth();
        void openUrl(String url);
        void move2Tab(int position);
        void move2Screen(Class classObject);
    }
}
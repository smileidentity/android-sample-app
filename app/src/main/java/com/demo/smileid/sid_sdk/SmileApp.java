package com.demo.smileid.sid_sdk;

import android.util.Log;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.smileidentity.libsmileid.utils.SIDErrorLogs;

public class SmileApp extends MultiDexApplication {

    public static FirebaseAnalytics mFirebaseAnalytics = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SMILE_IDENTITY_APP", "HERE...");
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SIDErrorLogs.init(this);
        SIDErrorLogs.getInstance().logInit();
    }
}
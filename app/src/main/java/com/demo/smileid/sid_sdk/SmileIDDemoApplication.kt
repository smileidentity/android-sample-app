package com.demo.smileid.sid_sdk

import androidx.multidex.MultiDexApplication
import com.smileidentity.libsmileid.utils.CrashReporting.enableSmileIdentityCrashReporting

class SmileIDDemoApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        enableSmileIdentityCrashReporting()
    }
}
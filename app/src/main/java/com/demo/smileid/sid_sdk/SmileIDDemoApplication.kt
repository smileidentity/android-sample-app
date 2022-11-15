package com.demo.smileid.sid_sdk

import androidx.multidex.MultiDexApplication
import com.smileid.smileidui.CrashReporting.enableSmileIdentityCrashReporting

class SmileIDDemoApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        enableSmileIdentityCrashReporting()
    }
}
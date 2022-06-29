package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class StaticPager extends ViewPager {

    public StaticPager(@NonNull Context context) {
        super(context);
    }

    public StaticPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(true);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
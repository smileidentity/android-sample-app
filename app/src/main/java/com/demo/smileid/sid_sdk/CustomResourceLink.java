package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomResourceLink extends ConstraintLayout {

    public CustomResourceLink(@NonNull Context context) {
        this(context, null);
    }

    public CustomResourceLink(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CustomResourceLink(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(attrs);
    }

    private void initialise(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_resource_link_layout, this, true);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomResourceLink,
        0, 0);

        String bigTxt = a.getString(R.styleable.CustomResourceLink_bigTxt);
        if ((bigTxt != null) && (!bigTxt.isEmpty())) {
            ((TextView) findViewById(R.id.tvBigTxt)).setText(bigTxt);
        }

        String smallTxt = a.getString(R.styleable.CustomResourceLink_smallTxt);
        if ((smallTxt != null) && (!smallTxt.isEmpty())) {
            ((TextView) findViewById(R.id.tvSmallTxt)).setText(smallTxt);
        }

        boolean lastItem = a.getBoolean(R.styleable.CustomResourceLink_lastItem, false);
        findViewById(R.id.vSeparator).setVisibility(lastItem ? GONE : VISIBLE);

        a.recycle();
    }
}
package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomProductBtn extends ConstraintLayout {

    public CustomProductBtn(@NonNull Context context) {
        this(context, null);
    }

    public CustomProductBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CustomProductBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(attrs);
    }

    private void initialise(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_product_btn_layout, this, true);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomProductBtn,
        0, 0);

        String label = a.getString(R.styleable.CustomProductBtn_label);
        if ((label != null) && (!label.isEmpty())) {
            ((TextView) findViewById(R.id.tvLabel)).setText(Html.fromHtml(label));
        }

        Drawable icon = a.getDrawable(R.styleable.CustomProductBtn_icon);
        if (icon != null) {
            ((ImageView) findViewById(R.id.ivIcon)).setImageDrawable(icon);
        }

        a.recycle();
    }
}
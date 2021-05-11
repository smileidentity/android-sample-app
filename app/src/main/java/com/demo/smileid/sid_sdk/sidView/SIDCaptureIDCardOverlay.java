package com.demo.smileid.sid_sdk.sidView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.demo.smileid.sid_sdk.R;


public class SIDCaptureIDCardOverlay extends View {

    public static final int SCREEN_HALF = 2;
    public static final float ID_RECT_AREA = 1.05f;

    double left, top, right, bottom; // rectangle bounds

    public SIDCaptureIDCardOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw light skyBlue on camera preview
        RectF outerRectangle = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // Anti alias allows for smooth corners
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorMainNavyTint));
        canvas.drawRect(outerRectangle, paint);
        // set color transparent for inside area
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // get bounds of rectangle
        double centerPointOfWidth = (float) canvas.getWidth() / SCREEN_HALF; // center of full width
        double centerPointOfHeight = (float) canvas.getHeight() / SCREEN_HALF; // center of full width

        top = centerPointOfHeight - centerPointOfHeight / ID_RECT_AREA;
        bottom = centerPointOfHeight + centerPointOfHeight / ID_RECT_AREA;
        right = centerPointOfWidth + Math.abs(top - bottom) * 1.6 / 2;
        left = centerPointOfWidth - Math.abs(top - bottom) * 1.6 / 2;
        // draw transparent rounded rect area
        RectF ovalRect = new RectF((float) left, (float) top, (float) right, (float) bottom);
        canvas.drawRoundRect(ovalRect, 20, 20, paint);//Rect drawn with x,y radius on corners
    }
}
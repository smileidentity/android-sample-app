package com.demo.smileid.sid_sdk.animation;


import androidx.annotation.AnimRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationUtil {

    public static void loadAnimation(View targetView, @AnimRes int animationResource, Animation.AnimationListener listener) {
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(targetView.getContext(), animationResource);
        targetView.startAnimation(hyperspaceJumpAnimation);
        if (listener != null)
            hyperspaceJumpAnimation.setAnimationListener(listener);
    }
}
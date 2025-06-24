package com.example.music_zengmeilian.utils;

import android.animation.ObjectAnimator;
import android.view.View;

public class AnimationUtils {

    // 简单的旋转动画
    public static ObjectAnimator createRotationAnimation(View view) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        rotation.setDuration(10000); // 10秒一圈
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        return rotation;
    }
}
package com.feibo.joke.view.util;

import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.feibo.joke.R;

/**
 * Create by：ml_bright on 2015/7/14 09:57
 * Email: 2504509903@qq.com
 */
public class AnimUtil {

    private static int[] getViewOffset(View v) {
        int[] p = new int[2];
        View t = v;
        while (t.getId() != Window.ID_ANDROID_CONTENT) {
            p[0] += t.getLeft();
            p[1] += t.getTop();
            ViewParent parent = t.getParent();
            if (parent == null || !(t instanceof View)) {
                break;
            } else {
                t = (View) parent;
            }
        }
        return p;
    }

    public static void scaleAnim(final View iconButton) {
        Animation btnAnim = AnimationUtils.loadAnimation(iconButton.getContext(), R.anim.anim_scale);
        btnAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                iconButton.setTextColor(isLiked ? selectTextColor : normalTextColor);
//                iconButton.setIconResource(isLiked ? selectDrawable : normalDrawable);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iconButton.startAnimation(btnAnim);
    }



}

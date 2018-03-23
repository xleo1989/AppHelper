package com.x.leo.apphelper.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    private static HashMap<Activity, ViewTreeObserver.OnGlobalLayoutListener> listenerHashMap = new HashMap<>();

    public static void assistActivity (@NotNull Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    public static void deassistActivity(@NotNull Activity activity){
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = listenerHashMap.get(activity);
        if (onGlobalLayoutListener == null) {
            return;
        }
        FrameLayout content = activity.findViewById(android.R.id.content);
        content.getChildAt(0).getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        listenerHashMap.remove(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity) {
        FrameLayout content =  activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        if (mChildOfContent == null) {
            return;
        }
        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        };
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        listenerHashMap.put(activity, listener);
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }
    private ValueAnimator animator;
    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                if (animator != null) {
                    animator.cancel();
                }
                animator = new ValueAnimator();
                animator.setDuration(200);
                animator.setIntValues(usableHeightSansKeyboard,usableHeightSansKeyboard - heightDifference);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int animatedValue = (int) animation.getAnimatedValue();
                        frameLayoutParams.height = animatedValue;
                        mChildOfContent.requestLayout();
                    }
                }) ;
                animator.start();
            } else {
                // keyboard probably just became hidden
                if (animator != null) {
                    animator.cancel();
                }
                frameLayoutParams.height = usableHeightSansKeyboard;
                mChildOfContent.requestLayout();
            }
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        if(Build.VERSION.SDK_INT >= 21) {
            return r.bottom;
        }else {
            return (r.bottom - r.top);// 全屏模式下： return r.bottom
        }
    }

}
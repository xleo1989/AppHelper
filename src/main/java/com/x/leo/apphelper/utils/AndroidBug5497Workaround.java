package com.x.leo.apphelper.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.x.leo.apphelper.log.XLog;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    private static HashMap<Activity, ViewTreeObserver.OnGlobalLayoutListener> listenerHashMap = new HashMap<>();

    public static void assistActivity(@NotNull Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    public static void deassistActivity(@NotNull Activity activity) {
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
    private int navigationBarHeight;
    private int unusableHeight;
    private int lStatusBarHeight;

    private AndroidBug5497Workaround(final Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        if (mChildOfContent == null) {
            return;
        }
        navigationBarHeight = getNavigationBarHeight(activity);
        int screenHeight = getScreenHeight(activity);
        int usableHeight = computeUsableHeight();
        if (screenHeight - usableHeight < screenHeight / 4) {
            if (Build.VERSION.SDK_INT < 21) {
                unusableHeight = screenHeight - usableHeight - lStatusBarHeight - navigationBarHeight;
            } else {
                unusableHeight = screenHeight - usableHeight - navigationBarHeight;
            }
        }
        XLog.INSTANCE.i(10, "View height detail,navigationBarHeight:%d ,lStatusBarHeight: %d ,usableHeight: %d , unusableHeight: %d , screenHeight :  %d",
                navigationBarHeight, lStatusBarHeight, usableHeight, unusableHeight, screenHeight);

        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        };
        mChildOfContent.getViewTreeObserver().

                addOnGlobalLayoutListener(listener);
        listenerHashMap.put(activity, listener);
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private ValueAnimator animator;

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                if (animator != null) {
                    animator.cancel();
                }
                animator = new ValueAnimator();
                animator.setDuration(250);
                animator.setIntValues(usableHeightSansKeyboard, usableHeightSansKeyboard - heightDifference);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        frameLayoutParams.height = (int) animation.getAnimatedValue();
                        mChildOfContent.requestLayout();
                    }
                });
                animator.start();
            } else {
                // keyboard probably just became hidden
                if (animator != null) {
                    animator.cancel();
                }
                if (Build.VERSION.SDK_INT < 21) {
                    frameLayoutParams.height = usableHeightSansKeyboard - navigationBarHeight - lStatusBarHeight;
                } else {
                    frameLayoutParams.height = usableHeightSansKeyboard - navigationBarHeight;
                }
                mChildOfContent.requestLayout();
            }
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        if (lStatusBarHeight != r.top) {
            lStatusBarHeight = r.top;
        }
        if (Build.VERSION.SDK_INT >= 21) {
//            XLog.INSTANCE.i(10, "full screen mode:%d,usable height: %d", r.top, r.bottom);
            return r.bottom;
        } else {
//            XLog.INSTANCE.i(10, "not full screen mode:%d,usable height: %d", r.top, r.bottom - r.top);
            return (r.bottom - r.top);// 全屏模式下： return r.bottom
        }
    }


    private boolean isNavigationBarShow(@NotNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    private int getNavigationBarHeight(@NotNull Activity activity) {
        if (!isNavigationBarShow(activity)) {
//            XLog.INSTANCE.i(10, "navigation bar shown:false");
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int dimensionPixelSize = resources.getDimensionPixelSize(resourceId);
//        XLog.INSTANCE.i(10, "navigation bar height: %d", dimensionPixelSize);
        return dimensionPixelSize;
    }


    public int getScreenHeight(@NotNull Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight() + navigationBarHeight;
    }

}
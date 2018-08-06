package com.x.leo.apphelper.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView
import com.x.leo.apphelper.log.xlog.XLog
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout


/**
 * Created by XLEO on 2017/11/28.
 * may produce black screen area
 */
@Deprecated("may produce black screen area")
class SoftSpanPopEditText {
    private var movedDistance: Int = 0
    private var currentListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    fun register(currentView: View, scrollRes: Int) {
        init(currentView, scrollRes)
        currentView.viewTreeObserver.addOnGlobalLayoutListener(currentListener)
    }

    fun unRegister(currentView: View) {
        val im = currentView.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(currentView.windowToken,0)
//        currentView.viewTreeObserver.removeOnGlobalLayoutListener(currentListener)
    }

    private fun init(currentView: View, scrollRes: Int) {
        currentListener = ViewTreeObserver.OnGlobalLayoutListener {
//            method1(currentView, scrollRes)
            method2(currentView)
        }
    }
    private var usableHeightPrevious = 0
    private var animator:ValueAnimator? = null
    private fun method2(currentView: View) {
        val content = currentView.rootView.findViewById<FrameLayout>(android.R.id.content)
        val currentParent = content.getChildAt(0)!!
        val layoutParams = currentParent.layoutParams as FrameLayout.LayoutParams
        val usableHeightNow = computerUsableHeight(currentParent)
        val usableHeightSansKeyboard = currentParent.rootView.height
        if(usableHeightNow != usableHeightPrevious){
            val heightDiff = usableHeightSansKeyboard - usableHeightNow
            if(heightDiff > usableHeightSansKeyboard / 4){
                animator?.cancel()
                animator = ValueAnimator()
                animator!!.duration = 200
                animator!!.setIntValues(usableHeightSansKeyboard,usableHeightSansKeyboard - heightDiff)
                animator!!.addUpdateListener {
                    val animatedValue = it.animatedValue as Int
                    layoutParams.height = animatedValue
                    currentParent.requestLayout()
                }
                animator!!.start()

            }else{
                animator?.cancel()
                layoutParams.height = usableHeightSansKeyboard
                currentParent.requestLayout()
            }
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computerUsableHeight(currentParent: View): Int {
        val r = Rect()
        currentParent.getWindowVisibleDisplayFrame(r)
        return if(Build.VERSION.SDK_INT >= 21) {
            r.bottom
        }else {
            r.bottom - r.top
        }
    }

    @Deprecated("poor effect",ReplaceWith("method2"))
    private fun method1(currentView: View, scrollRes: Int) {
        val r = Rect()
        currentView.getWindowVisibleDisplayFrame(r)
        if (currentView.rootView.height - r.bottom > currentView.resources.displayMetrics.heightPixels * 0.2) {
            val vr = Rect()
            currentView.getGlobalVisibleRect(vr)
            if (vr.bottom > r.bottom) {
                val scrolledHeight = vr.bottom - r.bottom
                if (scrollRes != -1) {
                    try {
                        val find = currentView.rootView.findViewById<ScrollView>(scrollRes)
                        val childAt = find.getChildAt(0)
                        if (childAt != null) {
                            if (childAt.height + find.paddingTop + find.paddingBottom + childAt.top - find.height > scrolledHeight) {
                                find.smoothScrollBy(0, scrolledHeight)
                                movedDistance += scrolledHeight
                            } else
                                currentView.rootView.scrollTo(0, scrolledHeight)
                        } else {
                            currentView.rootView.scrollTo(0, scrolledHeight)
                        }

                    } catch (e: Throwable) {
                        XLog.e("catch exception:", e, 100)
                    }
                } else {
                    currentView.rootView.scrollTo(0, scrolledHeight)
                }
            }
        } else {
            currentView.clearFocus()
            if (scrollRes != -1) {
                try {
                    val find = currentView.rootView.findViewById<ScrollView>(scrollRes)
                    val childAt = find.getChildAt(0)
                    if (childAt != null) {
                        val rect = Rect()
                        val offset = Point()
                        childAt.getGlobalVisibleRect(rect, offset)
                        if (rect.top - offset.y >= movedDistance && movedDistance != 0)
                            find.smoothScrollBy(0, -movedDistance)
                    }
                } catch (e: Throwable) {
                    XLog.e("catch exception:", e, 100)
                }
            }
            currentView.rootView.scrollTo(0, 0)
            movedDistance = 0
        }
    }

}